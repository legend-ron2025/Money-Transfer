package com.moneytracker.core.security

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

/**
 * Foreground service placeholder.
 * App-lock timeout is implemented in MoneyTrackerApplication
 * via ProcessLifecycleOwner — no foreground service needed.
 */
class AppLockService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("AppLockService", "started")
        return START_NOT_STICKY
    }
}
