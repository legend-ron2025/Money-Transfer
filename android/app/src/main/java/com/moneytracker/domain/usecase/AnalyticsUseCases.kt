package com.moneytracker.domain.usecase

import com.moneytracker.domain.model.AnalyticsOverview
import com.moneytracker.domain.model.SpendingAnalytics
import com.moneytracker.domain.model.IncomeAnalytics
import com.moneytracker.domain.model.CashFlowAnalytics
import com.moneytracker.domain.model.TrendAnalytics
import com.moneytracker.domain.model.Insight
import com.moneytracker.domain.model.CategorySpend
import com.moneytracker.domain.repository.AnalyticsRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetOverviewUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    operator fun invoke(userId: String, period: String = "month"): Single<AnalyticsOverview> {
        return analyticsRepository.getOverview(userId, period)
    }
}

class GetSpendingAnalyticsUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    operator fun invoke(userId: String, period: String = "month"): Single<SpendingAnalytics> {
        return analyticsRepository.getSpendingAnalytics(userId, period)
    }
}

class GetIncomeAnalyticsUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    operator fun invoke(userId: String, period: String = "month"): Single<IncomeAnalytics> {
        return analyticsRepository.getIncomeAnalytics(userId, period)
    }
}

class GetCashFlowAnalyticsUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    operator fun invoke(userId: String, period: String = "month"): Single<CashFlowAnalytics> {
        return analyticsRepository.getCashFlowAnalytics(userId, period)
    }
}

class GetTrendAnalyticsUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    operator fun invoke(userId: String, period: String = "month", months: Int = 6): Single<TrendAnalytics> {
        return analyticsRepository.getTrendAnalytics(userId, period, months)
    }
}

class GetInsightsUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    operator fun invoke(userId: String): Single<List<Insight>> {
        return analyticsRepository.getInsights(userId)
    }
}

class GetCategoryBreakdownUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    operator fun invoke(userId: String, period: String = "month", type: String = "expense"): Single<List<CategorySpend>> {
        return analyticsRepository.getCategoryBreakdown(userId, period, type)
    }
}

class InvalidateAnalyticsCacheUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    operator fun invoke(userId: String): Completable {
        return analyticsRepository.invalidateCache(userId)
    }
}