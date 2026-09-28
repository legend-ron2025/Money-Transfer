package com.moneytracker.data.mapper

import com.moneytracker.core.network.dto.GoalDto
import com.moneytracker.domain.model.Goal
import com.moneytracker.domain.model.Money
import com.moneytracker.domain.model.GoalStatus

object GoalMapper {

    fun toDomain(dto: GoalDto): Goal {
        return Goal(
            id = dto.id,
            userId = dto.userId,
            name = dto.name,
            description = dto.description,
            targetAmount = Money(dto.targetAmount, dto.currency),
            currentAmount = Money(dto.currentAmount, dto.currency),
            targetDate = dto.targetDate,
            monthlyContribution = dto.monthlyContribution?.let { Money(it, dto.currency) },
            status = parseStatus(dto.status),
            icon = dto.icon,
            color = dto.color,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    private fun parseStatus(status: String): GoalStatus {
        return when (status.uppercase()) {
            "ACTIVE" -> GoalStatus.ACTIVE
            "COMPLETED" -> GoalStatus.COMPLETED
            "PAUSED" -> GoalStatus.PAUSED
            "CANCELLED" -> GoalStatus.CANCELLED
            else -> GoalStatus.ACTIVE
        }
    }
}