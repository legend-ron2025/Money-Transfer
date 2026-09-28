package com.moneytracker.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytracker.core.common.AppExecutors
import com.moneytracker.core.security.TokenManager
import com.moneytracker.domain.model.AnalyticsOverview
import com.moneytracker.domain.model.Money
import com.moneytracker.domain.usecase.GetOverviewUseCase
import com.moneytracker.domain.usecase.GetTotalBalanceUseCase
import com.moneytracker.domain.usecase.GetActiveAccountsUseCase
import com.moneytracker.domain.usecase.GetActiveGoalsUseCase
import com.moneytracker.domain.usecase.GetActiveBudgetsUseCase
import com.moneytracker.domain.usecase.GetInsightsUseCase
import com.moneytracker.domain.usecase.GetUnreadNotificationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getOverviewUseCase: GetOverviewUseCase,
    private val getTotalBalanceUseCase: GetTotalBalanceUseCase,
    private val getActiveAccountsUseCase: GetActiveAccountsUseCase,
    private val getActiveGoalsUseCase: GetActiveGoalsUseCase,
    private val getActiveBudgetsUseCase: GetActiveBudgetsUseCase,
    private val getInsightsUseCase: GetInsightsUseCase,
    private val getUnreadNotificationsUseCase: GetUnreadNotificationsUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _overview = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val overview: androidx.lifecycle.LiveData<DashboardState> = _overview.asStateFlow().asLiveData()

    fun loadDashboard() {
        val userId = tokenManager.getUserId() ?: return
        
        viewModelScope.launch(AppExecutors.cpuIO()) {
            _overview.value = DashboardState.Loading
            
            try {
                val overview = getOverviewUseCase(userId, "month")
                val accounts = getActiveAccountsUseCase(userId).firstOrNull() ?: emptyList()
                val goals = getActiveGoalsUseCase(userId).firstOrNull() ?: emptyList()
                val budgets = getActiveBudgetsUseCase(userId).firstOrNull() ?: emptyList()
                val insights = getInsightsUseCase(userId).firstOrNull() ?: emptyList()
                val unreadCount = getUnreadNotificationsUseCase(userId, 5).firstOrNull()?.size ?: 0
                
                _overview.value = DashboardState.Success(
                    overview = overview,
                    accounts = accounts,
                    goals = goals,
                    budgets = budgets,
                    insights = insights,
                    unreadNotificationCount = unreadCount
                )
            } catch (e: Exception) {
                _overview.value = DashboardState.Error(e.message ?: "Failed to load dashboard")
            }
        }
    }

    sealed interface DashboardState {
        data class Loading : DashboardState
        data class Success(
            val overview: AnalyticsOverview,
            val accounts: List<com.moneytracker.domain.model.Account>,
            val goals: List<com.moneytracker.domain.model.Goal>,
            val budgets: List<com.moneytracker.domain.model.Budget>,
            val insights: List<com.moneytracker.domain.model.Insight>,
            val unreadNotificationCount: Int
        ) : DashboardState
        data class Error(val message: String) : DashboardState
    }
}