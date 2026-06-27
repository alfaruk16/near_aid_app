package com.nearaid.feature.messages.chat

import com.nearaid.core.common.mvi.UiEffect
import com.nearaid.core.common.mvi.UiIntent
import com.nearaid.core.common.mvi.UiState
import com.nearaid.core.model.ChatMessage

// ─── State ────────────────────────────────────────────────────────────────────

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val myUserId: String? = null,
    val loading: Boolean = false,
    val sending: Boolean = false,
    val error: String? = null,
) : UiState

// ─── Intent ───────────────────────────────────────────────────────────────────

sealed interface ChatIntent : UiIntent {
    data class Start(val claimId: String, val threadId: String) : ChatIntent
    data class InputChanged(val text: String) : ChatIntent
    data class Send(val claimId: String) : ChatIntent
    data object BackClicked : ChatIntent
}

// ─── Effect ───────────────────────────────────────────────────────────────────

sealed interface ChatEffect : UiEffect {
    data object NavigateBack : ChatEffect
}
