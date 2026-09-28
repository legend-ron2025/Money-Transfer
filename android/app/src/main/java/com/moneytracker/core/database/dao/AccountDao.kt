package com.moneytracker.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.moneytracker.core.database.entity.AccountEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

@Dao
interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAccounts(accounts: List<AccountEntity>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAccount(account: AccountEntity): Completable

    @Update
    fun updateAccount(account: AccountEntity): Completable

    @Query("SELECT * FROM accounts WHERE id = :accountId")
    fun getAccount(accountId: String): Maybe<AccountEntity>

    @Query("SELECT * FROM accounts WHERE user_id = :userId AND is_active = 1")
    fun getActiveAccounts(userId: String): Flowable<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE user_id = :userId")
    fun getAllAccounts(userId: String): Flowable<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE consent_id = :consentId")
    fun getAccountsByConsent(consentId: String): Flowable<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE institution_id = :institutionId AND user_id = :userId")
    fun getAccountsByInstitution(institutionId: String, userId: String): Flowable<List<AccountEntity>>

    @Query("UPDATE accounts SET balance = :balance, available_balance = :availableBalance, last_synced_at = :lastSynced, sync_status = :syncStatus WHERE id = :accountId")
    fun updateAccountBalance(
        accountId: String,
        balance: Double,
        availableBalance: Double?,
        lastSynced: String,
        syncStatus: String
    ): Completable

    @Query("UPDATE accounts SET sync_error = :error, sync_status = 'error' WHERE id = :accountId")
    fun setAccountSyncError(accountId: String, error: String): Completable

    @Query("DELETE FROM accounts WHERE id = :accountId")
    fun deleteAccount(accountId: String): Completable

    @Query("SELECT SUM(balance) FROM accounts WHERE user_id = :userId AND is_active = 1")
    fun getTotalBalance(userId: String): Single<Double?>

    @Query("SELECT * FROM accounts WHERE user_id = :userId AND sync_status = 'pending'")
    fun getPendingSyncAccounts(userId: String): Flowable<List<AccountEntity>>
}