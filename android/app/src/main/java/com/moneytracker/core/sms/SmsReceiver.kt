package com.moneytracker.core.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import androidx.work.*
import com.moneytracker.core.sync.SmsImportWorker
import java.util.concurrent.TimeUnit

/** Receives incoming bank SMS and enqueues a WorkManager job to process it. */
class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isNullOrEmpty()) return

        // Group multi-part messages by originating address
        val byAddress = messages.groupBy { it.originatingAddress ?: "" }

        for ((sender, parts) in byAddress) {
            if (!SmsParser.isBankSender(sender)) continue

            val body = parts.joinToString("") { it.messageBody }
            val txn  = SmsParser.parseSms(sender, body) ?: continue

            Log.d("SmsReceiver", "Bank SMS from $sender: ${txn.type} ₹${txn.amount}")

            // Enqueue processing via WorkManager (keeps app alive, handles retries)
            val data = workDataOf(
                "amount"           to txn.amount,
                "type"             to txn.type,
                "maskedAccount"    to (txn.maskedAccount ?: ""),
                "merchant"         to (txn.merchant ?: ""),
                "upiRef"           to (txn.upiRef ?: ""),
                "availableBalance" to (txn.availableBalance ?: 0.0),
                "sender"           to sender,
                "body"             to body
            )

            val request = OneTimeWorkRequestBuilder<SmsImportWorker>()
                .setInputData(data)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build()

            WorkManager.getInstance(context).enqueue(request)
        }
    }
}
