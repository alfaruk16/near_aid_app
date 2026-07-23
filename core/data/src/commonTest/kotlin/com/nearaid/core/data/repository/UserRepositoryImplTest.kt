package com.nearaid.core.data.repository

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.model.AppLanguage
import com.nearaid.core.network.api.UserApi
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserRepositoryImplTest {

    private val meJson = """{"id":"u1","phone":"+8801712345678","display_name":"Rahim","language":"en"}"""

    @Test
    fun refreshMe_updates_the_observable_current_user_on_success() = runTest {
        val repo = UserRepositoryImpl(UserApi(testClient { meJson }), testDispatcher)
        assertNull(repo.observeMe().first())
        val result = repo.refreshMe()
        assertIs<DataResult.Success<*>>(result)
        assertEquals("Rahim", repo.observeMe().first()?.displayName)
        assertEquals(AppLanguage.EN, repo.observeMe().first()?.language)
    }

    @Test
    fun refreshMe_leaves_cache_null_on_failure() = runTest {
        val repo = UserRepositoryImpl(UserApi(failingClient(HttpStatusCode.Unauthorized)), testDispatcher)
        assertIs<DataResult.Failure>(repo.refreshMe())
        assertNull(repo.observeMe().first())
    }

    @Test
    fun clear_drops_the_cached_profile() = runTest {
        val repo = UserRepositoryImpl(UserApi(testClient { meJson }), testDispatcher)
        repo.refreshMe()
        assertEquals("Rahim", repo.observeMe().first()?.displayName)
        repo.clear()
        assertNull(repo.observeMe().first())
    }

    @Test
    fun updateProfile_patches_with_language_code_and_caches_result() = runTest {
        val rec = RecordingRequests()
        val repo = UserRepositoryImpl(UserApi(testClient(rec) { meJson }), testDispatcher)
        val result = repo.updateProfile(displayName = "Rahim", language = AppLanguage.EN)
        assertIs<DataResult.Success<*>>(result)
        assertEquals("/me", rec.lastPath)
        val body = (rec.requests.last().body as TextContent).text
        assertTrue(body.contains("\"language\":\"en\""), body)
        assertEquals("Rahim", repo.observeMe().first()?.displayName)
    }

    @Test
    fun getUserRatings_maps_the_page_and_forwards_cursor() = runTest {
        val rec = RecordingRequests()
        val api = UserApi(
            testClient(rec) {
                """{"results":[{"id":"r1","score":5}],"next_cursor":"n2","has_more":true}"""
            }
        )
        val repo = UserRepositoryImpl(api, testDispatcher)
        val result = repo.getUserRatings("u9", "c1")
        assertIs<DataResult.Success<*>>(result)
        val page = (result as DataResult.Success).data
        assertEquals(1, page.items.size)
        assertEquals("n2", page.nextCursor)
        assertTrue(page.hasMore)
        assertEquals("c1", rec.requests.last().url.parameters["cursor"])
    }

    @Test
    fun getPublicUser_maps_the_user() = runTest {
        val rec = RecordingRequests()
        val api = UserApi(testClient(rec) { """{"id":"u2","display_name":"Karim","trust_score":4.0}""" })
        val repo = UserRepositoryImpl(api, testDispatcher)
        val result = repo.getPublicUser("u2")
        assertIs<DataResult.Success<*>>(result)
        assertEquals("Karim", (result as DataResult.Success).data.displayName)
        assertEquals("/users/u2", rec.lastPath)
    }

    @Test
    fun registerDevice_sends_the_fcm_token() = runTest {
        val rec = RecordingRequests()
        val repo = UserRepositoryImpl(UserApi(testClient(rec) { "" }), testDispatcher)
        assertIs<DataResult.Success<Unit>>(repo.registerDevice("fcm-1"))
        assertEquals("/me/devices", rec.lastPath)
        assertTrue((rec.requests.last().body as TextContent).text.contains("fcm-1"))
    }
}
