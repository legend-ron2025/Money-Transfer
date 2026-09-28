package com.moneytracker.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Goal(
    val id: String,
    val userId: String,
    val name: String,
    val description: String?,
    val targetAmount: Money,
    val currentAmount: Money,
    val targetDate: String,
    val monthlyContribution: Money?,
    val status: GoalStatus,
    val icon: String?,
    val color: String?,
    val createdAt: String,
    val updatedAt: String
) {
    val progress: Percentage
        get() = if (targetAmount.amount > 0) {
            Percentage.of((currentAmount.amount / targetAmount.amount) * 100)
        } else {
            Percentage.zero()
        }

    val remainingAmount: Money
        get() = targetAmount.minus(currentAmount)

    val daysRemaining: Int
        get() = calculateDaysRemaining()

    val requiredMonthlyContribution: Money
        get() = calculateRequiredMonthly()

    private fun calculateDaysRemaining(): Int {
        val now = java.time.LocalDate.now()
        val target = java.time.LocalDate.parse(targetDate)
        return java.time.temporal.ChronoUnit.DAYS.between(now, target).toInt().coerceAtLeast(0)
    }

    private fun calculateRequiredMonthly(): Money {
        val monthsRemaining = maxOf(1, daysRemaining / 30)
        return remainingAmount.div(monthsRemaining.toDouble())
    }

    val isOnTrack: Boolean
        get() = monthlyContribution != null && monthlyContribution!! >= requiredMonthlyContribution

    val isOverdue: Boolean
        get() = java.time.LocalDate.now() > java.time.LocalDate.parse(targetDate) && status == GoalStatus.ACTIVE
}

enum class GoalStatus {
    ACTIVE, COMPLETED, PAUSED, CANCELLED
}