package org.gosky.adapter;

import org.gosky.util.Utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @Auther: guozhong
 * @Date: 2019-04-25 11:33
 * @Description:
 */
public class DefaultCallAdapterFactory extends CallAdapter.Factory {
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations) {
        if (getRawType(returnType) != Call.class) {
            return null;
        }

        final Type responseType = Utils.getCallResponseType(returnType);

        return new CallAdapter<Object, Call<?>>() {
            @Override public Type responseType() {
                return responseType;
            }

            @Override public Call<Object> adapt(Call<Object> call) {
                return call;
            }
        };
    }
}
