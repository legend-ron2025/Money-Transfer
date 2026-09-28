package com.moneytracker.feature.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytracker.core.security.TokenManager
import com.moneytracker.domain.usecase.LoginUseCase
import com.moneytracker.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase:    LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val tokenManager:    TokenManager,
) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val deviceId = tokenManager.getDeviceId() ?: generateDeviceId()
                val user = loginUseCase(email.trim(), password, deviceId, null).blockingGet()
                _authState.postValue(AuthState.Success(user.id))
            } catch (e: Exception) {
                _authState.postValue(AuthState.Error(e.message ?: "Login failed"))
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val deviceId = tokenManager.getDeviceId() ?: generateDeviceId()
                val user = registerUseCase(email.trim(), password, name.trim(), deviceId, null).blockingGet()
                _authState.postValue(AuthState.Success(user.id))
            } catch (e: Exception) {
                _authState.postValue(AuthState.Error(e.message ?: "Registration failed"))
            }
        }
    }

    fun reset() { _authState.value = AuthState.Idle }

    private fun generateDeviceId(): String {
        val id = java.util.UUID.randomUUID().toString()
        tokenManager.setDeviceId(id)
        return id
    }

    sealed interface AuthState {
        object Idle    : AuthState
        object Loading : AuthState
        data class Success(val userId: String) : AuthState
        data class Error(val message: String)  : AuthState
    }
}
