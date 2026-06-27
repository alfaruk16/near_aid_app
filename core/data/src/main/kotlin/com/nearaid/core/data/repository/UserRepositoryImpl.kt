package com.nearaid.core.data.repository

import com.nearaid.core.common.dispatcher.Dispatcher
import com.nearaid.core.common.dispatcher.NearAidDispatcher
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.common.result.map
import com.nearaid.core.data.mapper.toDomain
import com.nearaid.core.domain.repository.UserRepository
import com.nearaid.core.model.AppLanguage
import com.nearaid.core.model.Me
import com.nearaid.core.model.Page
import com.nearaid.core.model.PublicUser
import com.nearaid.core.model.Rating
import com.nearaid.core.network.api.UserApi
import com.nearaid.core.network.dto.DeviceBody
import com.nearaid.core.network.dto.PatchMeBody
import com.nearaid.core.network.util.safeApiCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    @Dispatcher(NearAidDispatcher.IO) private val ioDispatcher: CoroutineDispatcher,
) : UserRepository {

    private val me = MutableStateFlow<Me?>(null)

    override fun observeMe(): Flow<Me?> = me.asStateFlow()

    override suspend fun refreshMe(): DataResult<Me> = withContext(ioDispatcher) {
        safeApiCall { userApi.getMe().toDomain() }.also {
            if (it is DataResult.Success) me.value = it.data
        }
    }

    override suspend fun updateProfile(
        displayName: String?,
        language: AppLanguage?,
        photoUrl: String?,
        defaultArea: String?,
        email: String?,
    ): DataResult<Me> = withContext(ioDispatcher) {
        val body = PatchMeBody(
            displayName = displayName,
            language = language?.code,
            photoUrl = photoUrl,
            defaultArea = defaultArea,
            email = email,
        )
        safeApiCall { userApi.updateMe(body).toDomain() }.also {
            if (it is DataResult.Success) me.value = it.data
        }
    }

    override suspend fun getPublicUser(id: String): DataResult<PublicUser> =
        withContext(ioDispatcher) { safeApiCall { userApi.getPublicUser(id).toDomain() } }

    override suspend fun getUserRatings(id: String, cursor: String?): DataResult<Page<Rating>> =
        withContext(ioDispatcher) {
            safeApiCall { userApi.getRatings(id, cursor) }.map { page ->
                Page(page.results.map { it.toDomain() }, page.nextCursor, page.hasMore)
            }
        }

    override suspend fun submitVerification(documentPath: String): DataResult<Unit> =
        withContext(ioDispatcher) {
            safeApiCall {
                val file = File(documentPath)
                val body = file.asRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("document", file.name, body)
                userApi.submitVerification(part)
            }
        }

    override suspend fun registerDevice(fcmToken: String): DataResult<Unit> =
        withContext(ioDispatcher) { safeApiCall { userApi.registerDevice(DeviceBody(fcmToken)) } }
}
