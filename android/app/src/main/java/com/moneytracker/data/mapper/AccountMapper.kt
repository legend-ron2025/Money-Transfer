package com.moneytracker.data.mapper

import com.moneytracker.core.network.dto.AccountDto
import com.moneytracker.core.network.dto.InstitutionDto
import com.moneytracker.domain.model.Account
import com.moneytracker.domain.model.Money
import com.moneytracker.domain.model.Institution

object AccountMapper {

    fun toDomain(dto: AccountDto): Account {
        return Account(
            id = dto.id,
            userId = dto.userId,
            institution = toDomain(dto.institution!!),
            accountType = parseAccountType(dto.accountType),
            accountSubType = dto.accountSubType,
            maskedNumber = dto.maskedNumber,
            currency = dto.currency,
            balance = Money(dto.balance, dto.currency),
            availableBalance = dto.availableBalance?.let { Money(it, dto.currency) },
            creditLimit = dto.creditLimit?.let { Money(it, dto.currency) },
            isActive = dto.isActive,
            lastSyncedAt = dto.lastSyncedAt,
            lastSuccessfulSync = dto.lastSuccessfulSync,
            syncStatus = parseSyncStatus(dto.syncStatus),
            syncError = dto.syncError,
            consentId = dto.consentId,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    fun toDomain(dto: InstitutionDto): Institution {
        return Institution(
            id = dto.id,
            name = dto.name,
            logo = dto.logo,
            type = parseInstitutionType(dto.type),
            country = dto.country,
            supportedFeatures = dto.supportedFeatures
        )
    }

    private fun parseAccountType(type: String): Account.AccountType {
        return when (type.uppercase()) {
            "SAVINGS" -> Account.AccountType.SAVINGS
            "CURRENT" -> Account.AccountType.CURRENT
            "CREDIT" -> Account.AccountType.CREDIT
            "LOAN" -> Account.AccountType.LOAN
            "INVESTMENT" -> Account.AccountType.INVESTMENT
            "WALLET" -> Account.AccountType.WALLET
            "CASH" -> Account.AccountType.CASH
            else -> Account.AccountType.OTHER
        }
    }

    private fun parseInstitutionType(type: String): Institution.InstitutionType {
        return when (type.uppercase()) {
            "BANK" -> Institution.InstitutionType.BANK
            "NBFC" -> Institution.InstitutionType.NBFC
            "WALLET" -> Institution.InstitutionType.WALLET
            "UPI" -> Institution.InstitutionType.UPI
            "INVESTMENT" -> Institution.InstitutionType.INVESTMENT
            "INSURANCE" -> Institution.InstitutionType.INSURANCE
            else -> Institution.InstitutionType.OTHER
        }
    }

    private fun parseSyncStatus(status: String): Account.SyncStatus {
        return when (status.uppercase()) {
            "SYNCED" -> Account.SyncStatus.SYNCED
            "SYNCING" -> Account.SyncStatus.SYNCING
            "ERROR" -> Account.SyncStatus.ERROR
            "PENDING" -> Account.SyncStatus.PENDING
            "DISCONNECTED" -> Account.SyncStatus.DISCONNECTED
            else -> Account.SyncStatus.PENDING
        }
    }
}