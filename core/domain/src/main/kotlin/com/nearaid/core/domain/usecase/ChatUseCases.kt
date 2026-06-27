package com.nearaid.core.domain.usecase

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.repository.ChatRepository
import com.nearaid.core.model.ChatMessage
import com.nearaid.core.model.Conversation
import com.nearaid.core.model.Page
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetConversationsUseCase @Inject constructor(
    private val repository: ChatRepository,
) {
    suspend operator fun invoke(cursor: String? = null): DataResult<Page<Conversation>> =
        repository.getConversations(cursor)
}

class GetMessagesUseCase @Inject constructor(
    private val repository: ChatRepository,
) {
    suspend operator fun invoke(claimId: String, cursor: String? = null): DataResult<Page<ChatMessage>> =
        repository.getMessages(claimId, cursor)
}

class SendMessageUseCase @Inject constructor(
    private val repository: ChatRepository,
) {
    suspend operator fun invoke(claimId: String, body: String): DataResult<ChatMessage> =
        repository.sendMessage(claimId, body.trim())
}

class MarkThreadReadUseCase @Inject constructor(
    private val repository: ChatRepository,
) {
    suspend operator fun invoke(claimId: String): DataResult<Unit> = repository.markRead(claimId)
}

class ObserveThreadUseCase @Inject constructor(
    private val repository: ChatRepository,
) {
    operator fun invoke(threadId: String): Flow<ChatMessage> = repository.observeThread(threadId)
}
