package org.gosky.mapping;

import org.gosky.common.ReturnTypeEnum;
import org.gosky.common.SQLType;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;


/**
 * 具体方法映射
 */
public class SqlFactory {
    private String methodName;
    private String sql;
    private SQLType sqlType;
    private Type returnType;
    //    private Type adapterType;
    private Type responseType;
    private Class<?>[] parameterTypes;
    private ReturnTypeEnum returnTypeEnum;

    //
    private Method method;


    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public SQLType getSqlType() {
        return sqlType;
    }

    public void setSqlType(SQLType sqlType) {
        this.sqlType = sqlType;
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public Type getResponseType() {
        return responseType;
    }

    public void setResponseType(Type responseType) {
        this.responseType = responseType;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public ReturnTypeEnum getReturnTypeEnum() {
        return returnTypeEnum;
    }

    public void setReturnTypeEnum(ReturnTypeEnum returnTypeEnum) {
        this.returnTypeEnum = returnTypeEnum;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }



    @Override
    public String toString() {
        return "SqlFactory{" +
                "methodName='" + methodName + '\'' +
                ", sql='" + sql + '\'' +
                ", sqlType=" + sqlType +
                ", returnType=" + returnType +
                ", responseType=" + responseType +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", returnTypeEnum=" + returnTypeEnum +
                ", method=" + method +
                '}';
    }

    public static final class SqlFactoryBuilder {
        private String methodName;
        private String sql;
        private SQLType sqlType;
        private Type returnType;
        //    private Type adapterType;
        private Type responseType;
        private Class<?>[] parameterTypes;
        private ReturnTypeEnum returnTypeEnum;
        //
        private Method method;

        private SqlFactoryBuilder() {
        }

        public static SqlFactoryBuilder aSqlFactory() {
            return new SqlFactoryBuilder();
        }

        public SqlFactoryBuilder methodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public SqlFactoryBuilder sql(String sql) {
            this.sql = sql;
            return this;
        }

        public SqlFactoryBuilder sqlType(SQLType sqlType) {
            this.sqlType = sqlType;
            return this;
        }

        public SqlFactoryBuilder returnType(Type returnType) {
            this.returnType = returnType;
            return this;
        }

        public SqlFactoryBuilder responseType(Type responseType) {
            this.responseType = responseType;
            return this;
        }

        public SqlFactoryBuilder parameterTypes(Class<?>[] parameterTypes) {
            this.parameterTypes = parameterTypes;
            return this;
        }

        public SqlFactoryBuilder returnTypeEnum(ReturnTypeEnum returnTypeEnum) {
            this.returnTypeEnum = returnTypeEnum;
            return this;
        }

        public SqlFactoryBuilder method(Method method) {
            this.method = method;
            return this;
        }

        public SqlFactory build() {
            SqlFactory sqlFactory = new SqlFactory();
            sqlFactory.setMethodName(methodName);
            sqlFactory.setSql(sql);
            sqlFactory.setSqlType(sqlType);
            sqlFactory.setReturnType(returnType);
            sqlFactory.setResponseType(responseType);
            sqlFactory.setParameterTypes(parameterTypes);
            sqlFactory.setReturnTypeEnum(returnTypeEnum);
            sqlFactory.setMethod(method);
            return sqlFactory;
        }
    }
}
