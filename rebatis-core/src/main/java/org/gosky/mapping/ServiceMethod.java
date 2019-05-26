package org.gosky.mapping;

import com.github.jasync.sql.db.QueryResult;

import org.gosky.Rebatis;
import org.gosky.adapter.CallAdapter;
import org.gosky.adapter.DefaultCall;
import org.gosky.annotations.Delete;
import org.gosky.annotations.Insert;
import org.gosky.annotations.Select;
import org.gosky.annotations.Update;
import org.gosky.common.ReturnTypeEnum;
import org.gosky.common.SQLType;
import org.gosky.converter.ConverterFactory;
import org.gosky.converter.ConverterUtil;
import org.gosky.executor.Executor;
import org.gosky.parsing.ParseSqlResult;
import org.gosky.parsing.Parser;
import org.gosky.util.Utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @Auther: guozhong
 * @Date: 2019-03-28 18:17
 * @Description:
 */
public class ServiceMethod<T> {

    private SqlFactory sqlFactory;
    private Executor executor;
    private ConverterFactory converterFactory;
    private final CallAdapter callAdapter;

    public ServiceMethod(SqlFactory sqlFactory, Executor executor, ConverterFactory converterFactory, CallAdapter callAdapter) {
        this.sqlFactory = sqlFactory;
        this.executor = executor;
        this.converterFactory = converterFactory;
        this.callAdapter = callAdapter;
    }

    public static <T> ServiceMethod<T> parseAnnotations(Rebatis rebatis, Method method) {

        Annotation[] annotations = method.getDeclaredAnnotations();
        String simpleName = annotations[0].annotationType().getSimpleName();
        //获取SQL类型
        SQLType sqlType = SQLType.covertToSQLType(simpleName);
        String[] value = {};
        switch (sqlType) {
            case INSERT:
                Insert inserts = (Insert) annotations[0];
                value = inserts.value();
                break;
            case DELETE:
                Delete delete = (Delete) annotations[0];
                value = delete.value();
                break;
            case UPDATE:
                Update update = (Update) annotations[0];
                value = update.value();
                break;
            case SELECT:
                Select select = (Select) annotations[0];
                value = select.value();
                break;
            default:
                break;
        }

        //泛型中的类型 eg: List<String>
        Type dataContainerType = Utils.getParameterUpperBound(0, ((ParameterizedType) method.getGenericReturnType()));

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

        //构建方法SQL映射
        SqlFactory sqlFactory = SqlFactory.builder().methodName(method.getName())
                .method(method)
                .returnType(method.getGenericReturnType())
                .responseType(dataContainerType)
                .returnType(method.getGenericReturnType())
                .returnTypeEnum(returnTypeEnum)
                .parameterTypes(method.getParameterTypes())
                .sql(value[0])
                .sqlType(sqlType)
                .build();

        return new ServiceMethod(sqlFactory, rebatis.executor, rebatis.converterFactory,
                rebatis.callAdapter(method.getGenericReturnType(), method.getAnnotations()));
    }

    public Object invoke(Object[] args) throws Exception {

        ParseSqlResult sqlResult = Parser.parse(sqlFactory.getSql(), sqlFactory.getMethod(), args);

        CompletableFuture<Object> future = executor.query(sqlResult.getSql(), sqlResult.getValues()).thenApply(queryResult -> convert(queryResult));

        return callAdapter.adapt(new DefaultCall(future));

    }

    private Object convert(QueryResult queryResult) {
        return ConverterUtil.with(converterFactory).convert(queryResult, sqlFactory.getReturnTypeEnum()
                , sqlFactory.getResponseType());
    }
}