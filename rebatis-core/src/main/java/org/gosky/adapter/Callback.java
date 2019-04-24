package org.gosky.adapter;

public interface Callback<T> {


    void onResponse(T response);


    void onFailure(Throwable t);
}
