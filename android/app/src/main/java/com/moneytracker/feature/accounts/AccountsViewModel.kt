package com.moneytracker.feature.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytracker.core.common.AppExecutors
import com.moneytracker.core.security.TokenManager
import com.moneytracker.domain.model.Account
import com.moneytracker.domain.usecase.GetActiveAccountsUseCase
import com.moneytracker.domain.usecase.GetTotalBalanceUseCase
import com.moneytracker.domain.usecase.SyncAllAccountsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val getActiveAccountsUseCase: GetActiveAccountsUseCase,
    private val getTotalBalanceUseCase: GetTotalBalanceUseCase,
    private val syncAllAccountsUseCase: SyncAllAccountsUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow<AccountsState>(AccountsState.Loading)
    val state: androidx.lifecycle.LiveData<AccountsState> = _state.asStateFlow().asLiveData()

    fun loadAccounts() {
        val userId = tokenManager.getUserId() ?: return
        
        viewModelScope.launch(AppExecutors.cpuIO()) {
            _state.value = AccountsState.Loading
            
            try {
                val accounts = getActiveAccountsUseCase(userId).firstOrNull() ?: emptyList()
                val totalBalance = getTotalBalanceUseCase(userId).blockingFirst()
                
                _state.value = AccountsState.Success(accounts = accounts, totalBalance = totalBalance)
            } catch (e: Exception) {
                _state.value = AccountsState.Error(e.message ?: "Failed to load accounts")
            }
        }
    }

    fun syncAllAccounts() {
        val userId = tokenManager.getUserId() ?: return
        
        viewModelScope.launch(AppExecutors.networkIO()) {
            try {
                syncAllAccountsUseCase(userId).blockingAwait()
                loadAccounts()
            } catch (e: Exception) {
                // Handle sync error
            }
        }
    }

    sealed interface AccountsState {
        data class Loading : AccountsState
        data class Success(val accounts: List<Account>, val totalBalance: com.moneytracker.domain.model.Money) : AccountsState
        data class Error(val message: String) : AccountsState
    }
}