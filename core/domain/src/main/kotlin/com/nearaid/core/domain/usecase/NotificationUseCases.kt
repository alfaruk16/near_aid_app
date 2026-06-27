package com.nearaid.core.domain.usecase

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.repository.NotificationRepository
import com.nearaid.core.model.NotificationItem
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository,
) {
    suspend operator fun invoke(): DataResult<List<NotificationItem>> = repository.getNotifications()
}

class MarkNotificationsReadUseCase @Inject constructor(
    private val repository: NotificationRepository,
) {
    suspend operator fun invoke(): DataResult<Unit> = repository.markAllRead()
}
