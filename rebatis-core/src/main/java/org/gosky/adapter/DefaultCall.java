package org.gosky.adapter;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import java.util.concurrent.CompletableFuture;

/**
 * @Auther: guozhong
 * @Date: 2019-04-24 16:29
 * @Description:
 */
public class DefaultCall<T> implements Call<T> {

    private Future<T> future;

    public DefaultCall(Future<T> future) {
        this.future = future;
    }

    @Override
    public void enqueue(Callback<T> callback) {
        future
                .onSuccess(callback::onResponse)
                .onFailure(new Handler<Throwable>() {
                    @Override
                    public void handle(Throwable event) {
                        callback.onFailure(event);
                    }
                });
    }

    @Override
    public void onComplete(Handler<AsyncResult<T>> handler) {
        future.onComplete(handler);
    }

    @SuppressWarnings("CloneDoesntCallSuperClone") // We are a final type & this saves clearing state.
    @Override
    public DefaultCall<T> clone() {
        return new DefaultCall<>(future);
    }

//    @Override
//    public void cancel() {
//        future.cancel(false);
//    }
//
//    @Override
//    public boolean isCanceled() {
//
//        return future.isCancelled();
//    }
}
