package org.gosky.adapter;

import java.util.concurrent.CompletableFuture;

/**
 * @Auther: guozhong
 * @Date: 2019-04-24 16:29
 * @Description:
 */
public class DefaultCall<T> implements Call<T> {

    private CompletableFuture<T> future;

    public DefaultCall(CompletableFuture<T> future) {
        this.future = future;
    }

    @Override
    public void enqueue(Callback<T> callback) {
        future.thenAccept(callback::onResponse)
                .exceptionally(throwable -> {
                    callback.onFailure(throwable);
                    return null;
                });
    }

    @SuppressWarnings("CloneDoesntCallSuperClone") // We are a final type & this saves clearing state.
    @Override public DefaultCall<T> clone() {
        return new DefaultCall<>(future);
    }

    @Override
    public void cancel() {
        future.cancel(false);
    }

    @Override
    public boolean isCanceled() {
        return future.isCancelled();
    }
}
