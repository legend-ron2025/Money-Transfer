package com.moneytracker

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.FirebaseApp
import com.moneytracker.core.notifications.MoneyTrackerFirebaseService
import com.moneytracker.core.security.TokenManager
import com.moneytracker.core.sync.SyncWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MoneyTrackerApplication : Application() {

    @Inject lateinit var tokenManager: TokenManager

    /** Timestamp (ms) when the app last moved to the background. */
    private var backgroundedAt: Long = 0L
    private val APP_LOCK_TIMEOUT_MS = 5 * 60 * 1000L  // 5 minutes

    override fun onCreate() {
        super.onCreate()

        // Firebase
        FirebaseApp.initializeApp(this)

        // Notification channel (required on Android 8+)
        MoneyTrackerFirebaseService.createNotificationChannel(this)

        // Periodic background sync
        SyncWorker.schedulePeriodicSync(this)

        // App-lock timeout observer
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLockObserver())
    }

    inner class AppLockObserver : DefaultLifecycleObserver {
        override fun onStop(owner: LifecycleOwner) {
            backgroundedAt = System.currentTimeMillis()
            Log.d("AppLock", "App backgrounded")
        }

        override fun onStart(owner: LifecycleOwner) {
            val elapsed = System.currentTimeMillis() - backgroundedAt
            if (backgroundedAt > 0 && elapsed >= APP_LOCK_TIMEOUT_MS) {
                if (tokenManager.isAppLockEnabled()) {
                    Log.d("AppLock", "Timeout reached — locking app")
                    // Signal MainActivity to show biometric prompt on next resume
                    // Using a simple SharedPrefs flag read in MainActivity
                    getSharedPreferences("auth", Context.MODE_PRIVATE)
                        .edit().putBoolean("lock_required", true).apply()
                }
            }
        }
    }
}
