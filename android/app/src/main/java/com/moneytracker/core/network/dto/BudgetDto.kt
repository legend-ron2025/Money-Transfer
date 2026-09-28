package com.moneytracker.core.network.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class BudgetDto(
    @SerializedName("id") val id: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("categoryId") val categoryId: String,
    @SerializedName("category") val category: CategoryDto?,
    @SerializedName("amount") val amount: Double,
    @SerializedName("period") val period: String,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String?,
    @SerializedName("spentAmount") val spentAmount: Double,
    @SerializedName("remainingAmount") val remainingAmount: Double,
    @SerializedName("percentageUsed") val percentageUsed: Double,
    @SerializedName("status") val status: String,
    @SerializedName("alertThreshold") val alertThreshold: Double = 0.8,
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)

@Serializable
data class CreateBudgetRequest(
    @SerializedName("categoryId") val categoryId: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("period") val period: String,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String?,
    @SerializedName("alertThreshold") val alertThreshold: Double = 0.8
)

@Serializable
data class UpdateBudgetRequest(
    @SerializedName("amount") val amount: Double? = null,
    @SerializedName("period") val period: String? = null,
    @SerializedName("startDate") val startDate: String? = null,
    @SerializedName("endDate") val endDate: String? = null,
    @SerializedName("alertThreshold") val alertThreshold: Double? = null,
    @SerializedName("isActive") val isActive: Boolean? = null
)