package org.gosky.adapter;


public interface Call<T> {

    void enqueue(Callback<T> callback);

}
