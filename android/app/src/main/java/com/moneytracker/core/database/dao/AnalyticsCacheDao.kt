package com.moneytracker.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.moneytracker.core.database.entity.AnalyticsCacheEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe

@Dao
interface AnalyticsCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCache(cache: AnalyticsCacheEntity): Completable

    @Query("SELECT * FROM analytics_cache WHERE user_id = :userId AND cache_key = :cacheKey")
    fun getCache(userId: String, cacheKey: String): Maybe<AnalyticsCacheEntity>

    @Query("SELECT * FROM analytics_cache WHERE user_id = :userId AND expires_at > :now")
    fun getValidCaches(userId: String, now: String): Flowable<List<AnalyticsCacheEntity>>

    @Query("DELETE FROM analytics_cache WHERE user_id = :userId AND cache_key = :cacheKey")
    fun invalidateCache(userId: String, cacheKey: String): Completable

    @Query("DELETE FROM analytics_cache WHERE user_id = :userId AND expires_at <= :now")
    fun cleanupExpiredCaches(userId: String, now: String): Completable

    @Query("DELETE FROM analytics_cache WHERE user_id = :userId")
    fun clearUserCaches(userId: String): Completable
}