package com.moneytracker.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.moneytracker.core.database.entity.BudgetEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBudgets(budgets: List<BudgetEntity>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBudget(budget: BudgetEntity): Completable

    @Update
    fun updateBudget(budget: BudgetEntity): Completable

    @Query("SELECT * FROM budgets WHERE id = :budgetId")
    fun getBudget(budgetId: String): Maybe<BudgetEntity>

    @Query("SELECT * FROM budgets WHERE user_id = :userId AND is_active = 1 ORDER BY created_at DESC")
    fun getActiveBudgets(userId: String): Flowable<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE user_id = :userId ORDER BY created_at DESC")
    fun getAllBudgets(userId: String): Flowable<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE user_id = :userId AND category_id = :categoryId AND is_active = 1")
    fun getBudgetByCategory(userId: String, categoryId: String): Maybe<BudgetEntity>

    @Query("SELECT * FROM budgets WHERE user_id = :userId AND period = :period AND date(:startDate) BETWEEN date(start_date) AND COALESCE(date(end_date), '9999-12-31')")
    fun getBudgetsByPeriod(userId: String, period: String, startDate: String): Flowable<List<BudgetEntity>>

    @Query("UPDATE budgets SET spent_amount = :spentAmount WHERE id = :budgetId")
    fun updateSpentAmount(budgetId: String, spentAmount: Double): Completable

    @Query("""
        SELECT b.*, 
               c.name as category_name,
               c.icon as category_icon
        FROM budgets b
        JOIN categories c ON b.category_id = c.id
        WHERE b.user_id = :userId AND b.is_active = 1
    """)
    fun getBudgetsWithCategory(userId: String): Flowable<List<BudgetWithCategory>>

    @Delete
    fun deleteBudget(budget: BudgetEntity): Completable

    @Query("DELETE FROM budgets WHERE id = :budgetId")
    fun deleteBudgetById(budgetId: String): Completable

    data class BudgetWithCategory(
        val id: String,
        val user_id: String,
        val category_id: String,
        val amount: Double,
        val period: String,
        val start_date: String,
        val end_date: String?,
        val spent_amount: Double,
        val alert_threshold: Double,
        val is_active: Boolean,
        val created_at: String,
        val updated_at: String,
        val category_name: String,
        val category_icon: String
    )
}