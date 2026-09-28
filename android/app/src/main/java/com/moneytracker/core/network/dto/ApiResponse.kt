package com.moneytracker.core.network.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T?,
    @SerializedName("error") val error: ApiError?,
    @SerializedName("meta") val meta: Meta?
)

@Serializable
data class ApiError(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("details") val details: Map<String, Any>? = null
)

@Serializable
data class Meta(
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("requestId") val requestId: String?,
    @SerializedName("version") val version: String?
)

@Serializable
data class PaginatedResponse<T>(
    @SerializedName("items") val items: List<T>,
    @SerializedName("page") val page: Int,
    @SerializedName("limit") val limit: Int,
    @SerializedName("total") val total: Long,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("hasNext") val hasNext: Boolean,
    @SerializedName("hasPrevious") val hasPrevious: Boolean
)