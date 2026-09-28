package com.moneytracker.data.mapper

import com.moneytracker.core.network.dto.BudgetDto
import com.moneytracker.domain.model.Budget
import com.moneytracker.domain.model.Money
import com.moneytracker.domain.model.BudgetPeriod
import com.moneytracker.domain.model.BudgetStatus

object BudgetMapper {

    fun toDomain(dto: BudgetDto): Budget {
        return Budget(
            id = dto.id,
            userId = dto.userId,
            category = CategoryMapper.toDomain(dto.category!!),
            amount = Money(dto.amount, dto.category?.let { "INR" } ?: "INR"),
            period = parsePeriod(dto.period),
            startDate = dto.startDate,
            endDate = dto.endDate,
            spentAmount = Money(dto.spentAmount, dto.category?.let { "INR" } ?: "INR"),
            alertThreshold = dto.alertThreshold,
            isActive = dto.isActive,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    private fun parsePeriod(period: String): BudgetPeriod {
        return when (period.uppercase()) {
            "WEEKLY" -> BudgetPeriod.WEEKLY
            "MONTHLY" -> BudgetPeriod.MONTHLY
            "QUARTERLY" -> BudgetPeriod.QUARTERLY
            "YEARLY" -> BudgetPeriod.YEARLY
            "CUSTOM" -> BudgetPeriod.CUSTOM
            else -> BudgetPeriod.MONTHLY
        }
    }
}