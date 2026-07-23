package com.nearaid.core.data.repository

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.datastore.AuthPreferencesDataSource
import com.nearaid.core.network.api.ChatApi
import com.nearaid.core.network.api.ClaimApi
import com.nearaid.core.network.socket.ChatSocket
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ChatRepositoryImplTest {

    private fun socket(): ChatSocket = ChatSocket(
        client = testClient { "" },
        json = Json { ignoreUnknownKeys = true },
        authPrefs = AuthPreferencesDataSource(FakeSecureTokenStore()),
        wsUrl = "ws://test",
    )

    private fun repo(
        dao: FakeConversationCacheDao = FakeConversationCacheDao(),
        rec: RecordingRequests = RecordingRequests(),
        handler: (HttpRequestData) -> String?,
    ) = ChatRepositoryImpl(
        chatApi = ChatApi(testClient(rec, handler = handler)),
        claimApi = ClaimApi(testClient(rec, handler = handler)),
        chatSocket = socket(),
        cacheDao = dao,
        ioDispatcher = testDispatcher,
    )

    private val convJson = """
        {"thread_id":"t1","claim_id":"c1",
         "listing":{"id":"l1","type":"offer","title":"Rice","status":"open"},
         "counterpart":{"id":"u2","display_name":"Karim"},
         "role":"helping","unread_count":2}
    """.trimIndent()

    @Test
    fun getConversations_maps_results_and_replaces_the_cache_on_the_first_page() = runTest {
        val dao = FakeConversationCacheDao()
        val repo = repo(dao) { """{"results":[$convJson],"next_cursor":null,"has_more":false}""" }
        val result = repo.getConversations(cursor = null)
        assertIs<DataResult.Success<*>>(result)
        assertEquals(1, (result as DataResult.Success).data.items.size)
        assertEquals(1, dao.clearCalls)
        assertEquals(1, dao.store.size)
    }

    @Test
    fun getConversations_does_not_write_cache_on_a_paged_request() = runTest {
        val dao = FakeConversationCacheDao()
        val repo = repo(dao) { """{"results":[$convJson]}""" }
        repo.getConversations(cursor = "page-2")
        assertEquals(0, dao.clearCalls)
        assertTrue(dao.store.isEmpty())
    }

    @Test
    fun getConversations_falls_back_to_cache_when_the_first_page_fails() = runTest {
        // Seed the cache by first serving a successful page, then fail.
        val dao = FakeConversationCacheDao()
        repo(dao) { """{"results":[$convJson]}""" }.getConversations(null)
        assertEquals(1, dao.store.size)

        val failingRepo = ChatRepositoryImpl(
            chatApi = ChatApi(failingClient(HttpStatusCode.InternalServerError)),
            claimApi = ClaimApi(failingClient(HttpStatusCode.InternalServerError)),
            chatSocket = socket(),
            cacheDao = dao,
            ioDispatcher = testDispatcher,
        )
        val result = failingRepo.getConversations(null)
        assertIs<DataResult.Success<*>>(result)
        assertEquals("t1", (result as DataResult.Success).data.items.first().threadId)
    }

    @Test
    fun getConversations_returns_failure_when_cache_empty() = runTest {
        val failingRepo = ChatRepositoryImpl(
            chatApi = ChatApi(failingClient(HttpStatusCode.InternalServerError)),
            claimApi = ClaimApi(failingClient(HttpStatusCode.InternalServerError)),
            chatSocket = socket(),
            cacheDao = FakeConversationCacheDao(),
            ioDispatcher = testDispatcher,
        )
        assertIs<DataResult.Failure>(failingRepo.getConversations(null))
    }

    @Test
    fun getMessages_maps_the_page() = runTest {
        val rec = RecordingRequests()
        val repo = repo(rec = rec) {
            """{"results":[{"id":"m1","sender":"u1","body":"hi"}],"next_cursor":"n2","has_more":true}"""
        }
        val result = repo.getMessages("c1", "cur")
        assertIs<DataResult.Success<*>>(result)
        val page = (result as DataResult.Success).data
        assertEquals("m1", page.items.first().id)
        assertEquals("n2", page.nextCursor)
        assertEquals("/claims/c1/messages", rec.lastPath)
    }

    @Test
    fun sendMessage_posts_the_trimmed_body_and_maps_the_message() = runTest {
        val rec = RecordingRequests()
        val repo = repo(rec = rec) { """{"id":"m9","sender":"u1","body":"hello"}""" }
        val result = repo.sendMessage("c1", "hello")
        assertIs<DataResult.Success<*>>(result)
        assertEquals("m9", (result as DataResult.Success).data.id)
        assertEquals("/claims/c1/messages", rec.lastPath)
        assertTrue((rec.requests.last().body as TextContent).text.contains("hello"))
    }

    @Test
    fun markRead_posts_to_the_read_endpoint() = runTest {
        val rec = RecordingRequests()
        val repo = repo(rec = rec) { "" }
        assertIs<DataResult.Success<Unit>>(repo.markRead("c1"))
        assertEquals("/claims/c1/messages/read", rec.lastPath)
    }
}
