package com.moneytracker.core.network.interceptor

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkInterceptor @Inject constructor(
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isNetworkAvailable()) {
            throw NoNetworkException("No network connection available")
        }
        return chain.proceed(chain.request())
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    class NoNetworkException(message: String) : RuntimeException(message)
}