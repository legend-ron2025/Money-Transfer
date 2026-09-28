package com.moneytracker.data.mapper

import com.moneytracker.core.network.dto.ConsentDto
import com.moneytracker.core.network.dto.ProviderDto
import com.moneytracker.core.network.dto.ConsentedAccountDto
import com.moneytracker.domain.model.Consent
import com.moneytracker.domain.model.Provider
import com.moneytracker.domain.model.ConsentedAccount
import com.moneytracker.domain.model.ConsentStatus
import com.moneytracker.domain.model.Provider.ProviderType

object ConsentMapper {

    fun toDomain(dto: ConsentDto): Consent {
        return Consent(
            id = dto.id,
            userId = dto.userId,
            provider = toDomain(dto.provider),
            consentId = dto.consentId,
            status = parseStatus(dto.status),
            grantedAt = dto.grantedAt,
            expiresAt = dto.expiresAt,
            revokedAt = dto.revokedAt,
            accounts = dto.accounts.map { toDomain(it) },
            permissions = dto.permissions
        )
    }

    fun toDomain(dto: ProviderDto): Provider {
        return Provider(
            id = dto.id,
            name = dto.name,
            logo = dto.logo,
            type = parseProviderType(dto.type),
            country = dto.country,
            supportedAccountTypes = dto.supportedAccountTypes,
            supportedPermissions = dto.supportedPermissions,
            isActive = dto.isActive
        )
    }

    fun toDomain(dto: ConsentedAccountDto): ConsentedAccount {
        return ConsentedAccount(
            accountId = dto.accountId,
            accountType = dto.accountType,
            maskedNumber = dto.maskedNumber,
            institutionName = dto.institutionName
        )
    }

    private fun parseStatus(status: String): ConsentStatus {
        return when (status.uppercase()) {
            "PENDING" -> ConsentStatus.PENDING
            "ACTIVE" -> ConsentStatus.ACTIVE
            "EXPIRED" -> ConsentStatus.EXPIRED
            "REVOKED" -> ConsentStatus.REVOKED
            "FAILED" -> ConsentStatus.FAILED
            else -> ConsentStatus.PENDING
        }
    }

    private fun parseProviderType(type: String): ProviderType {
        return when (type.uppercase()) {
            "AA" -> ProviderType.AA
            "BANK" -> ProviderType.BANK
            "UPI" -> ProviderType.UPI
            "WALLET" -> ProviderType.WALLET
            "INVESTMENT" -> ProviderType.INVESTMENT
            else -> ProviderType.BANK
        }
    }
}