package com.nearaid.feature.discovery.notifications

import com.nearaid.core.model.NotificationItem
import com.nearaid.core.common.mvi.UiEffect
import com.nearaid.core.common.mvi.UiIntent
import com.nearaid.core.common.mvi.UiState

data class NotificationsState(
    val notifications: List<NotificationItem> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
) : UiState

sealed interface NotificationsIntent : UiIntent {
    data object Load : NotificationsIntent
    data object BackClicked : NotificationsIntent
}

sealed interface NotificationsEffect : UiEffect {
    data object NavigateBack : NotificationsEffect
}
