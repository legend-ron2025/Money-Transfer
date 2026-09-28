package com.moneytracker.feature.budgets

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytracker.core.security.TokenManager
import com.moneytracker.domain.model.Budget
import com.moneytracker.domain.usecase.CreateBudgetUseCase
import com.moneytracker.domain.usecase.DeleteBudgetUseCase
import com.moneytracker.domain.usecase.GetActiveBudgetsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetsViewModel @Inject constructor(
    private val getActiveBudgetsUseCase: GetActiveBudgetsUseCase,
    private val createBudgetUseCase:     CreateBudgetUseCase,
    private val deleteBudgetUseCase:     DeleteBudgetUseCase,
    private val tokenManager:            TokenManager,
) : ViewModel() {

    private val _state = MutableLiveData<BudgetsState>(BudgetsState.Loading)
    val state: LiveData<BudgetsState> = _state

    fun loadBudgets() {
        val userId = tokenManager.getUserId() ?: return
        _state.value = BudgetsState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val budgets = getActiveBudgetsUseCase(userId).blockingFirst()
                _state.postValue(BudgetsState.Success(budgets))
            } catch (e: Exception) {
                _state.postValue(BudgetsState.Error(e.message ?: "Failed"))
            }
        }
    }

    fun createBudget(categoryName: String, amount: Double, period: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Minimal budget creation — full implementation uses CreateBudgetUseCase with a Budget object
                loadBudgets()
            } catch (_: Exception) {}
        }
    }

    fun deleteBudget(budgetId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                deleteBudgetUseCase(budgetId).blockingAwait()
                loadBudgets()
            } catch (_: Exception) {}
        }
    }

    sealed interface BudgetsState {
        object Loading : BudgetsState
        data class Success(val budgets: List<Budget>) : BudgetsState
        data class Error(val message: String) : BudgetsState
    }
}
