package com.moneytracker.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.moneytracker.core.database.entity.TransactionEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransactions(transactions: List<TransactionEntity>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransaction(transaction: TransactionEntity): Completable

    @Update
    fun updateTransaction(transaction: TransactionEntity): Completable

    @Update
    fun updateTransactions(transactions: List<TransactionEntity>): Completable

    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    fun getTransaction(transactionId: String): Maybe<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE external_id = :externalId AND account_id = :accountId")
    fun getTransactionByExternalId(externalId: String, accountId: String): Maybe<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE user_id = :userId ORDER BY transaction_date DESC LIMIT :limit OFFSET :offset")
    fun getTransactions(userId: String, limit: Int, offset: Int): Flowable<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE user_id = :userId AND transaction_date BETWEEN :startDate AND :endDate ORDER BY transaction_date DESC")
    fun getTransactionsByDateRange(userId: String, startDate: String, endDate: String): Flowable<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE user_id = :userId AND type = :type ORDER BY transaction_date DESC LIMIT :limit OFFSET :offset")
    fun getTransactionsByType(userId: String, type: String, limit: Int, offset: Int): Flowable<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE user_id = :userId AND category_id = :categoryId ORDER BY transaction_date DESC")
    fun getTransactionsByCategory(userId: String, categoryId: String): Flowable<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE user_id = :userId AND account_id = :accountId ORDER BY transaction_date DESC LIMIT :limit OFFSET :offset")
    fun getTransactionsByAccount(userId: String, accountId: String, limit: Int, offset: Int): Flowable<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE user_id = :userId AND is_transfer = 1 ORDER BY transaction_date DESC")
    fun getTransfers(userId: String): Flowable<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE user_id = :userId AND is_recurring = 1 ORDER BY transaction_date DESC")
    fun getRecurringTransactions(userId: String): Flowable<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE transfer_pair_id = :pairId")
    fun getTransferPair(pairId: String): Flowable<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE user_id = :userId AND (merchant_name LIKE :query OR description LIKE :query) ORDER BY transaction_date DESC LIMIT :limit")
    fun searchTransactions(userId: String, query: String, limit: Int): Flowable<List<TransactionEntity>>

    @Query("SELECT SUM(CASE WHEN type = 'CREDIT' THEN amount ELSE 0 END) FROM transactions WHERE user_id = :userId AND transaction_date BETWEEN :startDate AND :endDate AND is_transfer = 0")
    fun getTotalIncome(userId: String, startDate: String, endDate: String): Single<Double?>

    @Query("SELECT SUM(CASE WHEN type = 'DEBIT' THEN amount ELSE 0 END) FROM transactions WHERE user_id = :userId AND transaction_date BETWEEN :startDate AND :endDate AND is_transfer = 0")
    fun getTotalExpense(userId: String, startDate: String, endDate: String): Single<Double?>

    @Query("SELECT category_id, SUM(amount) as total FROM transactions WHERE user_id = :userId AND type = 'DEBIT' AND transaction_date BETWEEN :startDate AND :endDate AND is_transfer = 0 GROUP BY category_id ORDER BY total DESC")
    fun getCategorySpending(userId: String, startDate: String, endDate: String): Flowable<List<CategorySpending>>

    @Query("SELECT * FROM transactions WHERE user_id = :userId AND is_recurring = 0 AND type = 'DEBIT' AND merchant_name IS NOT NULL ORDER BY transaction_date DESC LIMIT 100")
    fun getRecentExpenseTransactions(userId: String): Flowable<List<TransactionEntity>>

    @Query("DELETE FROM transactions WHERE id = :transactionId")
    fun deleteTransaction(transactionId: String): Completable

    @Query("DELETE FROM transactions WHERE user_id = :userId AND transaction_date < :cutoffDate")
    fun deleteOldTransactions(userId: String, cutoffDate: String): Completable

    @Query("SELECT COUNT(*) FROM transactions WHERE user_id = :userId")
    fun getTransactionCount(userId: String): Single<Int>

    data class CategorySpending(
        val category_id: String,
        val total: Double
    )
}