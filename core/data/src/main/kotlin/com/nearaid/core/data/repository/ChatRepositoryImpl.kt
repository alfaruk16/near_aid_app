package com.nearaid.core.data.repository

import com.nearaid.core.network.socket.ChatSocket
import com.nearaid.core.network.util.safeApiCall
import com.nearaid.core.database.dao.ConversationCacheDao
import com.nearaid.core.data.mapper.toDomain
import com.nearaid.core.data.mapper.toEntity
import com.nearaid.core.network.api.ChatApi
import com.nearaid.core.network.api.ClaimApi
import com.nearaid.core.network.dto.SendMessageBody
import com.nearaid.core.data.mapper.toDomain
import com.nearaid.core.model.ChatMessage
import com.nearaid.core.model.Conversation
import com.nearaid.core.model.Page
import com.nearaid.core.domain.repository.ChatRepository
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.common.dispatcher.Dispatcher
import com.nearaid.core.common.dispatcher.NearAidDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi,
    private val claimApi: ClaimApi,
    private val chatSocket: ChatSocket,
    private val cacheDao: ConversationCacheDao,
    @Dispatcher(NearAidDispatcher.IO) private val ioDispatcher: CoroutineDispatcher,
) : ChatRepository {

    override suspend fun getConversations(cursor: String?): DataResult<Page<Conversation>> =
        withContext(ioDispatcher) {
            when (val result = safeApiCall { chatApi.getConversations(cursor) }) {
                is DataResult.Success -> {
                    val page = Page(
                        items = result.data.results.map { it.toDomain() },
                        nextCursor = result.data.nextCursor,
                        hasMore = result.data.hasMore,
                    )
                    if (cursor == null) {
                        cacheDao.clear()
                        cacheDao.upsertAll(page.items.map { it.toEntity() })
                    }
                    DataResult.Success(page)
                }
                is DataResult.Failure -> {
                    if (cursor == null) {
                        val cached = cacheDao.getAll().map { it.toDomain() }
                        if (cached.isNotEmpty()) DataResult.Success(Page(cached, null, false)) else result
                    } else {
                        result
                    }
                }
            }
        }

    override suspend fun getMessages(claimId: String, cursor: String?): DataResult<Page<ChatMessage>> =
        withContext(ioDispatcher) {
            when (val result = safeApiCall { claimApi.getMessages(claimId, cursor) }) {
                is DataResult.Success -> DataResult.Success(
                    Page(result.data.results.map { it.toDomain() }, result.data.nextCursor, result.data.hasMore)
                )
                is DataResult.Failure -> result
            }
        }

    override suspend fun sendMessage(claimId: String, body: String): DataResult<ChatMessage> =
        withContext(ioDispatcher) {
            safeApiCall { claimApi.sendMessage(claimId, SendMessageBody(body = body)).toDomain() }
        }

    override suspend fun markRead(claimId: String): DataResult<Unit> =
        withContext(ioDispatcher) { safeApiCall { claimApi.markRead(claimId) } }

    override fun observeThread(threadId: String): Flow<ChatMessage> = chatSocket.observe(threadId)
}
