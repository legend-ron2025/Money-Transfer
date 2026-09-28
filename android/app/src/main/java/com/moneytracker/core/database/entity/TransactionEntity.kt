package com.moneytracker.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index
import androidx.room.ForeignKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["account_id"]),
        Index(value = ["category_id"]),
        Index(value = ["transaction_date"]),
        Index(value = ["external_id"]),
        Index(value = ["upi_reference"]),
        Index(value = ["is_transfer"]),
        Index(value = ["is_recurring"]),
        Index(value = ["transfer_pair_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
@Serializable
data class TransactionEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "account_id") val accountId: String,
    @ColumnInfo(name = "external_id") val externalId: String?,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "currency") val currency: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "merchant_name") val merchantName: String?,
    @ColumnInfo(name = "merchant_logo") val merchantLogo: String?,
    @ColumnInfo(name = "transaction_date") val transactionDate: String,
    @ColumnInfo(name = "posted_date") val postedDate: String?,
    @ColumnInfo(name = "category_id") val categoryId: String?,
    @ColumnInfo(name = "subcategory") val subcategory: String?,
    @ColumnInfo(name = "source") val source: String,
    @ColumnInfo(name = "payment_channel") val paymentChannel: String?,
    @ColumnInfo(name = "upi_reference") val upiReference: String?,
    @ColumnInfo(name = "bank_reference") val bankReference: String?,
    @ColumnInfo(name = "is_recurring") val isRecurring: Boolean,
    @ColumnInfo(name = "is_transfer") val isTransfer: Boolean,
    @ColumnInfo(name = "transfer_pair_id") val transferPairId: String?,
    @ColumnInfo(name = "confidence") val confidence: Double,
    @ColumnInfo(name = "notes") val notes: String?,
    @ColumnInfo(name = "tags") val tags: String?,
    @ColumnInfo(name = "location_latitude") val locationLatitude: Double?,
    @ColumnInfo(name = "location_longitude") val locationLongitude: Double?,
    @ColumnInfo(name = "location_address") val locationAddress: String?,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String
)