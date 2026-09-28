package com.moneytracker.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.moneytracker.core.database.entity.NotificationEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotifications(notifications: List<NotificationEntity>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotification(notification: NotificationEntity): Completable

    @Update
    fun updateNotification(notification: NotificationEntity): Completable

    @Query("SELECT * FROM notifications WHERE id = :notificationId")
    fun getNotification(notificationId: String): Maybe<NotificationEntity>

    @Query("SELECT * FROM notifications WHERE user_id = :userId ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    fun getNotifications(userId: String, limit: Int, offset: Int): Flowable<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE user_id = :userId AND is_read = 0 ORDER BY created_at DESC LIMIT :limit")
    fun getUnreadNotifications(userId: String, limit: Int): Flowable<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE user_id = :userId AND is_read = 0")
    fun getUnreadCount(userId: String): Single<Int>

    @Query("UPDATE notifications SET is_read = 1, read_at = :readAt WHERE id = :notificationId")
    fun markAsRead(notificationId: String, readAt: String): Completable

    @Query("UPDATE notifications SET is_read = 1, read_at = :readAt WHERE user_id = :userId AND is_read = 0")
    fun markAllAsRead(userId: String, readAt: String): Completable

    @Query("DELETE FROM notifications WHERE id = :notificationId")
    fun deleteNotification(notificationId: String): Completable

    @Query("DELETE FROM notifications WHERE user_id = :userId AND created_at < :cutoffDate")
    fun deleteOldNotifications(userId: String, cutoffDate: String): Completable
}