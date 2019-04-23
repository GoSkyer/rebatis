package org.gosky.mapping;

import com.github.jasync.sql.db.QueryResult;

import org.gosky.Rebatis;
import org.gosky.annotations.Delete;
import org.gosky.annotations.Insert;
import org.gosky.annotations.Select;
import org.gosky.annotations.Update;
import org.gosky.common.ReturnTypeEnum;
import org.gosky.common.SQLType;
import org.gosky.converter.ConverterFactory;
import org.gosky.converter.ConverterUtil;
import org.gosky.executor.Executor;
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

    public ServiceMethod(SqlFactory sqlFactory, Executor executor, ConverterFactory converterFactory) {
        this.sqlFactory = sqlFactory;
        this.executor = executor;
        this.converterFactory = converterFactory;
    }

    public static ServiceMethod parseAnnotations(Rebatis rebatis, Method method) {

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
        Type dataContinerType = Utils.getParameterUpperBound(0, ((ParameterizedType) method.getGenericReturnType()));
        // 获取返回值类型
        ReturnTypeEnum returnTypeEnum;
        if (dataContinerType.equals(List.class) ||
                (dataContinerType instanceof ParameterizedType && ((ParameterizedType) dataContinerType).getRawType().equals(List.class))) {
            returnTypeEnum = ReturnTypeEnum.LIST;
        } else if (dataContinerType.equals(Map.class) ||
                (dataContinerType instanceof ParameterizedType && ((ParameterizedType) dataContinerType).getRawType().equals(Map.class))) {
            returnTypeEnum = ReturnTypeEnum.MAP;
        } else if (dataContinerType instanceof Class && dataContinerType.equals(Void.class)) {
            returnTypeEnum = ReturnTypeEnum.VOID;
        } else {
            returnTypeEnum = ReturnTypeEnum.SINGLE;
        }

        //构建方法SQL映射
        SqlFactory sqlFactory = SqlFactory.builder().methodName(method.getName())
                .returnType(method.getGenericReturnType())
                .responseType(dataContinerType)
                .returnType(method.getGenericReturnType())
                .returnTypeEnum(returnTypeEnum)
                .parameterTypes(method.getParameterTypes())
                .sql(value[0])
                .sqlType(sqlType)
                .build();

        return new ServiceMethod(sqlFactory, rebatis.executor, rebatis.converterFactory);
    }

    public CompletableFuture<Object> invoke(Object[] args) {
        CompletableFuture<QueryResult> query = executor.query(sqlFactory.getSql(), "");

        return query.thenApply(queryResult -> {
            try {
                return ConverterUtil.with(converterFactory).convert(queryResult, sqlFactory.getReturnTypeEnum()
                        , sqlFactory.getResponseType());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}