package com.nearaid.core.domain.usecase

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.repository.UserRepository
import com.nearaid.core.model.AppLanguage
import com.nearaid.core.model.Me
import com.nearaid.core.model.Page
import com.nearaid.core.model.PublicUser
import com.nearaid.core.model.Rating
import kotlinx.coroutines.flow.Flow

class ObserveCurrentUserUseCase(
    private val repository: UserRepository,
) {
    operator fun invoke(): Flow<Me?> = repository.observeMe()
}

class RefreshCurrentUserUseCase(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(): DataResult<Me> = repository.refreshMe()
}

class UpdateProfileUseCase(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(
        displayName: String? = null,
        language: AppLanguage? = null,
        photoUrl: String? = null,
        defaultArea: String? = null,
        email: String? = null,
    ): DataResult<Me> = repository.updateProfile(
        displayName = displayName?.trim(),
        language = language,
        photoUrl = photoUrl,
        defaultArea = defaultArea?.trim(),
        email = email?.trim()?.ifBlank { null },
    )
}

class GetPublicUserUseCase(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(id: String): DataResult<PublicUser> = repository.getPublicUser(id)
}

class GetUserRatingsUseCase(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(id: String, cursor: String? = null): DataResult<Page<Rating>> =
        repository.getUserRatings(id, cursor)
}

class SubmitVerificationUseCase(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(documentPath: String): DataResult<Unit> =
        repository.submitVerification(documentPath)
}

class RegisterDeviceUseCase(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(fcmToken: String): DataResult<Unit> =
        repository.registerDevice(fcmToken)
}
