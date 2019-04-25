package org.gosky.adapter.rxjava2;

import org.gosky.adapter.Call;
import org.gosky.adapter.CallAdapter;

import java.lang.reflect.Type;

import io.reactivex.plugins.RxJavaPlugins;

/**
 * @Auther: guozhong
 * @Date: 2019-04-25 12:13
 * @Description:
 */
public final class RxJava2Adapter implements CallAdapter {
    private final Type responseType;

    public RxJava2Adapter(Type responseType) {
        this.responseType = responseType;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public Object adapt(Call call) {
        CallEnqueueObservable callEnqueueObservable = new CallEnqueueObservable<>(call);

        return RxJavaPlugins.onAssembly(callEnqueueObservable);
    }
}
