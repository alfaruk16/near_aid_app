package com.nearaid.core.data.repository

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.model.ReportTargetType
import com.nearaid.core.network.api.CategoryApi
import com.nearaid.core.network.api.NotificationApi
import com.nearaid.core.network.api.BlockedResponse
import com.nearaid.core.network.api.SafetyApi
import com.nearaid.core.network.dto.BlockBody
import com.nearaid.core.network.dto.CategoriesResponse
import com.nearaid.core.network.dto.CategoryDto
import com.nearaid.core.network.dto.NotificationDto
import com.nearaid.core.network.dto.NotificationsResponse
import com.nearaid.core.network.dto.PublicUserDto
import com.nearaid.core.network.dto.ReportBody
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SimpleRepositoriesTest {

    private val dispatcher = UnconfinedTestDispatcher()

    // --- Categories ------------------------------------------------------------

    @Test
    fun refreshCategories_maps_and_updates_the_observed_stream() = runTest {
        val api = mockk<CategoryApi>()
        coEvery { api.getCategories() } returns CategoriesResponse(
            results = listOf(CategoryDto(1, "food", "Food", "খাবার", null)),
        )
        val repo = CategoryRepositoryImpl(api, dispatcher)

        val result = repo.refreshCategories()

        assertTrue(result is DataResult.Success)
        assertEquals("food", (result as DataResult.Success).data.single().key)
        assertEquals("food", repo.observeCategories().first().single().key)
    }

    // --- Notifications ---------------------------------------------------------

    @Test
    fun getNotifications_maps_and_markAllRead_delegates() = runTest {
        val api = mockk<NotificationApi>(relaxUnitFun = true)
        coEvery { api.getNotifications() } returns NotificationsResponse(
            results = listOf(NotificationDto(id = "n1", title = "Hi", readAt = null)),
        )
        val repo = NotificationRepositoryImpl(api, dispatcher)

        val result = repo.getNotifications()
        assertTrue(result is DataResult.Success)
        assertEquals("n1", (result as DataResult.Success).data.single().id)

        assertTrue(repo.markAllRead() is DataResult.Success)
        coVerify(exactly = 1) { api.markAllRead() }
    }

    // --- Safety ----------------------------------------------------------------

    @Test
    fun safety_actions_build_the_right_bodies_and_map_blocked() = runTest {
        val api = mockk<SafetyApi>(relaxUnitFun = true)
        coEvery { api.getBlocked() } returns BlockedResponse(results = listOf(PublicUserDto(id = "u9")))
        val repo = SafetyRepositoryImpl(api, dispatcher)

        repo.report(ReportTargetType.LISTING, "l1", "spam")
        repo.block("u1")
        repo.unblock("u2")
        val blocked = repo.getBlockedUsers()

        coVerify(exactly = 1) { api.report(ReportBody("listing", "l1", "spam")) }
        coVerify(exactly = 1) { api.block(BlockBody("u1")) }
        coVerify(exactly = 1) { api.unblock("u2") }
        assertTrue(blocked is DataResult.Success)
        assertEquals("u9", (blocked as DataResult.Success).data.single().id)
    }
}
