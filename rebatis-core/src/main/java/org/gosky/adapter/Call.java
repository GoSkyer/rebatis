package org.gosky.adapter;


public interface Call<T> extends Cloneable {

    void enqueue(Callback<T> callback);


    Call<T> clone();

    void cancel();

    /** True if {@link #cancel()} was called. */
    boolean isCanceled();

}
