package com.moneytracker.core.common

import android.content.Context
import android.content.res.Configuration
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.analytics.FirebaseAnalytics
import com.moneytracker.MoneyTrackerApplication
import dagger.hilt.android.scopes.Singleton
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    @Provides
    @Singleton
    fun provideAnalyticsManager(@ApplicationContext context: Context): AnalyticsManager {
        return AnalyticsManagerImpl(context)
    }
}

interface AnalyticsManager : LifecycleObserver {
    fun trackEvent(eventName: String, params: Map<String, Any>?)
    fun trackScreenView(screenName: String, screenClass: String?)
    fun setUserId(userId: String?)
    fun setUserProperty(name: String, value: String?)
    fun logPurchase(amount: Double, currency: String, itemName: String)
    fun logLogin(method: String)
    fun logSignUp(method: String)
    fun logSyncStatus(status: String, details: String?)
    fun logError(error: Throwable, context: String)
}

class AnalyticsManagerImpl @Inject constructor(
    private val context: Context
) : AnalyticsManager {

    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    override fun trackEvent(eventName: String, params: Map<String, Any>?) {
        val bundle = android.os.Bundle()
        params?.forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Double -> bundle.putDouble(key, value)
                is Boolean -> bundle.putBoolean(key, value)
                else -> bundle.putString(key, value.toString())
            }
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    override fun trackScreenView(screenName: String, screenClass: String?) {
        firebaseAnalytics.setCurrentScreen(context as? android.app.Activity, screenName, screenClass)
    }

    override fun setUserId(userId: String?) {
        firebaseAnalytics.setUserId(userId)
    }

    override fun setUserProperty(name: String, value: String?) {
        firebaseAnalytics.setUserProperty(name, value)
    }

    override fun logPurchase(amount: Double, currency: String, itemName: String) {
        val bundle = android.os.Bundle().apply {
            putString(FirebaseAnalytics.Param.CURRENCY, currency)
            putDouble(FirebaseAnalytics.Param.VALUE, amount)
            putString(FirebaseAnalytics.Param.ITEM_NAME, itemName)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE, bundle)
    }

    override fun logLogin(method: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, android.os.Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, method)
        })
    }

    override fun logSignUp(method: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, android.os.Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, method)
        })
    }

    override fun logSyncStatus(status: String, details: String?) {
        trackEvent("sync_status", mapOf(
            "status" to status,
            "details" to details ?: ""
        ))
    }

    override fun logError(error: Throwable, context: String) {
        trackEvent("app_error", mapOf(
            "error_message" to error.message ?: "Unknown error",
            "error_class" to error.javaClass.simpleName,
            "context" to context
        ))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppStart() {
        trackEvent("app_start", null)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppStop() {
        trackEvent("app_stop", null)
    }
}

import dagger.hilt.android.qualifiers.ApplicationContext