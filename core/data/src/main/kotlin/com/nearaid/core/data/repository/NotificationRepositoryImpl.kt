package com.nearaid.core.data.repository

import com.nearaid.core.network.util.safeApiCall
import com.nearaid.core.network.api.NotificationApi
import com.nearaid.core.data.mapper.toDomain
import com.nearaid.core.model.NotificationItem
import com.nearaid.core.domain.repository.NotificationRepository
import com.nearaid.core.common.result.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class NotificationRepositoryImpl(
    private val notificationApi: NotificationApi,
    private val ioDispatcher: CoroutineDispatcher,
) : NotificationRepository {

    override suspend fun getNotifications(): DataResult<List<NotificationItem>> =
        withContext(ioDispatcher) {
            safeApiCall { notificationApi.getNotifications().results.map { it.toDomain() } }
        }

    override suspend fun markAllRead(): DataResult<Unit> =
        withContext(ioDispatcher) { safeApiCall { notificationApi.markAllRead() } }
}
