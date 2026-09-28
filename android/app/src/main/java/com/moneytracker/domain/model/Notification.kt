package com.moneytracker.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: String,
    val userId: String,
    val type: NotificationType,
    val title: String,
    val body: String,
    val data: Map<String, String>?,
    val isRead: Boolean,
    val priority: NotificationPriority,
    val actionUrl: String?,
    val imageUrl: String?,
    val createdAt: String,
    val readAt: String?
) {
    val isUnread: Boolean
        get() = !isRead

    val timeAgo: String
        get() = calculateTimeAgo()

    private fun calculateTimeAgo(): String {
        val created = java.time.Instant.parse(createdAt)
        val now = java.time.Instant.now()
        val diff = java.time.Duration.between(created, now)

        return when {
            diff.toMinutes() < 1 -> "Just now"
            diff.toHours() < 1 -> "${diff.toMinutes()}m ago"
            diff.toDays() < 1 -> "${diff.toHours()}h ago"
            diff.toDays() < 7 -> "${diff.toDays()}d ago"
            else -> java.time.format.DateTimeFormatter.ofPattern("MMM d").format(created.atZone(java.time.ZoneId.systemDefault()))
        }
    }
}

enum class NotificationType {
    TRANSACTION, BUDGET, SUBSCRIPTION, GOAL, SYNC, SECURITY, MARKETING, INSIGHT
}

enum class NotificationPriority {
    LOW, NORMAL, HIGH, URGENT
}