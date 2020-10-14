package org.gosky.mapping;

import io.vertx.core.Future;
import io.vertx.mysqlclient.MySQLException;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.reflection.MetaObject;
import org.gosky.Rebatis;
import org.gosky.adapter.CallAdapter;
import org.gosky.adapter.DefaultCall;
import org.gosky.basemapper.EntityHelper;
import org.gosky.basemapper.ProviderUtil;
import org.gosky.common.ReturnTypeEnum;
import org.gosky.common.SQLType;
import org.gosky.converter.ConverterFactory;
import org.gosky.converter.ConverterUtil;
import org.gosky.executor.Executor;
import org.gosky.parsing.ParamNameResolver;
import org.gosky.parsing.ParseSqlResult;
import org.gosky.parsing.Parser;
import org.gosky.util.TypeConstants;
import org.gosky.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 缓存每个method 解析后的信息
 */
public class ServiceMethod {
    private final Logger logger = LoggerFactory.getLogger(ServiceMethod.class);
    private final SqlFactory sqlFactory;
    private final Executor executor;
    private ConverterFactory converterFactory;
    private final CallAdapter<?, ?> callAdapter;
    private final Class<?> mapper;

    public ServiceMethod(SqlFactory sqlFactory, Executor executor, ConverterFactory converterFactory, CallAdapter<?, ?> callAdapter, Class<?> mapper) {
        this.sqlFactory = sqlFactory;
        this.executor = executor;
        this.converterFactory = converterFactory;
        this.callAdapter = callAdapter;
        this.mapper = mapper;
    }

    public static ServiceMethod parseAnnotations(Rebatis rebatis, Class<?> mapper, Method method) {

        Annotation[] annotations = method.getDeclaredAnnotations();
        //构建方法SQL映射
        SqlFactory sqlFactory = new SqlFactory();
        //获取SQL类型
        SQLType sqlType = SQLType.UNKNOWN;
        String sql = null;
        boolean isBaseMethod = false;
        if (annotations[0] instanceof Insert) {
            Insert anno = (Insert) annotations[0];
            sql = anno.value()[0];
            sqlType = SQLType.INSERT;
        } else if (annotations[0] instanceof Delete) {
            Delete anno = (Delete) annotations[0];
            sql = anno.value()[0];
            sqlType = SQLType.DELETE;
        } else if (annotations[0] instanceof Update) {
            Update anno = (Update) annotations[0];
            sql = anno.value()[0];
            sqlType = SQLType.UPDATE;
        } else if (annotations[0] instanceof Select) {
            Select anno = (Select) annotations[0];
            sql = anno.value()[0];
            sqlType = SQLType.SELECT;
        } else if (annotations[0] instanceof SelectProvider) {
            SelectProvider anno = (SelectProvider) annotations[0];
            sqlFactory.setProviderClass(anno.value());
            String providerMethodName = anno.method();
            Method providerMethod = ProviderUtil.resolveMethod(sqlFactory.getProviderClass(), providerMethodName);
            sqlFactory.setProviderMethod(providerMethod);
//            sql = invokeProviderMethod(providerClass, providerMethod, mapper);
            sqlType = SQLType.SELECT;
            isBaseMethod = true;
        } else if (annotations[0] instanceof InsertProvider) {
            InsertProvider anno = (InsertProvider) annotations[0];
            sqlFactory.setProviderClass(anno.value());
            String providerMethodName = anno.method();
            Method providerMethod = ProviderUtil.resolveMethod(sqlFactory.getProviderClass(), providerMethodName);
            sqlFactory.setProviderMethod(providerMethod);
//            sql = invokeProviderMethod(providerClass, providerMethod, mapper);
            sqlType = SQLType.INSERT;
            isBaseMethod = true;
        }
        if (annotations[0] instanceof UpdateProvider) {
            UpdateProvider anno = (UpdateProvider) annotations[0];
            sqlFactory.setProviderClass(anno.value());
            String providerMethodName = anno.method();
            Method providerMethod = ProviderUtil.resolveMethod(sqlFactory.getProviderClass(), providerMethodName);
            sqlFactory.setProviderMethod(providerMethod);
//            sql = invokeProviderMethod(providerClass, providerMethod, mapper);
            sqlType = SQLType.UPDATE;
            isBaseMethod = true;
        }

        //泛型中的类型 eg: Call<User>
        Type dataContainerType = Utils.getParameterUpperBound(0, ((ParameterizedType) method.getGenericReturnType()));

        if (isBaseMethod) {
            if (dataContainerType instanceof ParameterizedType) {
                dataContainerType = ParameterizedTypeImpl.make((Class) (((ParameterizedType) dataContainerType).getRawType()), new Type[]{EntityHelper.getEntityClass(mapper)}, null);
            } else {
                dataContainerType = ParameterizedTypeImpl.make(dataContainerType.getClass(), new Type[]{EntityHelper.getEntityClass(mapper)}, null);
            }
        }

        // 获取返回值类型
        ReturnTypeEnum returnTypeEnum;
        if (dataContainerType.equals(List.class) ||
                (dataContainerType instanceof ParameterizedType && ((ParameterizedType) dataContainerType).getRawType().equals(List.class))) {
            returnTypeEnum = ReturnTypeEnum.LIST;
        } else if (dataContainerType.equals(Map.class) ||
                (dataContainerType instanceof ParameterizedType && ((ParameterizedType) dataContainerType).getRawType().equals(Map.class))) {
            returnTypeEnum = ReturnTypeEnum.MAP;
        } else if (dataContainerType instanceof Class && dataContainerType.equals(Void.class)) {
            returnTypeEnum = ReturnTypeEnum.VOID;
        } else {
            returnTypeEnum = ReturnTypeEnum.SINGLE;
        }


        sqlFactory.setMethodName(method.getName());
        sqlFactory.setSql(sql);
        sqlFactory.setSqlType(sqlType);
        sqlFactory.setReturnType(method.getGenericReturnType());
        sqlFactory.setResponseType(dataContainerType);
        sqlFactory.setParameterTypes(method.getParameterTypes());
        sqlFactory.setReturnTypeEnum(returnTypeEnum);
        sqlFactory.setMethod(method);
        sqlFactory.setBaseMethod(isBaseMethod);


        return new ServiceMethod(sqlFactory, rebatis.executor, rebatis.converterFactory,
                rebatis.callAdapter(method.getGenericReturnType(), method.getAnnotations()), mapper);
    }

