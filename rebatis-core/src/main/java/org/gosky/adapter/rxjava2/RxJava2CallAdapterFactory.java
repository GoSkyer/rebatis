package org.gosky.adapter.rxjava2;

import org.gosky.adapter.CallAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * @Auther: guozhong
 * @Date: 2019-04-25 12:19
 * @Description:
 */
public final class RxJava2CallAdapterFactory extends CallAdapter.Factory {
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations) {
        Class<?> rawType = getRawType(returnType);

        boolean isFlowable = rawType == Flowable.class;
        boolean isSingle = rawType == Single.class;
        boolean isMaybe = rawType == Maybe.class;

        if (rawType != Observable.class && !isFlowable && !isSingle && !isMaybe) {
            return null;
        }

        if (!(returnType instanceof ParameterizedType)) {
            String name = isFlowable ? "Flowable"
                    : isSingle ? "Single"
                    : isMaybe ? "Maybe" : "Observable";
            throw new IllegalStateException(name + " return type must be parameterized"
                    + " as " + name + "<Foo> or " + name + "<? extends Foo>");
        }

        Type responseType = getParameterUpperBound(0, (ParameterizedType) returnType);


        return new RxJava2Adapter(responseType, isFlowable, isSingle, isMaybe);
    }
}
