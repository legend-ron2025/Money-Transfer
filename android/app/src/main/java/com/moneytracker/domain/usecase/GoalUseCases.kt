package com.moneytracker.domain.usecase

import com.moneytracker.domain.model.Goal
import com.moneytracker.domain.model.Money
import com.moneytracker.domain.repository.GoalRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetGoalsUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
    operator fun invoke(userId: String): Flowable<List<Goal>> {
        return goalRepository.getGoals(userId)
    }
}

class GetActiveGoalsUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
    operator fun invoke(userId: String): Flowable<List<Goal>> {
        return goalRepository.getActiveGoals(userId)
    }
}

class GetGoalUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
    operator fun invoke(goalId: String): Maybe<Goal> {
        return goalRepository.getGoal(goalId)
    }
}

class CreateGoalUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
    operator fun invoke(goal: Goal): Completable {
        return goalRepository.insertGoal(goal)
    }
}

class UpdateGoalUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
    operator fun invoke(goal: Goal): Completable {
        return goalRepository.updateGoal(goal)
    }
}

class UpdateGoalProgressUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
    operator fun invoke(goalId: String, currentAmount: Money): Completable {
        return goalRepository.updateCurrentAmount(goalId, currentAmount)
    }
}

class UpdateGoalStatusUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
    operator fun invoke(goalId: String, status: Goal.GoalStatus): Completable {
        return goalRepository.updateStatus(goalId, status)
    }
}

class DeleteGoalUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
    operator fun invoke(goalId: String): Completable {
        return goalRepository.deleteGoal(goalId)
    }
}