    /**
     * 解析sql并执行
     *
     * @param args service方法中的参数
     * @return
     */
    public Object invoke(Object[] args) {
        ParseSqlResult sqlResult;
        if (args == null || args.length == 0) {
            sqlResult = new ParseSqlResult(sqlFactory.getSql());
        } else {
            //预解析参数
            //1.解析方法参数
            ParamNameResolver paramNameResolver = new ParamNameResolver(sqlFactory.getMethod());
            Object parameter = paramNameResolver.getNamedParams(args);
            //是否是单参数java基础类型
            boolean isSimpleType = TypeConstants.typeList.contains(parameter.getClass());
            MetaObject metaObject = null;
            if (!isSimpleType) {
                metaObject = MetaObject.forObject(parameter);
            }

            //判断是否是baseMapper的method
            if (sqlFactory.isBaseMethod()) {
                //调用SqlProvider的方法获取sql
                String sql = ProviderUtil.invokeProviderMethod(sqlFactory.getProviderClass(), sqlFactory.getProviderMethod(), mapper, metaObject);
                logger.info("base mapper sql={}", sql);
                sqlFactory.setSql(sql);
            }

            //解析sql
            sqlResult = Parser.parse(sqlFactory.getSql(), sqlFactory.getMethod(), args, isSimpleType, parameter, metaObject);
        }

        long start = System.currentTimeMillis();
        Future<Object> future = executor.query(sqlResult.getSql(), sqlResult.getValues()).map(rowSet -> convert(sqlFactory, rowSet));
        future.onComplete(o -> {
            logger.info("run sql={}, params={}, duration={}, result={}", sqlResult.getSql(),
                    sqlResult.getValues(), System.currentTimeMillis() - start, o);
            if (o.failed()) {
                Throwable cause = o.cause();
                if (cause instanceof MySQLException && cause.getMessage() != null && cause.getMessage().contains("Duplicate entry")){
                    logger.error("run sql={}, params={}, duration={}, msg={}", sqlResult.getSql(),
                            sqlResult.getValues(), System.currentTimeMillis() - start, cause.getMessage());
                } else {
                    logger.error("run sql={}, params={}, duration={}", sqlResult.getSql(),
                            sqlResult.getValues(), System.currentTimeMillis() - start, cause);
                }
            }

        });

        //call只有一个就是defaultCall 返回的不同的那个是CallAdapter
        return callAdapter.adapt(new DefaultCall(future));

    }

    private Object convert(SqlFactory sqlFactory, RowSet<Row> queryResult) {
        return ConverterUtil.with().convert(sqlFactory, queryResult, this.sqlFactory.getReturnTypeEnum()
                , this.sqlFactory.getResponseType());
    }


}