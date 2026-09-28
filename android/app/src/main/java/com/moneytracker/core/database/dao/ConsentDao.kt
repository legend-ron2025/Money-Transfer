package com.moneytracker.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.moneytracker.core.database.entity.ConsentEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe

@Dao
interface ConsentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertConsents(consents: List<ConsentEntity>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertConsent(consent: ConsentEntity): Completable

    @Update
    fun updateConsent(consent: ConsentEntity): Completable

    @Query("SELECT * FROM consents WHERE id = :consentId")
    fun getConsent(consentId: String): Maybe<ConsentEntity>

    @Query("SELECT * FROM consents WHERE consent_id = :consentId")
    fun getConsentByConsentId(consentId: String): Maybe<ConsentEntity>

    @Query("SELECT * FROM consents WHERE user_id = :userId ORDER BY granted_at DESC")
    fun getConsents(userId: String): Flowable<List<ConsentEntity>>

    @Query("SELECT * FROM consents WHERE user_id = :userId AND status = 'active'")
    fun getActiveConsents(userId: String): Flowable<List<ConsentEntity>>

    @Query("SELECT * FROM consents WHERE user_id = :userId AND provider_id = :providerId")
    fun getConsentsByProvider(userId: String, providerId: String): Flowable<List<ConsentEntity>>

    @Query("UPDATE consents SET status = :status, revoked_at = :revokedAt WHERE id = :consentId")
    fun updateConsentStatus(consentId: String, status: String, revokedAt: String?): Completable

    @Query("DELETE FROM consents WHERE id = :consentId")
    fun deleteConsent(consentId: String): Completable
}