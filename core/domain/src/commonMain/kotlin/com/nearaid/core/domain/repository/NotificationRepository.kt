package com.nearaid.core.domain.repository

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.model.NotificationItem

interface NotificationRepository {
    suspend fun getNotifications(): DataResult<List<NotificationItem>>
    suspend fun markAllRead(): DataResult<Unit>
}
