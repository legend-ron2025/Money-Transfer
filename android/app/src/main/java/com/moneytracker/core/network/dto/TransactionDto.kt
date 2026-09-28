package com.moneytracker.core.network.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    @SerializedName("id") val id: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("accountId") val accountId: String,
    @SerializedName("account") val account: AccountSummaryDto?,
    @SerializedName("externalId") val externalId: String?,
    @SerializedName("amount") val amount: Double,
    @SerializedName("currency") val currency: String,
    @SerializedName("type") val type: String,
    @SerializedName("status") val status: String,
    @SerializedName("description") val description: String,
    @SerializedName("merchantName") val merchantName: String?,
    @SerializedName("merchantLogo") val merchantLogo: String?,
    @SerializedName("transactionDate") val transactionDate: String,
    @SerializedName("postedDate") val postedDate: String?,
    @SerializedName("categoryId") val categoryId: String?,
    @SerializedName("category") val category: CategoryDto?,
    @SerializedName("subcategory") val subcategory: String?,
    @SerializedName("source") val source: String,
    @SerializedName("paymentChannel") val paymentChannel: String?,
    @SerializedName("upiReference") val upiReference: String?,
    @SerializedName("bankReference") val bankReference: String?,
    @SerializedName("isRecurring") val isRecurring: Boolean,
    @SerializedName("isTransfer") val isTransfer: Boolean,
    @SerializedName("transferPairId") val transferPairId: String?,
    @SerializedName("confidence") val confidence: Double,
    @SerializedName("notes") val notes: String?,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("location") val location: LocationDto?,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)

@Serializable
data class AccountSummaryDto(
    @SerializedName("id") val id: String,
    @SerializedName("maskedNumber") val maskedNumber: String,
    @SerializedName("institutionName") val institutionName: String,
    @SerializedName("accountType") val accountType: String
)

@Serializable
data class LocationDto(
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("address") val address: String?
)

@Serializable
data class TransactionFilter(
    @SerializedName("type") val type: String? = null,
    @SerializedName("categoryId") val categoryId: String? = null,
    @SerializedName("accountId") val accountId: String? = null,
    @SerializedName("startDate") val startDate: String? = null,
    @SerializedName("endDate") val endDate: String? = null,
    @SerializedName("minAmount") val minAmount: Double? = null,
    @SerializedName("maxAmount") val maxAmount: Double? = null,
    @SerializedName("search") val search: String? = null,
    @SerializedName("isRecurring") val isRecurring: Boolean? = null,
    @SerializedName("isTransfer") val isTransfer: Boolean? = null
)

@Serializable
data class TransactionUpdateRequest(
    @SerializedName("categoryId") val categoryId: String? = null,
    @SerializedName("subcategory") val subcategory: String? = null,
    @SerializedName("merchantName") val merchantName: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("tags") val tags: List<String>? = null,
    @SerializedName("isTransfer") val isTransfer: Boolean? = null,
    @SerializedName("isRecurring") val isRecurring: Boolean? = null
)

@Serializable
data class BulkCategorizeRequest(
    @SerializedName("transactionIds") val transactionIds: List<String>,
    @SerializedName("categoryId") val categoryId: String
)