package com.moneytracker.feature.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytracker.core.common.AppExecutors
import com.moneytracker.domain.model.Transaction
import com.moneytracker.domain.usecase.GetTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    private val getTransactionUseCase: GetTransactionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<TransactionDetailState>(TransactionDetailState.Loading)
    val state: androidx.lifecycle.LiveData<TransactionDetailState> = _state.asStateFlow().asLiveData()

    fun loadTransaction(transactionId: String) {
        viewModelScope.launch(AppExecutors.cpuIO()) {
            _state.value = TransactionDetailState.Loading
            
            try {
                val transaction = getTransactionUseCase(transactionId).blockingFirst()
                _state.value = TransactionDetailState.Success(transaction)
            } catch (e: Exception) {
                _state.value = TransactionDetailState.Error(e.message ?: "Failed to load transaction")
            }
        }
    }

    sealed interface TransactionDetailState {
        data class Loading : TransactionDetailState
        data class Success(val transaction: Transaction) : TransactionDetailState
        data class Error(val message: String) : TransactionDetailState
    }
}