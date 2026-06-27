package com.nearaid.core.domain.repository

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.model.ChatMessage
import com.nearaid.core.model.Conversation
import com.nearaid.core.model.Page
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun getConversations(cursor: String? = null): DataResult<Page<Conversation>>
    suspend fun getMessages(claimId: String, cursor: String? = null): DataResult<Page<ChatMessage>>
    suspend fun sendMessage(claimId: String, body: String): DataResult<ChatMessage>
    suspend fun markRead(claimId: String): DataResult<Unit>
    /** Live messages for a thread over WebSocket (§10). */
    fun observeThread(threadId: String): Flow<ChatMessage>
}
