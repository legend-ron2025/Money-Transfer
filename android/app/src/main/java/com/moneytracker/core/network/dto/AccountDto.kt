package com.moneytracker.core.network.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class AccountDto(
    @SerializedName("id") val id: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("institutionId") val institutionId: String,
    @SerializedName("institution") val institution: InstitutionDto?,
    @SerializedName("accountType") val accountType: String,
    @SerializedName("accountSubType") val accountSubType: String?,
    @SerializedName("maskedNumber") val maskedNumber: String,
    @SerializedName("currency") val currency: String,
    @SerializedName("balance") val balance: Double,
    @SerializedName("availableBalance") val availableBalance: Double?,
    @SerializedName("creditLimit") val creditLimit: Double?,
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("lastSyncedAt") val lastSyncedAt: String?,
    @SerializedName("lastSuccessfulSync") val lastSuccessfulSync: String?,
    @SerializedName("syncStatus") val syncStatus: String,
    @SerializedName("syncError") val syncError: String?,
    @SerializedName("consentId") val consentId: String?,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)

@Serializable
data class InstitutionDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("logo") val logo: String?,
    @SerializedName("type") val type: String,
    @SerializedName("country") val country: String,
    @SerializedName("supportedFeatures") val supportedFeatures: List<String>
)

@Serializable
data class AccountSyncRequest(
    @SerializedName("accountId") val accountId: String?,
    @SerializedName("consentId") val consentId: String?,
    @SerializedName("fullSync") val fullSync: Boolean = false
)

@Serializable
data class AccountSyncResponse(
    @SerializedName("syncedTransactions") val syncedTransactions: Int,
    @SerializedName("newTransactions") val newTransactions: Int,
    @SerializedName("updatedTransactions") val updatedTransactions: Int,
    @SerializedName("failedTransactions") val failedTransactions: Int,
    @SerializedName("lastSyncTimestamp") val lastSyncTimestamp: String,
    @SerializedName("nextSyncTimestamp") val nextSyncTimestamp: String?
)