package com.moneytracker.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Budget(
    val id: String,
    val userId: String,
    val category: Category,
    val amount: Money,
    val period: BudgetPeriod,
    val startDate: String,
    val endDate: String?,
    val spentAmount: Money,
    val alertThreshold: Double = 0.8,
    val isActive: Boolean = true,
    val createdAt: String,
    val updatedAt: String
) {
    val remainingAmount: Money
        get() = amount.minus(spentAmount)

    val percentageUsed: Percentage
        get() = if (amount.amount > 0) {
            Percentage.of((spentAmount.amount / amount.amount) * 100)
        } else {
            Percentage.zero()
        }

    val status: BudgetStatus
        get() = when {
            percentageUsed.value >= 100 -> BudgetStatus.EXCEEDED
            percentageUsed.value >= 90 -> BudgetStatus.NEAR_LIMIT
            percentageUsed.value >= 70 -> BudgetStatus.WATCH
            else -> BudgetStatus.NORMAL
        }

    val isCurrentPeriod: Boolean
        get() = isDateInCurrentPeriod()

    private fun isDateInCurrentPeriod(): Boolean {
        val now = java.time.LocalDate.now()
        val start = java.time.LocalDate.parse(startDate)
        val end = endDate?.let { java.time.LocalDate.parse(it) } ?: java.time.LocalDate.MAX
        return now >= start && now <= end
    }

    val daysRemaining: Int
        get() = calculateDaysRemaining()

    private fun calculateDaysRemaining(): Int {
        val now = java.time.LocalDate.now()
        val end = endDate?.let { java.time.LocalDate.parse(it) } ?: return 30
        return java.time.temporal.ChronoUnit.DAYS.between(now, end).toInt().coerceAtLeast(0)
    }
}

enum class BudgetPeriod {
    WEEKLY, MONTHLY, QUARTERLY, YEARLY, CUSTOM
}

enum class BudgetStatus {
    NORMAL, WATCH, NEAR_LIMIT, EXCEEDED
}