package com.moneytracker.domain.usecase

import com.moneytracker.domain.model.User
import com.moneytracker.domain.repository.AuthRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(email: String, password: String, deviceId: String, fcmToken: String?): Single<User> {
        return authRepository.login(email, password, deviceId, fcmToken)
    }
}

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(email: String, password: String, name: String, deviceId: String, fcmToken: String?): Single<User> {
        return authRepository.register(email, password, name, deviceId, fcmToken)
    }
}

class RefreshTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(refreshToken: String): Single<User> {
        return authRepository.refreshToken(refreshToken)
    }
}

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Completable {
        return authRepository.logout()
    }
}

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Single<User?> {
        return authRepository.getCurrentUser()
    }
}

class ForgotPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(email: String): Completable {
        return authRepository.forgotPassword(email)
    }
}

class ResetPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(token: String, newPassword: String): Completable {
        return authRepository.resetPassword(token, newPassword)
    }
}

class UpdateUserPreferencesUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(preferences: UserPreferences): Completable {
        return authRepository.updatePreferences(preferences)
    }
}