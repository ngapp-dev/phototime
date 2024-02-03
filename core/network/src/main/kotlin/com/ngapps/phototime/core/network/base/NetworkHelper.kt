/*
 * Copyright 2024 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ngapps.phototime.core.network.base

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.ngapps.phototime.core.network.interceptor.HttpRequestInterceptor
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.Cache
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.io.File
import java.util.concurrent.TimeUnit

private const val CONTENT_LENGTH = 250_000L
private const val CLIENT_TIME_OUT = 60L
private const val CLIENT_CACHE_SIZE = 10 * 1024 * 1024L
private const val CLIENT_CACHE_DIRECTORY = "http"


fun createCache(context: Context): Cache = Cache(
    directory = File(context.cacheDir, CLIENT_CACHE_DIRECTORY),
    maxSize = CLIENT_CACHE_SIZE,
)

fun createHttpLoggingInterceptor(isDev: Boolean = true): HttpLoggingInterceptor {
    return HttpLoggingInterceptor().apply {
        level = if (isDev) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }
}

fun createHttpRequestInterceptor(): HttpRequestInterceptor {
    return HttpRequestInterceptor()
}


fun createOkHttpClient(
    isDev: Boolean = true,
    authenticator: Authenticator,
    context: Context
): OkHttpClient {
    return OkHttpClient.Builder().apply {
        addInterceptor(createHttpLoggingInterceptor(isDev))
        addInterceptor(createHttpRequestInterceptor())
//        authenticator(authenticator)
        connectTimeout(CLIENT_TIME_OUT, TimeUnit.SECONDS)
        readTimeout(CLIENT_TIME_OUT, TimeUnit.SECONDS)
        writeTimeout(CLIENT_TIME_OUT, TimeUnit.SECONDS)
        followSslRedirects(true)
        followRedirects(true)
        retryOnConnectionFailure(true)
    }.build()
}

fun createOkHttpClient(
    isDev: Boolean = true,
    isCache: Boolean = false,
    authenticator: Authenticator,
    context: Context
): OkHttpClient {
    return OkHttpClient.Builder().apply {
        addInterceptor(createHttpLoggingInterceptor(isDev))
        addInterceptor(createHttpRequestInterceptor())
//        authenticator(authenticator)
        connectTimeout(CLIENT_TIME_OUT, TimeUnit.SECONDS)
        readTimeout(CLIENT_TIME_OUT, TimeUnit.SECONDS)
        writeTimeout(CLIENT_TIME_OUT, TimeUnit.SECONDS)
        followSslRedirects(true)
        followRedirects(true)
        retryOnConnectionFailure(true)
    }.build()
}

fun createOkHttpClient2(
    interceptors: MutableList<Interceptor> = mutableListOf(),
): OkHttpClient {
    return OkHttpClient.Builder().apply {
        interceptors.forEach { addInterceptor(it) }
    }.build()
}

fun createOkHttpClient(
    isCache: Boolean = false,
    vararg interceptors: Interceptor,
//    authenticator: Authenticator,
    context: Context
): OkHttpClient {
    return OkHttpClient.Builder().apply {
        if (isCache) cache(createCache(context))
        interceptors.forEach { addInterceptor(it) }
//        authenticator(authenticator)
        connectTimeout(CLIENT_TIME_OUT, TimeUnit.SECONDS)
        readTimeout(CLIENT_TIME_OUT, TimeUnit.SECONDS)
        writeTimeout(CLIENT_TIME_OUT, TimeUnit.SECONDS)
        followSslRedirects(true)
        followRedirects(true)
        retryOnConnectionFailure(true)
    }.build()
}

fun createOkHttpClient(
    isCache: Boolean = false,
    vararg interceptors: Interceptor,
    authenticator: Authenticator,
    context: Context
): OkHttpClient {
    return OkHttpClient.Builder().apply {
        if (isCache) cache(createCache(context))
        interceptors.forEach { addInterceptor(it) }
//        authenticator(authenticator)
        connectTimeout(CLIENT_TIME_OUT, TimeUnit.SECONDS)
        readTimeout(CLIENT_TIME_OUT, TimeUnit.SECONDS)
        writeTimeout(CLIENT_TIME_OUT, TimeUnit.SECONDS)
        followSslRedirects(true)
        followRedirects(true)
        retryOnConnectionFailure(true)
    }.build()
}

/**
 * Create Retrofit Client with Moshi
 *
 * <reified T> private func let us using reflection.
 * We can use generics and reflection so ->
 *  we don't have to define required NewsApi Interface here
 */
inline fun <reified T> createRetrofit(
    networkJson: Json,
    okhttpCallFactory: Call.Factory,
    okHttpClient: OkHttpClient,
    baseUrl: String
): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .callFactory(okhttpCallFactory)
        .addConverterFactory(networkJson.asConverterFactory("application/json".toMediaType()))
        .client(okHttpClient)
        .build()
    return retrofit.create(T::class.java)
}