package com.moneytracker.core.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.moneytracker.core.common.Constants
import com.moneytracker.data.repository.AccountRepository
import com.moneytracker.data.repository.TransactionRepository
import com.moneytracker.data.repository.ConsentRepository
import dagger.hilt.android.scopes.Singleton
import dagger.Module
import dagger.Provides
import dagger.hilt.work.HiltWorkerFactory
import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object SyncModule {

    @Provides
    @Singleton
    fun provideSyncScheduler(
        context: Context,
        accountRepository: AccountRepository,
        transactionRepository: TransactionRepository,
        consentRepository: ConsentRepository
    ): SyncScheduler {
        return SyncSchedulerImpl(context, accountRepository, transactionRepository, consentRepository)
    }

    @Provides
    @Singleton
    fun provideHiltWorkerFactory(): HiltWorkerFactory {
        return HiltWorkerFactory()
    }
}

interface SyncScheduler {
    fun schedulePeriodicSync()
    fun cancelPeriodicSync()
    fun triggerImmediateSync()
    fun triggerAccountSync(accountId: String)
    fun isSyncing(): Boolean
}

class SyncSchedulerImpl @Inject constructor(
    private val context: Context,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val consentRepository: ConsentRepository
) : SyncScheduler {

    private val WORK_NAME = "periodic_sync"
    private var isSyncingFlag = false

    override fun schedulePeriodicSync() {
        val workRequest = PeriodicWorkRequest.Builder(SyncWorker::class.java, 15, TimeUnit.MINUTES)
            .setInitialDelay(5, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, workRequest)
    }

    override fun cancelPeriodicSync() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    override fun triggerImmediateSync() {
        val workRequest = androidx.work.OneTimeWorkRequest.Builder(SyncWorker::class.java)
            .build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }

    override fun triggerAccountSync(accountId: String) {
        val workRequest = androidx.work.OneTimeWorkRequest.Builder(AccountSyncWorker::class.java)
            .setInputData(androidx.work.Data.Builder().putString("account_id", accountId).build())
            .build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }

    override fun isSyncing(): Boolean = isSyncingFlag

    fun setSyncing(syncing: Boolean) {
        isSyncingFlag = syncing
    }
}

class SyncWorker @Inject constructor(
    context: Context,
    params: WorkerParameters,
    private val syncScheduler: SyncSchedulerImpl,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val consentRepository: ConsentRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        syncScheduler.setSyncing(true)
        return try {
            // Get active consents
            val consents = consentRepository.getActiveConsents()
            
            for (consent in consents) {
                // Sync accounts for each consent
                val accounts = accountRepository.getAccountsByConsent(consent.id)
                
                for (account in accounts) {
                    if (account.syncStatus != Constants.AccountSyncStatus.SYNCING) {
                        accountRepository.syncAccount(account.id)
                    }
                }
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        } finally {
            syncScheduler.setSyncing(false)
        }
    }
}

class AccountSyncWorker @Inject constructor(
    context: Context,
    params: WorkerParameters,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val accountId = inputData.getString("account_id") ?: return Result.failure()
        
        return try {
            accountRepository.syncAccount(accountId)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}