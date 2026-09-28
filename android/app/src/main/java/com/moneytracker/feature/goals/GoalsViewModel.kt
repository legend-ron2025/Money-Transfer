package com.moneytracker.feature.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytracker.core.common.AppExecutors
import com.moneytracker.core.security.TokenManager
import com.moneytracker.domain.model.Goal
import com.moneytracker.domain.model.Money
import com.moneytracker.domain.usecase.GetActiveGoalsUseCase
import com.moneytracker.domain.usecase.CreateGoalUseCase
import com.moneytracker.domain.usecase.UpdateGoalUseCase
import com.moneytracker.domain.usecase.DeleteGoalUseCase
import com.moneytracker.domain.usecase.UpdateGoalProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val getActiveGoalsUseCase: GetActiveGoalsUseCase,
    private val createGoalUseCase: CreateGoalUseCase,
    private val updateGoalUseCase: UpdateGoalUseCase,
    private val deleteGoalUseCase: DeleteGoalUseCase,
    private val updateGoalProgressUseCase: UpdateGoalProgressUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow<GoalsState>(GoalsState.Loading)
    val state: androidx.lifecycle.LiveData<GoalsState> = _state.asStateFlow().asLiveData()

    fun loadGoals() {
        val userId = tokenManager.getUserId() ?: return
        
        viewModelScope.launch(AppExecutors.cpuIO()) {
            _state.value = GoalsState.Loading
            
            try {
                val goals = getActiveGoalsUseCase(userId).firstOrNull() ?: emptyList()
                _state.value = GoalsState.Success(goals = goals)
            } catch (e: Exception) {
                _state.value = GoalsState.Error(e.message ?: "Failed to load goals")
            }
        }
    }

    fun createGoal(goal: Goal) {
        val userId = tokenManager.getUserId() ?: return
        
        viewModelScope.launch(AppExecutors.networkIO()) {
            try {
                createGoalUseCase(goal).blockingAwait()
                loadGoals()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateGoal(goal: Goal) {
        viewModelScope.launch(AppExecutors.networkIO()) {
            try {
                updateGoalUseCase(goal).blockingAwait()
                loadGoals()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteGoal(goalId: String) {
        viewModelScope.launch(AppExecutors.networkIO()) {
            try {
                deleteGoalUseCase(goalId).blockingAwait()
                loadGoals()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun contributeToGoal(goalId: String, amount: Money) {
        viewModelScope.launch(AppExecutors.networkIO()) {
            try {
                updateGoalProgressUseCase(goalId, amount).blockingAwait()
                loadGoals()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    sealed interface GoalsState {
        data class Loading : GoalsState
        data class Success(val goals: List<Goal>) : GoalsState
        data class Error(val message: String) : GoalsState
    }
}