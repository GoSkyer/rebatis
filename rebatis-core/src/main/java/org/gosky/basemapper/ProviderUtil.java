package org.gosky.basemapper;

import org.apache.ibatis.reflection.MetaObject;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProviderUtil {
    /**
     * 解析provider
     *
     * @return
     */
    public static Method resolveMethod(Class<?> providerClass, String providerMethod) {
        List<Method> sameNameMethods = Arrays.stream(providerClass.getMethods())
                .filter(m -> m.getName().equals(providerMethod))
                .collect(Collectors.toList());
        if (sameNameMethods.isEmpty()) {
            throw new RuntimeException("Cannot resolve the provider method because '"
                    + providerMethod + "' not found in SqlProvider '" + providerClass.getName() + "'.");
        }
        List<Method> targetMethods = sameNameMethods.stream()
                .filter(m -> CharSequence.class.isAssignableFrom(m.getReturnType()))
                .collect(Collectors.toList());
        if (targetMethods.size() == 1) {
            return targetMethods.get(0);
        }
        if (targetMethods.isEmpty()) {
            throw new RuntimeException("Cannot resolve the provider method because '"
                    + providerMethod + "' does not return the CharSequence or its subclass in SqlProvider '"
                    + providerClass.getName() + "'.");
        } else {
            throw new RuntimeException("Cannot resolve the provider method because '"
                    + providerMethod + "' is found multiple in SqlProvider '" + providerClass.getName() + "'.");
        }
    }


    public static String invokeProviderMethod(Class<?> providerClass, Method method, Class<?> mapperClass, MetaObject metaObject) {
        CharSequence sql;
        try {
            Object targetObject = null;
            if (!Modifier.isStatic(method.getModifiers())) {
                targetObject = providerClass.getDeclaredConstructor().newInstance();
            }
            sql = (CharSequence) method.invoke(targetObject, mapperClass, metaObject);
        } catch (Exception e) {
            throw new RuntimeException("Error invoking SqlProvider method '" + method
                    + "' with specify parameter '" + (mapperClass == null ? null : mapperClass.getClass()) + "'.  Cause: " + e, e);
        }
        return sql != null ? sql.toString() : null;
    }
}
