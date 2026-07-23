package com.nearaid.core.data.repository

import com.nearaid.core.common.result.AppError
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.model.ReportTargetType
import com.nearaid.core.network.api.CategoryApi
import com.nearaid.core.network.api.NotificationApi
import com.nearaid.core.network.api.SafetyApi
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class SimpleRepositoriesTest {

    // --- Safety ---

    @Test
    fun report_sends_the_target_type_wire_value_and_reason() = runTest {
        val rec = RecordingRequests()
        val api = SafetyApi(testClient(rec) { "" })
        val repo = SafetyRepositoryImpl(api, testDispatcher)
        val result = repo.report(ReportTargetType.LISTING, "l1", "spam")
        assertIs<DataResult.Success<Unit>>(result)
        assertEquals("/reports", rec.lastPath)
        val body = (rec.requests.last().body as io.ktor.http.content.TextContent).text
        assertTrue(body.contains("\"listing\""), body)
        assertTrue(body.contains("spam"), body)
    }

    @Test
    fun block_and_unblock_hit_the_right_endpoints() = runTest {
        val rec = RecordingRequests()
        val api = SafetyApi(testClient(rec) { "" })
        val repo = SafetyRepositoryImpl(api, testDispatcher)
        repo.block("u5")
        assertEquals("/blocks", rec.lastPath)
        repo.unblock("u6")
        assertEquals("/blocks/u6", rec.lastPath)
    }

    @Test
    fun getBlockedUsers_maps_the_results() = runTest {
        val api = SafetyApi(
            testClient { """{"results":[{"id":"u2","display_name":"Karim","trust_score":4.0}]}""" }
        )
        val repo = SafetyRepositoryImpl(api, testDispatcher)
        val result = repo.getBlockedUsers()
        assertIs<DataResult.Success<*>>(result)
        val users = (result as DataResult.Success).data
        assertEquals(1, users.size)
        assertEquals("Karim", users.first().displayName)
    }

    @Test
    fun getBlockedUsers_maps_a_404_to_NotFound() = runTest {
        val repo = SafetyRepositoryImpl(SafetyApi(failingClient(HttpStatusCode.NotFound)), testDispatcher)
        val result = repo.getBlockedUsers()
        assertIs<DataResult.Failure>(result)
        assertIs<AppError.NotFound>(result.error)
    }

    // --- Notification ---

    @Test
    fun getNotifications_maps_read_state_from_readAt() = runTest {
        val api = NotificationApi(
            testClient {
                """{"results":[
                    {"id":"n1","read_at":"2026-01-01"},
                    {"id":"n2","read_at":null}
                ]}"""
            }
        )
        val repo = NotificationRepositoryImpl(api, testDispatcher)
        val result = repo.getNotifications()
        assertIs<DataResult.Success<*>>(result)
        val items = (result as DataResult.Success).data
        assertEquals(listOf(true, false), items.map { it.isRead })
    }

    @Test
    fun markAllRead_posts_and_succeeds() = runTest {
        val rec = RecordingRequests()
        val repo = NotificationRepositoryImpl(NotificationApi(testClient(rec) { "" }), testDispatcher)
        assertIs<DataResult.Success<Unit>>(repo.markAllRead())
        assertEquals("/me/notifications/read", rec.lastPath)
    }

    // --- Category ---

    @Test
    fun refreshCategories_caches_the_result_into_the_observable_flow() = runTest {
        val api = CategoryApi(
            testClient { """{"results":[{"id":1,"key":"food","name_en":"Food","name_bn":"খাবার"}]}""" }
        )
        val repo = CategoryRepositoryImpl(api, testDispatcher)

        assertTrue(repo.observeCategories().first().isEmpty())
        val result = repo.refreshCategories()
        assertIs<DataResult.Success<*>>(result)
        assertEquals(listOf("food"), repo.observeCategories().first().map { it.key })
    }

    @Test
    fun refreshCategories_leaves_the_cache_untouched_on_failure() = runTest {
        val repo = CategoryRepositoryImpl(CategoryApi(failingClient(HttpStatusCode.InternalServerError)), testDispatcher)
        val result = repo.refreshCategories()
        assertIs<DataResult.Failure>(result)
        assertIs<AppError.Server>(result.error)
        assertTrue(repo.observeCategories().first().isEmpty())
    }
}
