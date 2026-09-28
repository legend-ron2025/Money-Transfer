package com.moneytracker.feature.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.moneytracker.core.security.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val tokenManager: TokenManager) : ViewModel() {

    data class Prefs(
        val appLockEnabled: Boolean = false,
        val biometricEnabled: Boolean = false,
        val autoSync: Boolean = true,
        val theme: String = "System",
        val txnAlerts: Boolean = true,
        val budgetAlerts: Boolean = true,
        val subAlerts: Boolean = true,
    )

    private val _prefs = MutableLiveData(load())
    val prefs: LiveData<Prefs> = _prefs

    private fun load() = Prefs(
        appLockEnabled  = tokenManager.isAppLockEnabled(),
        biometricEnabled = tokenManager.isBiometricEnabled(),
    )

    private fun update(fn: (Prefs) -> Prefs) { _prefs.value = fn(_prefs.value ?: Prefs()) }

    fun setAppLock(v: Boolean)     { tokenManager.setAppLockEnabled(v);   update { it.copy(appLockEnabled = v) } }
    fun setBiometric(v: Boolean)   { tokenManager.setBiometricEnabled(v); update { it.copy(biometricEnabled = v) } }
    fun setAutoSync(v: Boolean)    { update { it.copy(autoSync = v) } }
    fun setTxnAlerts(v: Boolean)   { update { it.copy(txnAlerts = v) } }
    fun setBudgetAlerts(v: Boolean){ update { it.copy(budgetAlerts = v) } }
    fun setSubAlerts(v: Boolean)   { update { it.copy(subAlerts = v) } }
}
