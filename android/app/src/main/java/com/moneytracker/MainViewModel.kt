package com.moneytracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytracker.core.security.BiometricManager
import com.moneytracker.core.security.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val biometricManager: BiometricManager
) : ViewModel() {

    fun authenticateWithBiometric() {
        if (tokenManager.isBiometricEnabled() && biometricManager.isBiometricAvailable()) {
            biometricManager.authenticate("Unlock MoneyTracker") { result ->
                when (result) {
                    is BiometricManager.BiometricResult.Success -> {
                        // App unlocked
                    }
                    is BiometricManager.BiometricResult.Error -> {
                        // Handle error
                    }
                    is BiometricManager.BiometricResult.Cancelled -> {
                        // User cancelled, maybe close app or show PIN
                    }
                }
            }
        }
    }
}

import com.moneytracker.core.security.BiometricManager.BiometricResult