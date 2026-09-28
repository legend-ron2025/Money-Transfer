package com.moneytracker.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index
import androidx.room.ForeignKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "goals",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["status"]),
        Index(value = ["target_date"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@Serializable
data class GoalEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "target_amount") val targetAmount: Double,
    @ColumnInfo(name = "current_amount") val currentAmount: Double,
    @ColumnInfo(name = "currency") val currency: String,
    @ColumnInfo(name = "target_date") val targetDate: String,
    @ColumnInfo(name = "monthly_contribution") val monthlyContribution: Double?,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "icon") val icon: String?,
    @ColumnInfo(name = "color") val color: String?,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String
)