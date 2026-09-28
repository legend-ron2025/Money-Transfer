package com.moneytracker.data.repository

import com.moneytracker.core.database.MoneyTrackerDatabase
import com.moneytracker.core.database.dao.GoalDao
import com.moneytracker.core.database.entity.GoalEntity
import com.moneytracker.data.mapper.GoalMapper
import com.moneytracker.domain.model.Goal
import com.moneytracker.domain.model.Money
import com.moneytracker.domain.repository.GoalRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val database: MoneyTrackerDatabase,
    private val goalDao: GoalDao,
    private val goalsApi: com.moneytracker.core.network.api.GoalsApi
) : GoalRepository {

    override fun getGoals(userId: String): Flowable<List<Goal>> {
        return goalDao.getGoals(userId)
            .map { entities -> entities.map { GoalMapper.toDomain(it) } }
    }

    override fun getActiveGoals(userId: String): Flowable<List<Goal>> {
        return goalDao.getActiveGoals(userId)
            .map { entities -> entities.map { GoalMapper.toDomain(it) } }
    }

    override fun getGoalsByStatus(userId: String, status: Goal.GoalStatus): Flowable<List<Goal>> {
        return goalDao.getGoalsByStatus(userId, status.name)
            .map { entities -> entities.map { GoalMapper.toDomain(it) } }
    }

    override fun getGoal(goalId: String): Maybe<Goal> {
        return goalDao.getGoal(goalId)
            .map { GoalMapper.toDomain(it) }
    }

    override fun insertGoal(goal: Goal): Completable {
        return Completable.fromAction {
            val entity = toEntity(goal)
            goalDao.insertGoal(entity)
        }.subscribeOn(Schedulers.io())
    }

    override fun updateGoal(goal: Goal): Completable {
        return Completable.fromAction {
            val entity = toEntity(goal)
            goalDao.updateGoal(entity)
        }.subscribeOn(Schedulers.io())
    }

    override fun updateCurrentAmount(goalId: String, currentAmount: Money): Completable {
        return Completable.fromAction {
            goalDao.updateCurrentAmount(goalId, currentAmount.amount)
        }.subscribeOn(Schedulers.io())
    }

    override fun updateStatus(goalId: String, status: Goal.GoalStatus): Completable {
        return Completable.fromAction {
            goalDao.updateStatus(goalId, status.name)
        }.subscribeOn(Schedulers.io())
    }

    override fun deleteGoal(goalId: String): Completable {
        return Completable.fromAction {
            goalDao.deleteGoalById(goalId)
        }.subscribeOn(Schedulers.io())
    }

    private fun toEntity(goal: Goal): GoalEntity {
        return GoalEntity(
            id = goal.id,
            userId = goal.userId,
            name = goal.name,
            description = goal.description,
            targetAmount = goal.targetAmount.amount,
            currentAmount = goal.currentAmount.amount,
            currency = goal.targetAmount.currency,
            targetDate = goal.targetDate,
            monthlyContribution = goal.monthlyContribution?.amount,
            status = goal.status.name,
            icon = goal.icon,
            color = goal.color,
            createdAt = goal.createdAt,
            updatedAt = goal.updatedAt
        )
    }
}