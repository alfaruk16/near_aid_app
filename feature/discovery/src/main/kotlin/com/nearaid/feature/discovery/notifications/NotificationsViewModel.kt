package com.nearaid.feature.discovery.notifications

import androidx.lifecycle.viewModelScope
import com.nearaid.core.common.mvi.MviViewModel
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.GetNotificationsUseCase
import com.nearaid.core.domain.usecase.MarkNotificationsReadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getNotifications: GetNotificationsUseCase,
    private val markNotificationsRead: MarkNotificationsReadUseCase,
) : MviViewModel<NotificationsState, NotificationsIntent, NotificationsEffect>() {

    override fun initialState() = NotificationsState()

    override fun onIntent(intent: NotificationsIntent) {
        when (intent) {
            NotificationsIntent.Load -> load()
            NotificationsIntent.BackClicked -> sendEffect(NotificationsEffect.NavigateBack)
        }
    }

    private fun load() {
        viewModelScope.launch {
            setState { copy(loading = true, error = null) }
            when (val result = getNotifications()) {
                is DataResult.Success -> {
                    setState { copy(loading = false, notifications = result.data) }
                    markNotificationsRead()
                }
                is DataResult.Failure -> setState { copy(loading = false, error = result.error.message) }
            }
        }
    }
}
