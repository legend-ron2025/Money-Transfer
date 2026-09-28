package com.moneytracker.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Consent(
    val id: String,
    val userId: String,
    val provider: Provider,
    val consentId: String,
    val status: ConsentStatus,
    val grantedAt: String,
    val expiresAt: String?,
    val revokedAt: String?,
    val accounts: List<ConsentedAccount>,
    val permissions: List<String>
) {
    val isActive: Boolean
        get() = status == ConsentStatus.ACTIVE

    val isExpired: Boolean
        get() = expiresAt != null && java.time.Instant.parse(expiresAt!!).isBefore(java.time.Instant.now())

    val daysUntilExpiry: Int?
        get() = expiresAt?.let {
            val expiry = java.time.Instant.parse(it)
            val now = java.time.Instant.now()
            java.time.temporal.ChronoUnit.DAYS.between(now, expiry).toInt()
        }
}

@Serializable
data class Provider(
    val id: String,
    val name: String,
    val logo: String,
    val type: ProviderType,
    val country: String,
    val supportedAccountTypes: List<String>,
    val supportedPermissions: List<String>,
    val isActive: Boolean
) {
    enum class ProviderType {
        AA, BANK, UPI, WALLET, INVESTMENT
    }
}

@Serializable
data class ConsentedAccount(
    val accountId: String,
    val accountType: String,
    val maskedNumber: String,
    val institutionName: String
)

enum class ConsentStatus {
    PENDING, ACTIVE, EXPIRED, REVOKED, FAILED
}