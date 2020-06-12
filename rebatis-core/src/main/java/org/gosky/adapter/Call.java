package org.gosky.adapter;


import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public interface Call<T> extends Cloneable {

    void enqueue(Callback<T> callback);

    void onComplete(Handler<AsyncResult<T>> handler);

    Call<T> clone();

//    void cancel();

    /** True if {@link #cancel()} was called. */
//    boolean isCanceled();

}
