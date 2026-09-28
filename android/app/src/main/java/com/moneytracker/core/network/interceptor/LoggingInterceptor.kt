package com.moneytracker.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class LoggingInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startTime = System.currentTimeMillis()

        Timber.tag("Network").d("Request: ${request.method} ${request.url}")
        request.headers().forEach { (name, value) ->
            if (name.lowercase() != "authorization") {
                Timber.tag("Network").d("Header: $name: $value")
            }
        }

        val response = chain.proceed(request)

        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        Timber.tag("Network").d("Response: ${response.code} ${response.message()} (${duration}ms)")
        response.headers().forEach { (name, value) ->
            Timber.tag("Network").d("Response Header: $name: $value")
        })

        return response
    }
}