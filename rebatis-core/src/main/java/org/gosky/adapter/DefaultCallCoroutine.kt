package org.gosky.adapter

import io.vertx.kotlin.coroutines.awaitResult

suspend fun <T> Call<T>.sendAwait(): T {
    return awaitResult {
        this.onComplete(it)
    }
}