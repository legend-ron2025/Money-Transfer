package com.moneytracker.core.network.interceptor

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.rxjava3.RxDataStore
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder
import com.moneytracker.core.security.TokenManager
import io.reactivex.rxjava3.core.Single
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val context: Context
) : Interceptor {

    private val ACCESS_TOKEN_KEY = preferencesKey<String>("access_token")
    private val REFRESH_TOKEN_KEY = preferencesKey<String>("refresh_token")

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val accessToken = tokenManager.getAccessToken()
            ?: return chain.proceed(originalRequest)

        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

        val response = chain.proceed(authenticatedRequest)

        if (response.code == 401) {
            return handleUnauthorized(chain, originalRequest)
        }

        return response
    }

    private fun handleUnauthorized(chain: Interceptor.Chain, originalRequest: Request): Response {
        val refreshToken = tokenManager.getRefreshToken()
            ?: return chain.proceed(originalRequest)

        // Attempt token refresh
        val refreshed = tokenManager.refreshAccessToken(refreshToken)

        if (refreshed) {
            val newAccessToken = tokenManager.getAccessToken()!!
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
            return chain.proceed(newRequest)
        }

        // Refresh failed, clear tokens and return original response
        tokenManager.clearTokens()
        return chain.proceed(originalRequest)
    }
}