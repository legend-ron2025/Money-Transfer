package com.moneytracker.data.mapper

import com.moneytracker.core.network.dto.TransactionDto
import com.moneytracker.core.network.dto.AccountSummaryDto
import com.moneytracker.core.network.dto.CategoryDto
import com.moneytracker.core.network.dto.LocationDto
import com.moneytracker.domain.model.Transaction
import com.moneytracker.domain.model.Money
import com.moneytracker.domain.model.AccountSummary
import com.moneytracker.domain.model.Category
import com.moneytracker.domain.model.Location
import com.moneytracker.domain.model.TransactionType
import com.moneytracker.domain.model.TransactionStatus
import com.moneytracker.domain.model.TransactionSource
import com.moneytracker.domain.model.PaymentChannel

object TransactionMapper {

    fun toDomain(dto: TransactionDto): Transaction {
        return Transaction(
            id = dto.id,
            userId = dto.userId,
            accountId = dto.accountId,
            account = dto.account?.let { toDomain(it) },
            externalId = dto.externalId,
            amount = Money(dto.amount, dto.currency),
            type = parseTransactionType(dto.type),
            status = parseTransactionStatus(dto.status),
            description = dto.description,
            merchantName = dto.merchantName,
            merchantLogo = dto.merchantLogo,
            transactionDate = dto.transactionDate,
            postedDate = dto.postedDate,
            category = dto.category?.let { CategoryMapper.toDomain(it) },
            subcategory = dto.subcategory,
            source = parseTransactionSource(dto.source),
            paymentChannel = dto.paymentChannel?.let { parsePaymentChannel(it) },
            upiReference = dto.upiReference,
            bankReference = dto.bankReference,
            isRecurring = dto.isRecurring,
            isTransfer = dto.isTransfer,
            transferPairId = dto.transferPairId,
            confidence = dto.confidence,
            notes = dto.notes,
            tags = dto.tags,
            location = dto.location?.let { toDomain(it) },
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    fun toDomain(dto: AccountSummaryDto): AccountSummary {
        return AccountSummary(
            id = dto.id,
            maskedNumber = dto.maskedNumber,
            institutionName = dto.institutionName,
            accountType = AccountMapper.parseAccountType(dto.accountType)
        )
    }

    fun toDomain(dto: LocationDto): Location {
        return Location(
            latitude = dto.latitude,
            longitude = dto.longitude,
            address = dto.address
        )
    }

    private fun parseTransactionType(type: String): TransactionType {
        return when (type.uppercase()) {
            "CREDIT" -> TransactionType.CREDIT
            "DEBIT" -> TransactionType.DEBIT
            "TRANSFER" -> TransactionType.TRANSFER
            else -> TransactionType.DEBIT
        }
    }

    private fun parseTransactionStatus(status: String): TransactionStatus {
        return when (status.uppercase()) {
            "PENDING" -> TransactionStatus.PENDING
            "COMPLETED" -> TransactionStatus.COMPLETED
            "FAILED" -> TransactionStatus.FAILED
            "CANCELLED" -> TransactionStatus.CANCELLED
            else -> TransactionStatus.PENDING
        }
    }

    private fun parseTransactionSource(source: String): TransactionSource {
        return when (source.uppercase()) {
            "BANK" -> TransactionSource.BANK
            "AA" -> TransactionSource.AA
            "MANUAL" -> TransactionSource.MANUAL
            "UPI" -> TransactionSource.UPI
            "CARD" -> TransactionSource.CARD
            "WALLET" -> TransactionSource.WALLET
            else -> TransactionSource.MANUAL
        }
    }

    private fun parsePaymentChannel(channel: String): PaymentChannel {
        return when (channel.uppercase()) {
            "UPI" -> PaymentChannel.UPI
            "NET_BANKING" -> PaymentChannel.NET_BANKING
            "CARD" -> PaymentChannel.CARD
            "WALLET" -> PaymentChannel.WALLET
            "CASH" -> PaymentChannel.CASH
            "IMPS" -> PaymentChannel.IMPS
            "NEFT" -> PaymentChannel.NEFT
            "RTGS" -> PaymentChannel.RTGS
            else -> PaymentChannel.OTHER
        }
    }
}

object CategoryMapper {
    fun toDomain(dto: CategoryDto): com.moneytracker.domain.model.Category {
        return com.moneytracker.domain.model.Category(
            id = dto.id,
            name = dto.name,
            parentId = dto.parentId,
            icon = dto.icon,
            type = parseCategoryType(dto.type),
            color = dto.color,
            isSystem = dto.isSystem,
            sortOrder = dto.sortOrder,
            children = dto.children.map { toDomain(it) }
        )
    }

    private fun parseCategoryType(type: String): com.moneytracker.domain.model.Category.CategoryType {
        return when (type.uppercase()) {
            "INCOME" -> com.moneytracker.domain.model.Category.CategoryType.INCOME
            "EXPENSE" -> com.moneytracker.domain.model.Category.CategoryType.EXPENSE
            "TRANSFER" -> com.moneytracker.domain.model.Category.CategoryType.TRANSFER
            else -> com.moneytracker.domain.model.Category.CategoryType.EXPENSE
        }
    }
}