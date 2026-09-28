package com.moneytracker.feature.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytracker.core.common.AppExecutors
import com.moneytracker.core.security.TokenManager
import com.moneytracker.domain.model.Transaction
import com.moneytracker.domain.repository.TransactionFilter
import com.moneytracker.domain.repository.PaginatedResult
import com.moneytracker.domain.usecase.GetTransactionsUseCase
import com.moneytracker.domain.usecase.SearchTransactionsUseCase
import com.moneytracker.domain.usecase.GetCategoriesByTypeUseCase
import com.moneytracker.domain.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val searchTransactionsUseCase: SearchTransactionsUseCase,
    private val getCategoriesByTypeUseCase: GetCategoriesByTypeUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow<TransactionsState>(TransactionsState.Loading)
    val state: androidx.lifecycle.LiveData<TransactionsState> = _state.asStateFlow().asLiveData()

    private var currentFilter = TransactionFilter()
    private var currentPage = 1
    private var isLoading = false

    fun loadTransactions(filter: TransactionFilter = TransactionFilter(), page: Int = 1, append: Boolean = false) {
        val userId = tokenManager.getUserId() ?: return
        if (isLoading) return
        
        currentFilter = filter
        currentPage = page
        isLoading = true
        
        viewModelScope.launch(AppExecutors.cpuIO()) {
            if (!append) {
                _state.value = TransactionsState.Loading
            }
            
            try {
                val result = getTransactionsUseCase(userId, filter, page, 20).blockingFirst()
                
                val allTransactions = if (append) {
                    val current = (_state.value as? TransactionsState.Success)?.transactions ?: emptyList()
                    current + result.items
                } else {
                    result.items
                }
                
                _state.value = TransactionsState.Success(
                    transactions = allTransactions,
                    hasMore = result.hasNext,
                    currentPage = page,
                    filter = filter
                )
            } catch (e: Exception) {
                _state.value = TransactionsState.Error(e.message ?: "Failed to load transactions")
            } finally {
                isLoading = false
            }
        }
    }

    fun searchTransactions(query: String) {
        val userId = tokenManager.getUserId() ?: return
        
        viewModelScope.launch(AppExecutors.cpuIO()) {
            _state.value = TransactionsState.Loading
            
            try {
                val results = searchTransactionsUseCase(userId, query, 50).blockingFirst()
                _state.value = TransactionsState.Success(
                    transactions = results,
                    hasMore = false,
                    currentPage = 1,
                    filter = currentFilter
                )
            } catch (e: Exception) {
                _state.value = TransactionsState.Error(e.message ?: "Search failed")
            }
        }
    }

    fun loadCategories(): List<Category> {
        return getCategoriesByTypeUseCase(com.moneytracker.domain.model.Category.CategoryType.EXPENSE).blockingFirst() ?: emptyList()
    }

    sealed interface TransactionsState {
        data class Loading : TransactionsState
        data class Success(
            val transactions: List<Transaction>,
            val hasMore: Boolean,
            val currentPage: Int,
            val filter: TransactionFilter
        ) : TransactionsState
        data class Error(val message: String) : TransactionsState
    }
}