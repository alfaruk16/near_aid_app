package com.nearaid.core.data.repository

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.database.dao.ConversationCacheDao
import com.nearaid.core.network.api.ChatApi
import com.nearaid.core.network.api.ClaimApi
import com.nearaid.core.network.dto.AuthorDto
import com.nearaid.core.network.dto.ConversationDto
import com.nearaid.core.network.dto.ConversationListingDto
import com.nearaid.core.network.dto.ConversationsResponse
import com.nearaid.core.network.dto.MessageDto
import com.nearaid.core.network.dto.MessagesResponse
import com.nearaid.core.network.dto.SendMessageBody
import com.nearaid.core.network.socket.ChatSocket
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatRepositoryImplTest {

    private val chatApi = mockk<ChatApi>()
    private val claimApi = mockk<ClaimApi>(relaxUnitFun = true)
    private val socket = mockk<ChatSocket>()
    private val cache = mockk<ConversationCacheDao>(relaxUnitFun = true)
    private fun repo() = ChatRepositoryImpl(chatApi, claimApi, socket, cache, UnconfinedTestDispatcher())

    private fun conversationDto(id: String) = ConversationDto(
        threadId = "t$id",
        claimId = id,
        listing = ConversationListingDto(id = "l$id", type = "request", title = "Help", status = "open"),
        counterpart = AuthorDto("u2"),
        role = "helping",
    )

    @Test
    fun getConversations_first_page_maps_and_refreshes_cache() = runTest {
        coEvery { chatApi.getConversations(null) } returns ConversationsResponse(
            results = listOf(conversationDto("c1")),
            nextCursor = "n2",
            hasMore = true,
        )

        val result = repo().getConversations(null)

        assertTrue(result is DataResult.Success)
        val page = (result as DataResult.Success).data
        assertEquals("c1", page.items.single().claimId)
        coVerify(exactly = 1) { cache.clear() }
        coVerify(exactly = 1) { cache.upsertAll(any()) }
    }

    @Test
    fun getConversations_failure_first_page_falls_back_to_empty_cache_as_failure() = runTest {
        coEvery { chatApi.getConversations(null) } throws java.io.IOException("offline")
        coEvery { cache.getAll() } returns emptyList()

        assertTrue(repo().getConversations(null) is DataResult.Failure)
    }

    @Test
    fun getMessages_maps_the_page() = runTest {
        coEvery { claimApi.getMessages("c1", null) } returns MessagesResponse(
            results = listOf(MessageDto(id = "m1", senderId = "u2", type = "text", body = "hi")),
            nextCursor = null,
            hasMore = false,
        )
        val result = repo().getMessages("c1", null)
        assertTrue(result is DataResult.Success)
        assertEquals("m1", (result as DataResult.Success).data.items.single().id)
    }

    @Test
    fun sendMessage_posts_body_and_maps_result() = runTest {
        coEvery { claimApi.sendMessage("c1", SendMessageBody(body = "hi")) } returns
            MessageDto(id = "m1", senderId = "me", body = "hi")

        val result = repo().sendMessage("c1", "hi")

        assertTrue(result is DataResult.Success)
        assertEquals("m1", (result as DataResult.Success).data.id)
        coVerify(exactly = 1) { claimApi.sendMessage("c1", SendMessageBody(body = "hi")) }
    }

    @Test
    fun markRead_delegates_and_observeThread_uses_the_socket() = runTest {
        every { socket.observe("t1") } returns flowOf(
            MessageDto(id = "m1", senderId = "u2", body = "yo").let {
                com.nearaid.core.model.ChatMessage("m1", "u2", com.nearaid.core.model.MessageType.TEXT, "yo", null, "", null)
            },
        )

        assertTrue(repo().markRead("c1") is DataResult.Success)
        assertEquals("m1", repo().observeThread("t1").first().id)
        coVerify(exactly = 1) { claimApi.markRead("c1") }
    }
}
