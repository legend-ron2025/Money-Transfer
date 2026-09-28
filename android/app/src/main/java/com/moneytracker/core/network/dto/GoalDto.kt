package com.moneytracker.core.network.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class GoalDto(
    @SerializedName("id") val id: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("targetAmount") val targetAmount: Double,
    @SerializedName("currentAmount") val currentAmount: Double,
    @SerializedName("currency") val currency: String,
    @SerializedName("targetDate") val targetDate: String,
    @SerializedName("progressPercentage") val progressPercentage: Double,
    @SerializedName("remainingAmount") val remainingAmount: Double,
    @SerializedName("monthlyContribution") val monthlyContribution: Double?,
    @SerializedName("status") val status: String,
    @SerializedName("icon") val icon: String?,
    @SerializedName("color") val color: String?,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)

@Serializable
data class CreateGoalRequest(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("targetAmount") val targetAmount: Double,
    @SerializedName("currency") val currency: String = "INR",
    @SerializedName("targetDate") val targetDate: String,
    @SerializedName("monthlyContribution") val monthlyContribution: Double? = null,
    @SerializedName("icon") val icon: String? = null,
    @SerializedName("color") val color: String? = null
)

@Serializable
data class UpdateGoalRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("targetAmount") val targetAmount: Double? = null,
    @SerializedName("targetDate") val targetDate: String? = null,
    @SerializedName("monthlyContribution") val monthlyContribution: Double? = null,
    @SerializedName("icon") val icon: String? = null,
    @SerializedName("color") val color: String? = null,
    @SerializedName("status") val status: String? = null
)