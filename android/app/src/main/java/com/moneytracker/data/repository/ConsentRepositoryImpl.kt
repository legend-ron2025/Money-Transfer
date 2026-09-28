package com.moneytracker.data.repository

import com.moneytracker.core.database.MoneyTrackerDatabase
import com.moneytracker.core.database.dao.ConsentDao
import com.moneytracker.core.database.entity.ConsentEntity
import com.moneytracker.data.mapper.ConsentMapper
import com.moneytracker.domain.model.Consent
import com.moneytracker.domain.model.Provider
import com.moneytracker.domain.repository.ConsentRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConsentRepositoryImpl @Inject constructor(
    private val database: MoneyTrackerDatabase,
    private val consentDao: ConsentDao,
    private val consentsApi: com.moneytracker.core.network.api.ConsentsApi
) : ConsentRepository {

    override fun getConsents(userId: String): Flowable<List<Consent>> {
        return consentDao.getConsents(userId)
            .map { entities -> entities.map { ConsentMapper.toDomain(it) } }
    }

    override fun getActiveConsents(userId: String): Flowable<List<Consent>> {
        return consentDao.getActiveConsents(userId)
            .map { entities -> entities.map { ConsentMapper.toDomain(it) } }
    }

    override fun getConsent(consentId: String): Maybe<Consent> {
        return consentDao.getConsent(consentId)
            .map { ConsentMapper.toDomain(it) }
    }

    override fun getConsentByConsentId(consentId: String): Maybe<Consent> {
        return consentDao.getConsentByConsentId(consentId)
            .map { ConsentMapper.toDomain(it) }
    }

    override fun getConsentsByProvider(userId: String, providerId: String): Flowable<List<Consent>> {
        return consentDao.getConsentsByProvider(userId, providerId)
            .map { entities -> entities.map { ConsentMapper.toDomain(it) } }
    }

    override fun insertConsent(consent: Consent): Completable {
        return Completable.fromAction {
            val entity = toEntity(consent)
            consentDao.insertConsent(entity)
        }.subscribeOn(Schedulers.io())
    }

    override fun updateConsent(consent: Consent): Completable {
        return Completable.fromAction {
            val entity = toEntity(consent)
            consentDao.updateConsent(entity)
        }.subscribeOn(Schedulers.io())
    }

    override fun revokeConsent(consentId: String): Completable {
        return consentsApi.revokeConsent(consentId)
            .flatMapCompletable { response ->
                if (response.success) {
                    consentDao.updateConsentStatus(consentId, "REVOKED", java.time.Instant.now().toString())
                } else {
                    Completable.error(Exception(response.error?.message ?: "Failed to revoke consent"))
                }
            }
    }

    override fun getAvailableProviders(): Single<List<Provider>> {
        return consentsApi.getAvailableProviders()
            .map { response ->
                response.data?.map { ConsentMapper.toDomain(it) } ?: emptyList()
            }
    }

    override fun initiateConsent(providerId: String, accounts: List<String>, permissions: List<String>): Single<String> {
        val request = com.moneytracker.core.network.dto.CreateConsentRequest(
            providerId = providerId,
            accounts = accounts,
            permissions = permissions,
            redirectUrl = "moneytracker://consent-callback"
        )
        return consentsApi.createConsent(request)
            .map { response ->
                response.data?.consentId ?: throw Exception("Failed to create consent")
            }
    }

    override fun handleConsentCallback(consentId: String, code: String): Single<Consent> {
        return consentsApi.refreshConsent(consentId)
            .map { response ->
                ConsentMapper.toDomain(response.data!!)
            }
    }

    private fun toEntity(consent: Consent): ConsentEntity {
        return ConsentEntity(
            id = consent.id,
            userId = consent.userId,
            providerId = consent.provider.id,
            providerName = consent.provider.name,
            providerLogo = consent.provider.logo,
            consentId = consent.consentId,
            status = consent.status.name,
            grantedAt = consent.grantedAt,
            expiresAt = consent.expiresAt,
            revokedAt = consent.revokedAt,
            accounts = com.google.gson.Gson().toJson(consent.accounts),
            permissions = com.google.gson.Gson().toJson(consent.permissions),
            createdAt = java.time.Instant.now().toString(),
            updatedAt = java.time.Instant.now().toString()
        )
    }
}