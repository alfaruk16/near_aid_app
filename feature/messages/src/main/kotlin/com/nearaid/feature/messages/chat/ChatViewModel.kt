package com.nearaid.feature.messages.chat

import androidx.lifecycle.viewModelScope
import com.nearaid.core.common.mvi.MviViewModel
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.GetMessagesUseCase
import com.nearaid.core.domain.usecase.MarkThreadReadUseCase
import com.nearaid.core.domain.usecase.ObserveCurrentUserUseCase
import com.nearaid.core.domain.usecase.ObserveThreadUseCase
import com.nearaid.core.domain.usecase.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getMessages: GetMessagesUseCase,
    private val sendMessage: SendMessageUseCase,
    private val markThreadRead: MarkThreadReadUseCase,
    private val observeThread: ObserveThreadUseCase,
    private val observeCurrentUser: ObserveCurrentUserUseCase,
) : MviViewModel<ChatState, ChatIntent, ChatEffect>() {

    override fun initialState(): ChatState = ChatState()

    init {
        viewModelScope.launch {
            observeCurrentUser()
                .filterNotNull()
                .collect { me ->
                    setState { copy(myUserId = me.id) }
                }
        }
    }

    override fun onIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.Start -> start(intent.claimId, intent.threadId)
            is ChatIntent.InputChanged -> setState { copy(inputText = intent.text) }
            is ChatIntent.Send -> sendMessage(intent.claimId)
            ChatIntent.BackClicked -> sendEffect(ChatEffect.NavigateBack)
        }
    }

    private fun start(claimId: String, threadId: String) {
        setState { copy(loading = true, error = null) }
        viewModelScope.launch {
            // Load message history
            when (val result = getMessages(claimId)) {
                is DataResult.Success -> setState {
                    copy(loading = false, messages = result.data.items)
                }
                is DataResult.Failure -> setState {
                    copy(loading = false, error = result.error.message)
                }
            }

            // Mark as read (fire-and-forget — ignore result)
            markThreadRead(claimId)

            // Observe live thread messages
            observeThread(threadId).collect { newMsg ->
                setState {
                    val exists = messages.any { it.id == newMsg.id }
                    if (exists) this
                    else copy(messages = messages + newMsg)
                }
            }
        }
    }

    private fun sendMessage(claimId: String) {
        val text = currentState.inputText.trim()
        if (text.isBlank()) return
        setState { copy(sending = true, inputText = "") }
        viewModelScope.launch {
            when (val result = sendMessage(claimId, text)) {
                is DataResult.Success -> {
                    val sentMsg = result.data
                    setState {
                        val exists = messages.any { it.id == sentMsg.id }
                        copy(
                            sending = false,
                            messages = if (exists) messages else messages + sentMsg,
                        )
                    }
                }
                is DataResult.Failure -> setState {
                    copy(sending = false, error = result.error.message)
                }
            }
        }
    }
}
