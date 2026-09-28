package com.moneytracker.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.moneytracker.core.database.entity.GoalEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe

@Dao
interface GoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGoals(goals: List<GoalEntity>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGoal(goal: GoalEntity): Completable

    @Update
    fun updateGoal(goal: GoalEntity): Completable

    @Query("SELECT * FROM goals WHERE id = :goalId")
    fun getGoal(goalId: String): Maybe<GoalEntity>

    @Query("SELECT * FROM goals WHERE user_id = :userId ORDER BY target_date ASC")
    fun getGoals(userId: String): Flowable<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE user_id = :userId AND status = :status ORDER BY target_date ASC")
    fun getGoalsByStatus(userId: String, status: String): Flowable<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE user_id = :userId AND status = 'active' ORDER BY target_date ASC")
    fun getActiveGoals(userId: String): Flowable<List<GoalEntity>>

    @Query("UPDATE goals SET current_amount = :currentAmount WHERE id = :goalId")
    fun updateCurrentAmount(goalId: String, currentAmount: Double): Completable

    @Query("UPDATE goals SET status = :status WHERE id = :goalId")
    fun updateStatus(goalId: String, status: String): Completable

    @Delete
    fun deleteGoal(goal: GoalEntity): Completable

    @Query("DELETE FROM goals WHERE id = :goalId")
    fun deleteGoalById(goalId: String): Completable
}