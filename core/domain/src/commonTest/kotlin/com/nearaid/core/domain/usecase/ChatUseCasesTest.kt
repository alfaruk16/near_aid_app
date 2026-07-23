package com.nearaid.core.domain.usecase

import com.nearaid.core.domain.FakeChatRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ChatUseCasesTest {

    @Test
    fun getConversations_defaults_cursor_to_null() = runTest {
        val repo = FakeChatRepository()
        GetConversationsUseCase(repo).invoke()
        assertNull(repo.lastConversationsCursor)
    }

    @Test
    fun getConversations_forwards_cursor() = runTest {
        val repo = FakeChatRepository()
        GetConversationsUseCase(repo).invoke("c-9")
        assertEquals("c-9", repo.lastConversationsCursor)
    }

    @Test
    fun getMessages_forwards_claim_id_and_cursor() = runTest {
        val repo = FakeChatRepository()
        GetMessagesUseCase(repo).invoke("claim-1", "cur-2")
        assertEquals("claim-1", repo.lastMessagesClaimId)
        assertEquals("cur-2", repo.lastMessagesCursor)
    }

    @Test
    fun sendMessage_trims_the_body() = runTest {
        val repo = FakeChatRepository()
        SendMessageUseCase(repo).invoke("claim-1", "  hello there  ")
        assertEquals("claim-1", repo.lastSendClaimId)
        assertEquals("hello there", repo.lastSendBody)
    }

    @Test
    fun markThreadRead_forwards_claim_id() = runTest {
        val repo = FakeChatRepository()
        MarkThreadReadUseCase(repo).invoke("claim-3")
        assertEquals("claim-3", repo.lastMarkReadClaimId)
    }

    @Test
    fun observeThread_subscribes_with_the_thread_id() = runTest {
        val repo = FakeChatRepository()
        val message = ObserveThreadUseCase(repo).invoke("thread-7").first()
        assertEquals("thread-7", repo.lastObserveThreadId)
        assertEquals(repo.thread.value, message)
    }
}
