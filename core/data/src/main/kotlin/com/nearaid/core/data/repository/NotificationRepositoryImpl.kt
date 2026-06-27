package com.nearaid.core.data.repository

import com.nearaid.core.network.util.safeApiCall
import com.nearaid.core.network.api.NotificationApi
import com.nearaid.core.data.mapper.toDomain
import com.nearaid.core.model.NotificationItem
import com.nearaid.core.domain.repository.NotificationRepository
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.common.dispatcher.Dispatcher
import com.nearaid.core.common.dispatcher.NearAidDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val notificationApi: NotificationApi,
    @Dispatcher(NearAidDispatcher.IO) private val ioDispatcher: CoroutineDispatcher,
) : NotificationRepository {

    override suspend fun getNotifications(): DataResult<List<NotificationItem>> =
        withContext(ioDispatcher) {
            safeApiCall { notificationApi.getNotifications().results.map { it.toDomain() } }
        }

    override suspend fun markAllRead(): DataResult<Unit> =
        withContext(ioDispatcher) { safeApiCall { notificationApi.markAllRead() } }
}
