package com.moneytracker.core.network.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ConsentDto(
    @SerializedName("id") val id: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("providerId") val providerId: String,
    @SerializedName("provider") val provider: ProviderDto,
    @SerializedName("consentId") val consentId: String,
    @SerializedName("status") val status: String,
    @SerializedName("grantedAt") val grantedAt: String,
    @SerializedName("expiresAt") val expiresAt: String?,
    @SerializedName("revokedAt") val revokedAt: String?,
    @SerializedName("accounts") val accounts: List<ConsentedAccountDto>,
    @SerializedName("permissions") val permissions: List<String>
)

@Serializable
data class ConsentedAccountDto(
    @SerializedName("accountId") val accountId: String,
    @SerializedName("accountType") val accountType: String,
    @SerializedName("maskedNumber") val maskedNumber: String,
    @SerializedName("institutionName") val institutionName: String
)

@Serializable
data class CreateConsentRequest(
    @SerializedName("providerId") val providerId: String,
    @SerializedName("accounts") val accounts: List<String>,
    @SerializedName("permissions") val permissions: List<String>,
    @SerializedName("redirectUrl") val redirectUrl: String
)

@Serializable
data class ProviderDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("logo") val logo: String,
    @SerializedName("type") val type: String,
    @SerializedName("country") val country: String,
    @SerializedName("supportedAccountTypes") val supportedAccountTypes: List<String>,
    @SerializedName("supportedPermissions") val supportedPermissions: List<String>,
    @SerializedName("isActive") val isActive: Boolean
)