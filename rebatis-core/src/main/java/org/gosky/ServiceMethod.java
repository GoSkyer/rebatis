package org.gosky;

import org.jetbrains.annotations.Nullable;

/**
 * @Auther: guozhong
 * @Date: 2019-03-28 18:17
 * @Description:
 */
abstract class ServiceMethod<T> {
//    static <T> ServiceMethod<T> parseAnnotations(Retrofit retrofit, Method method) {
//        RequestFactory requestFactory = RequestFactory.parseAnnotations(retrofit, method);
//
//        Type returnType = method.getGenericReturnType();
//        if (Utils.hasUnresolvableType(returnType)) {
//            throw methodError(method,
//                    "Method return type must not include a type variable or wildcard: %s", returnType);
//        }
//        if (returnType == void.class) {
//            throw methodError(method, "Service methods cannot return void.");
//        }
//
//        return HttpServiceMethod.parseAnnotations(retrofit, method, requestFactory);
//    }

    abstract @Nullable
    T invoke(Object[] args);
}