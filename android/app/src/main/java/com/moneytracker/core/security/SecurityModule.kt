package com.moneytracker.core.security

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.rxjava3.RxDataStore
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.moneytracker.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.Singleton
import dagger.Module
import dagger.Provides
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    private const val ENCRYPTED_PREFS_NAME = "encrypted_prefs"
    private const val MASTER_KEY_ALIAS = "master_key"
    private const val BIOMETRIC_KEY_ALIAS = "biometric_key"
    private const val DATASTORE_NAME = "security_prefs"

    private const val KEY_DEVICE_ID = preferencesKey<String>("device_id")
    private const val KEY_ACCESS_TOKEN = preferencesKey<String>("access_token")
    private const val KEY_REFRESH_TOKEN = preferencesKey<String>("refresh_token")
    private const val KEY_USER_ID = preferencesKey<String>("user_id")
    private const val KEY_BIOMETRIC_ENABLED = preferencesKey<Boolean>("biometric_enabled")
    private const val KEY_APP_LOCK_ENABLED = preferencesKey<Boolean>("app_lock_enabled")

    @Provides
    @Singleton
    fun provideRxDataStore(@ApplicationContext context: Context): RxDataStore<Preferences> {
        return RxPreferenceDataStoreBuilder(context, DATASTORE_NAME).build()
    }

    @Provides
    @Singleton
    fun provideTokenManager(
        rxDataStore: RxDataStore<Preferences>,
        encryptedPrefs: SharedPreferences
    ): TokenManager {
        return TokenManagerImpl(rxDataStore, encryptedPrefs)
    }

    @Provides
    @Singleton
    fun provideBiometricManager(@ApplicationContext context: Context): BiometricManager {
        return BiometricManagerImpl(context)
    }

    @Provides
    @Singleton
    fun provideEncryptedPrefs(@ApplicationContext context: Context): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(KeyGenParameterSpec.Builder(
            MASTER_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build())

        return EncryptedSharedPreferences.create(
            ENCRYPTED_PREFS_NAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Provides
    @Singleton
    fun provideKeystore(): KeyStore {
        return KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    }

    fun initialize(context: Context) {
        // Initialize security module
    }
}

interface TokenManager {
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun setTokens(accessToken: String, refreshToken: String, expiresIn: Long)
    fun clearTokens()
    fun refreshAccessToken(refreshToken: String): Boolean
    fun getDeviceId(): String?
    fun setDeviceId(deviceId: String)
    fun getUserId(): String?
    fun setUserId(userId: String)
    fun isBiometricEnabled(): Boolean
    fun setBiometricEnabled(enabled: Boolean)
    fun isAppLockEnabled(): Boolean
    fun setAppLockEnabled(enabled: Boolean)
}

class TokenManagerImpl @Inject constructor(
    private val rxDataStore: RxDataStore<Preferences>,
    private val encryptedPrefs: SharedPreferences
) : TokenManager {

    override fun getAccessToken(): String? {
        return encryptedPrefs.getString("access_token", null)
    }

    override fun getRefreshToken(): String? {
        return encryptedPrefs.getString("refresh_token", null)
    }

    override fun setTokens(accessToken: String, refreshToken: String, expiresIn: Long) {
        encryptedPrefs.edit()
            .putString("access_token", accessToken)
            .putString("refresh_token", refreshToken)
            .putLong("token_expires_at", System.currentTimeMillis() + expiresIn * 1000)
            .apply()
    }

    override fun clearTokens() {
        encryptedPrefs.edit()
            .remove("access_token")
            .remove("refresh_token")
            .remove("token_expires_at")
            .apply()
        rxDataStore.updateDataAsync { preferences ->
            preferences.mutate {
                it[KEY_USER_ID] = null
            }
        }
    }

    override fun refreshAccessToken(refreshToken: String): Boolean {
        // This would call the auth API to refresh the token
        // For now, return false to force re-login
        return false
    }

    override fun getDeviceId(): String? {
        return encryptedPrefs.getString("device_id", null)
    }

    override fun setDeviceId(deviceId: String) {
        encryptedPrefs.edit().putString("device_id", deviceId).apply()
    }

    override fun getUserId(): String? {
        return encryptedPrefs.getString("user_id", null)
    }

    override fun setUserId(userId: String) {
        encryptedPrefs.edit().putString("user_id", userId).apply()
        rxDataStore.updateDataAsync { preferences ->
            preferences.mutate {
                it[KEY_USER_ID] = userId
            }
        }
    }

    override fun isBiometricEnabled(): Boolean {
        return encryptedPrefs.getBoolean("biometric_enabled", false)
    }

    override fun setBiometricEnabled(enabled: Boolean) {
        encryptedPrefs.edit().putBoolean("biometric_enabled", enabled).apply()
    }

    override fun isAppLockEnabled(): Boolean {
        return encryptedPrefs.getBoolean("app_lock_enabled", false)
    }

    override fun setAppLockEnabled(enabled: Boolean) {
        encryptedPrefs.edit().putBoolean("app_lock_enabled", enabled).apply()
    }
}

interface BiometricManager {
    fun authenticate(reason: String, callback: BiometricCallback)
    fun isBiometricAvailable(): Boolean
    fun hasEnrolledBiometrics(): Boolean
}

interface BiometricCallback {
    fun onSuccess()
    fun onError(errorCode: Int, errorMessage: String)
    fun onCancel()
}

class BiometricManagerImpl @Inject constructor(
    private val context: Context
) : BiometricManager {

    override fun authenticate(reason: String, callback: BiometricCallback) {
        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = androidx.biometric.BiometricPrompt(
            context as androidx.fragment.app.FragmentActivity,
            executor,
            object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                    callback.onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    callback.onError(errorCode, errString.toString())
                }

                override fun onAuthenticationFailed() {
                    callback.onError(-1, "Authentication failed")
                }
            }
        )

        val promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
            .setTitle("MoneyTracker")
            .setSubtitle(reason)
            .setNegativeButtonText("Cancel")
            .setDeviceCredentialAllowed(true)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    override fun isBiometricAvailable(): Boolean {
        val biometricManager = androidx.biometric.BiometricManager.from(context)
        return biometricManager.canAuthenticate(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
               androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
    }

    override fun hasEnrolledBiometrics(): Boolean {
        val biometricManager = androidx.biometric.BiometricManager.from(context)
        return biometricManager.canAuthenticate(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
               androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
    }
}

import androidx.core.content.ContextCompat