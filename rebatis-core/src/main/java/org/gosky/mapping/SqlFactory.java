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
    //是否是baseMapper的方法
    private boolean isBaseMethod = false;
    private Class<?> providerClass;
    private Method providerMethod;


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

    public boolean isBaseMethod() {
        return isBaseMethod;
    }

    public void setBaseMethod(boolean baseMethod) {
        isBaseMethod = baseMethod;
    }

    public Class<?> getProviderClass() {
        return providerClass;
    }

    public void setProviderClass(Class<?> providerClass) {
        this.providerClass = providerClass;
    }

    public Method getProviderMethod() {
        return providerMethod;
    }

    public void setProviderMethod(Method providerMethod) {
        this.providerMethod = providerMethod;
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
                ", isBaseMethod=" + isBaseMethod +
                ", providerClass=" + providerClass +
                ", providerMethod=" + providerMethod +
                '}';
    }
}
