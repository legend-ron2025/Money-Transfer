package com.moneytracker.data.repository

import com.moneytracker.core.database.MoneyTrackerDatabase
import com.moneytracker.core.database.dao.NotificationDao
import com.moneytracker.core.database.entity.NotificationEntity
import com.moneytracker.domain.model.Notification
import com.moneytracker.domain.repository.NotificationRepository
import com.moneytracker.domain.repository.PaginatedResult
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val database: MoneyTrackerDatabase,
    private val notificationDao: NotificationDao,
    private val notificationsApi: com.moneytracker.core.network.api.NotificationsApi
) : NotificationRepository {

    override fun getNotifications(userId: String, page: Int, limit: Int): Flowable<PaginatedResult<Notification>> {
        val offset = (page - 1) * limit
        return notificationDao.getNotifications(userId, limit, offset)
            .map { entities ->
                val items = entities.map { toDomain(it) }
                PaginatedResult(
                    items = items,
                    page = page,
                    limit = limit,
                    total = items.size.toLong(),
                    totalPages = 1,
                    hasNext = items.size == limit,
                    hasPrevious = page > 1
                )
            }
    }

    override fun getUnreadNotifications(userId: String, limit: Int): Flowable<List<Notification>> {
        return notificationDao.getUnreadNotifications(userId, limit)
            .map { entities -> entities.map { toDomain(it) } }
    }

    override fun getUnreadCount(userId: String): Single<Int> {
        return notificationDao.getUnreadCount(userId)
            .subscribeOn(Schedulers.io())
    }

    override fun insertNotification(notification: Notification): Completable {
        return Completable.fromAction {
            val entity = toEntity(notification)
            notificationDao.insertNotification(entity)
        }.subscribeOn(Schedulers.io())
    }

    override fun insertNotifications(notifications: List<Notification>): Completable {
        return Completable.fromAction {
            val entities = notifications.map { toEntity(it) }
            notificationDao.insertNotifications(entities)
        }.subscribeOn(Schedulers.io())
    }

    override fun markAsRead(notificationId: String): Completable {
        return Completable.fromAction {
            notificationDao.markAsRead(notificationId, java.time.Instant.now().toString())
        }.subscribeOn(Schedulers.io())
    }

    override fun markAllAsRead(userId: String): Completable {
        return Completable.fromAction {
            notificationDao.markAllAsRead(userId, java.time.Instant.now().toString())
        }.subscribeOn(Schedulers.io())
    }

    override fun deleteNotification(notificationId: String): Completable {
        return Completable.fromAction {
            notificationDao.deleteNotification(notificationId)
        }.subscribeOn(Schedulers.io())
    }

    override fun deleteOldNotifications(userId: String, cutoffDate: String): Completable {
        return Completable.fromAction {
            notificationDao.deleteOldNotifications(userId, cutoffDate)
        }.subscribeOn(Schedulers.io())
    }

    private fun toDomain(entity: NotificationEntity): Notification {
        return Notification(
            id = entity.id,
            userId = entity.user_id,
            type = com.moneytracker.domain.model.NotificationType.valueOf(entity.type),
            title = entity.title,
            body = entity.body,
            data = com.google.gson.Gson().fromJson(entity.data ?: "{}", Map::class.java),
            isRead = entity.is_read,
            priority = com.moneytracker.domain.model.NotificationPriority.valueOf(entity.priority),
            actionUrl = entity.action_url,
            imageUrl = entity.image_url,
            createdAt = entity.created_at,
            readAt = entity.read_at
        )
    }

    private fun toEntity(notification: Notification): NotificationEntity {
        return NotificationEntity(
            id = notification.id,
            user_id = notification.userId,
            type = notification.type.name,
            title = notification.title,
            body = notification.body,
            data = com.google.gson.Gson().toJson(notification.data),
            is_read = notification.isRead,
            priority = notification.priority.name,
            action_url = notification.actionUrl,
            image_url = notification.imageUrl,
            created_at = notification.createdAt,
            read_at = notification.readAt
        )
    }
}