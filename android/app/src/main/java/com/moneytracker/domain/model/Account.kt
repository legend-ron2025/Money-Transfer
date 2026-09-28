package com.moneytracker.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Account(
    val id: String,
    val userId: String,
    val institution: Institution,
    val accountType: AccountType,
    val accountSubType: String?,
    val maskedNumber: String,
    val currency: String,
    val balance: Money,
    val availableBalance: Money?,
    val creditLimit: Money?,
    val isActive: Boolean,
    val lastSyncedAt: String?,
    val lastSuccessfulSync: String?,
    val syncStatus: SyncStatus,
    val syncError: String?,
    val consentId: String?,
    val createdAt: String,
    val updatedAt: String
) {
    val displayName: String
        get() = "${institution.name} ${accountType.displayName} ${maskedNumber}"

    val effectiveBalance: Money
        get() = availableBalance ?: balance

    enum class AccountType(val displayName: String) {
        SAVINGS("Savings"),
        CURRENT("Current"),
        CREDIT("Credit Card"),
        LOAN("Loan"),
        INVESTMENT("Investment"),
        WALLET("Wallet"),
        CASH("Cash"),
        OTHER("Other")
    }

    enum class SyncStatus {
        SYNCED, SYNCING, ERROR, PENDING, DISCONNECTED
    }
}

@Serializable
data class Institution(
    val id: String,
    val name: String,
    val logo: String?,
    val type: InstitutionType,
    val country: String,
    val supportedFeatures: List<String>
) {
    enum class InstitutionType {
        BANK, NBFC, WALLET, UPI, INVESTMENT, INSURANCE, OTHER
    }
}