package com.moneytracker.feature.analytics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytracker.core.security.TokenManager
import com.moneytracker.domain.model.AnalyticsOverview
import com.moneytracker.domain.usecase.GetOverviewUseCase
import com.moneytracker.domain.usecase.GetTrendAnalyticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getOverviewUseCase:   GetOverviewUseCase,
    private val getTrendUseCase:      GetTrendAnalyticsUseCase,
    private val tokenManager:         TokenManager,
) : ViewModel() {

    private val _state = MutableLiveData<AnalyticsState>(AnalyticsState.Loading)
    val analyticsState: LiveData<AnalyticsState> = _state

    fun loadAnalytics(period: String = "month") {
        val userId = tokenManager.getUserId() ?: return
        _state.value = AnalyticsState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val overview = getOverviewUseCase(userId, period).blockingGet()
                val trend    = getTrendUseCase(userId, period, 6).blockingGet()

                val monthly = (trend as? com.moneytracker.domain.model.TrendAnalytics)
                    ?.monthlyIncome
                    ?.mapIndexed { i, mv ->
                        MonthlyData(
                            month   = mv.month,
                            income  = mv.value.amount,
                            expense = trend.monthlyExpense.getOrNull(i)?.value?.amount ?: 0.0
                        )
                    } ?: emptyList()

                _state.postValue(AnalyticsState.Success(overview, monthly))
            } catch (e: Exception) {
                _state.postValue(AnalyticsState.Error(e.message ?: "Failed to load analytics"))
            }
        }
    }

    data class MonthlyData(val month: String, val income: Double, val expense: Double)

    sealed interface AnalyticsState {
        object Loading : AnalyticsState
        data class Success(val overview: AnalyticsOverview, val monthlyTrend: List<MonthlyData>) : AnalyticsState
        data class Error(val message: String) : AnalyticsState
    }
}
