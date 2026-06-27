package com.nearaid.feature.messages.conversations

import androidx.lifecycle.viewModelScope
import com.nearaid.core.common.mvi.MviViewModel
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.GetConversationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationsViewModel @Inject constructor(
    private val getConversations: GetConversationsUseCase,
) : MviViewModel<ConversationsState, ConversationsIntent, ConversationsEffect>() {

    override fun initialState(): ConversationsState = ConversationsState()

    init {
        onIntent(ConversationsIntent.Load)
    }

    override fun onIntent(intent: ConversationsIntent) {
        when (intent) {
            ConversationsIntent.Load -> load()
            is ConversationsIntent.ConversationClicked -> sendEffect(
                ConversationsEffect.OpenChat(intent.claimId, intent.threadId, intent.title)
            )
        }
    }

    private fun load() {
        setState { copy(loading = true, error = null) }
        viewModelScope.launch {
            when (val result = getConversations()) {
                is DataResult.Success -> setState {
                    copy(loading = false, conversations = result.data.items)
                }
                is DataResult.Failure -> setState {
                    copy(loading = false, error = result.error.message)
                }
            }
        }
    }
}
