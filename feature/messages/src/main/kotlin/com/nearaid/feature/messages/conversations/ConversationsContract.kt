package com.nearaid.feature.messages.conversations

import com.nearaid.core.common.mvi.UiEffect
import com.nearaid.core.common.mvi.UiIntent
import com.nearaid.core.common.mvi.UiState
import com.nearaid.core.model.Conversation

// ─── State ────────────────────────────────────────────────────────────────────

data class ConversationsState(
    val conversations: List<Conversation> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
) : UiState

// ─── Intent ───────────────────────────────────────────────────────────────────

sealed interface ConversationsIntent : UiIntent {
    data object Load : ConversationsIntent
    data class ConversationClicked(
        val claimId: String,
        val threadId: String,
        val title: String,
    ) : ConversationsIntent
}

// ─── Effect ───────────────────────────────────────────────────────────────────

sealed interface ConversationsEffect : UiEffect {
    data class OpenChat(
        val claimId: String,
        val threadId: String,
        val title: String,
    ) : ConversationsEffect
}
