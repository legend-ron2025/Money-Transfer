package com.moneytracker.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.moneytracker.MainActivity
import com.moneytracker.R

class MoneyTrackerFirebaseService : FirebaseMessagingService() {

    companion object {
        const val TAG           = "MTFirebase"
        const val CHANNEL_ID    = "money_tracker_alerts"
        const val CHANNEL_NAME  = "MoneyTracker Alerts"

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Budget alerts, transaction notifications and sync updates"
                    enableVibration(true)
                }
                (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    .createNotificationChannel(channel)
            }
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "FCM token refreshed: ${token.take(20)}…")
        // TODO: POST token to /api/v1/users/me/fcm-token
    }

    override fun onMessageReceived(msg: RemoteMessage) {
        Log.d(TAG, "Message from ${msg.from}: ${msg.notification?.title}")

        val title = msg.notification?.title ?: msg.data["title"] ?: "MoneyTracker"
        val body  = msg.notification?.body  ?: msg.data["body"]  ?: ""
        val type  = msg.data["type"] ?: ""

        val deepLink = when (type) {
            "BUDGET_ALERT"         -> "moneytracker://budgets"
            "SUBSCRIPTION_REMINDER"-> "moneytracker://subscriptions"
            "GOAL_MILESTONE"       -> "moneytracker://goals"
            "LARGE_TRANSACTION"    -> "moneytracker://transactions"
            "SYNC_COMPLETE"        -> "moneytracker://dashboard"
            else                   -> "moneytracker://dashboard"
        }

        showNotification(title, body, deepLink, type.hashCode())
    }

    private fun showNotification(title: String, body: String, deepLink: String, id: Int) {
        createNotificationChannel(this)

        val tapIntent = Intent(this, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data   = android.net.Uri.parse(deepLink)
            flags  = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pi = PendingIntent.getActivity(
            this, id, tapIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(id, notification)
    }
}
