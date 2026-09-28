package com.moneytracker.core.sync

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.moneytracker.core.network.api.AccountsApi
import com.moneytracker.core.security.TokenManager
import com.moneytracker.data.repository.AccountRepositoryImpl
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val tokenManager: TokenManager,
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG          = "SyncWorker"
        const val WORK_NAME_PERIODIC   = "periodic_sync"
        const val WORK_NAME_ONE_SHOT   = "one_shot_sync"
        private val LAST_SYNC_KEY      = stringPreferencesKey("last_sync_token")

        /** Enqueue a 15-minute periodic sync (fires once per 15m when network is available). */
        fun schedulePeriodicSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME_PERIODIC,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
            Log.d(TAG, "Periodic sync scheduled")
        }

        /** Trigger an immediate one-shot sync. */
        fun syncNow(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME_ONE_SHOT,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val userId = tokenManager.getUserId() ?: run {
            Log.w(TAG, "No authenticated user — skipping sync")
            return@withContext Result.success()
        }

        Log.i(TAG, "Starting sync for user $userId")

        try {
            // In a full implementation this would:
            // 1. GET /sync/delta?since=lastSyncToken from server
            // 2. Upsert all returned entities into Room
            // 3. POST /sync/push for any locally queued changes
            // 4. Persist the new nextSyncToken to DataStore

            // The BootReceiver already calls schedulePeriodicSync on boot.
            // Account-level sync is triggered by AccountRepository.syncAccount().
            Log.i(TAG, "Sync complete")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Sync failed: ${e.message}")
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
