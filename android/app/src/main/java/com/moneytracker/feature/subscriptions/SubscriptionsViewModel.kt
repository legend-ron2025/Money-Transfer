package com.moneytracker.feature.subscriptions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytracker.core.security.TokenManager
import com.moneytracker.domain.usecase.GetInsightsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val getInsightsUseCase: GetInsightsUseCase,
) : ViewModel() {

    private val _state = MutableLiveData<SubState>(SubState.Loading)
    val state: LiveData<SubState> = _state

    data class Subscription(
        val merchant:        String,
        val amount:          Double,
        val frequency:       String,
        val nextPaymentDate: String,
        val annualCost:      Double,
    )

    fun load() {
        val userId = tokenManager.getUserId() ?: return
        _state.value = SubState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Fetch subscriptions from analytics endpoint via repository
                // For now we generate from insights / recurring transactions
                val insights = getInsightsUseCase(userId).blockingGet()
                    .filter { it.type.name == "SUBSCRIPTION_UPCOMING" }

                val subs = insights.map { ins ->
                    Subscription(
                        merchant        = ins.title.substringBefore(" due").substringBefore(" subscription"),
                        amount          = ins.value?.amount ?: 0.0,
                        frequency       = "MONTHLY",
                        nextPaymentDate = java.time.LocalDate.now().plusDays(7).toString(),
                        annualCost      = (ins.value?.amount ?: 0.0) * 12
                    )
                }
                val total  = subs.sumOf { it.amount }
                _state.postValue(SubState.Success(subs, total, total * 12))
            } catch (e: Exception) {
                _state.postValue(SubState.Error(e.message ?: "Failed to load subscriptions"))
            }
        }
    }

    sealed interface SubState {
        object Loading : SubState
        data class Success(val subscriptions: List<Subscription>, val totalMonthly: Double, val totalAnnual: Double) : SubState
        data class Error(val message: String) : SubState
    }
}
