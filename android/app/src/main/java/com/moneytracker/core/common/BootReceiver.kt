package com.moneytracker.core.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.moneytracker.core.sync.SyncWorker

/** Re-schedules periodic sync after device reboot or app update. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action == Intent.ACTION_BOOT_COMPLETED || action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            Log.i("BootReceiver", "Rescheduling sync after $action")
            SyncWorker.schedulePeriodicSync(context)
        }
    }
}
