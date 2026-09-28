package com.moneytracker.core.network.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("data") val data: Map<String, String>?,
    @SerializedName("isRead") val isRead: Boolean,
    @SerializedName("priority") val priority: String,
    @SerializedName("actionUrl") val actionUrl: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("readAt") val readAt: String?
)

@Serializable
data class NotificationPreferencesDto(
    @SerializedName("transactionAlerts") val transactionAlerts: Boolean = true,
    @SerializedName("budgetAlerts") val budgetAlerts: Boolean = true,
    @SerializedName("subscriptionAlerts") val subscriptionAlerts: Boolean = true,
    @SerializedName("goalAlerts") val goalAlerts: Boolean = true,
    @SerializedName("syncAlerts") val syncAlerts: Boolean = true,
    @SerializedName("securityAlerts") val securityAlerts: Boolean = true,
    @SerializedName("marketingAlerts") val marketingAlerts: Boolean = false,
    @SerializedName("dailySummary") val dailySummary: Boolean = false,
    @SerializedName("weeklySummary") val weeklySummary: Boolean = true,
    @SerializedName("monthlySummary") val monthlySummary: Boolean = true,
    @SerializedName("quietHoursStart") val quietHoursStart: String = "22:00",
    @SerializedName("quietHoursEnd") val quietHoursEnd: String = "08:00"
)

@Serializable
data class UpdateNotificationPreferencesRequest(
    @SerializedName("transactionAlerts") val transactionAlerts: Boolean? = null,
    @SerializedName("budgetAlerts") val budgetAlerts: Boolean? = null,
    @SerializedName("subscriptionAlerts") val subscriptionAlerts: Boolean? = null,
    @SerializedName("goalAlerts") val goalAlerts: Boolean? = null,
    @SerializedName("syncAlerts") val syncAlerts: Boolean? = null,
    @SerializedName("securityAlerts") val securityAlerts: Boolean? = null,
    @SerializedName("marketingAlerts") val marketingAlerts: Boolean? = null,
    @SerializedName("dailySummary") val dailySummary: Boolean? = null,
    @SerializedName("weeklySummary") val weeklySummary: Boolean? = null,
    @SerializedName("monthlySummary") val monthlySummary: Boolean? = null,
    @SerializedName("quietHoursStart") val quietHoursStart: String? = null,
    @SerializedName("quietHoursEnd") val quietHoursEnd: String? = null
)