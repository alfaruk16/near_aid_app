package com.nearaid.core.domain.repository

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.model.AppLanguage
import com.nearaid.core.model.Me
import com.nearaid.core.model.Page
import com.nearaid.core.model.PublicUser
import com.nearaid.core.model.Rating
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeMe(): Flow<Me?>
    /** Drops the in-memory cached [Me] so it can't leak across accounts (e.g. on logout). */
    fun clear()
    suspend fun refreshMe(): DataResult<Me>
    suspend fun updateProfile(
        displayName: String? = null,
        language: AppLanguage? = null,
        photoUrl: String? = null,
        defaultArea: String? = null,
        email: String? = null,
    ): DataResult<Me>
    suspend fun getPublicUser(id: String): DataResult<PublicUser>
    suspend fun getUserRatings(id: String, cursor: String? = null): DataResult<Page<Rating>>
    suspend fun submitVerification(documentPath: String): DataResult<Unit>
    suspend fun registerDevice(fcmToken: String): DataResult<Unit>
}
