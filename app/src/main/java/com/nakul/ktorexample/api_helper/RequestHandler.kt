package com.nakul.ktorexample.api_helper

fun interface RequestHandler<T> {
    suspend fun sendRequest(): T
}