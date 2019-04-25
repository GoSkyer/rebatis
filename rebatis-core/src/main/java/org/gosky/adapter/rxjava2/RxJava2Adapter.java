package org.gosky.adapter.rxjava2;

import org.gosky.adapter.Call;
import org.gosky.adapter.CallAdapter;

import java.lang.reflect.Type;

import io.reactivex.BackpressureStrategy;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * @Auther: guozhong
 * @Date: 2019-04-25 12:13
 * @Description:
 */
public final class RxJava2Adapter implements CallAdapter {
    private final Type responseType;
    private final boolean isFlowable;
    private final boolean isSingle;
    private final boolean isMaybe;

    public RxJava2Adapter(Type responseType, boolean isFlowable, boolean isSingle, boolean isMaybe) {
        this.responseType = responseType;
        this.isFlowable = isFlowable;
        this.isSingle = isSingle;
        this.isMaybe = isMaybe;
    }


    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public Object adapt(Call call) {
        CallEnqueueObservable observable = new CallEnqueueObservable<>(call);
        if (isFlowable) {
            return observable.toFlowable(BackpressureStrategy.LATEST);
        }
        if (isSingle) {
            return observable.singleOrError();
        }
        if (isMaybe) {
            return observable.singleElement();
        }
        return RxJavaPlugins.onAssembly(observable);
    }
}
