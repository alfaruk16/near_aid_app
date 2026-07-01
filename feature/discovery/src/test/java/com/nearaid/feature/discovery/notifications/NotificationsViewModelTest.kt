package com.nearaid.feature.discovery.notifications

import com.nearaid.feature.discovery.MainDispatcherRule
import app.cash.turbine.test
import com.nearaid.core.common.result.AppError
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.GetNotificationsUseCase
import com.nearaid.core.domain.usecase.MarkNotificationsReadUseCase
import com.nearaid.core.model.NotificationItem
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getNotifications = mockk<GetNotificationsUseCase>()
    private val markNotificationsRead = mockk<MarkNotificationsReadUseCase>()

    @Before
    fun setUp() {
        coEvery { getNotifications() } returns DataResult.Success(emptyList())
        coEvery { markNotificationsRead() } returns DataResult.Success(Unit)
    }

    private fun viewModel() = NotificationsViewModel(
        getNotifications = getNotifications,
        markNotificationsRead = markNotificationsRead,
    )

    @Test
    fun `Load populates notifications and marks them read`() {
        val items = listOf(notification("n1"), notification("n2"))
        coEvery { getNotifications() } returns DataResult.Success(items)
        val viewModel = viewModel()

        viewModel.onIntent(NotificationsIntent.Load)

        val state = viewModel.state.value
        assertEquals(items, state.notifications)
        assertFalse(state.loading)
        assertNull(state.error)
        coVerify(exactly = 1) { markNotificationsRead() }
    }

    @Test
    fun `Load surfaces an error and does not mark read on failure`() {
        coEvery { getNotifications() } returns DataResult.Failure(AppError.Network("offline"))
        val viewModel = viewModel()

        viewModel.onIntent(NotificationsIntent.Load)

        val state = viewModel.state.value
        assertEquals("offline", state.error)
        assertFalse(state.loading)
        assertTrue(state.notifications.isEmpty())
        coVerify(exactly = 0) { markNotificationsRead() }
    }

    @Test
    fun `BackClicked emits a NavigateBack effect`() = runTest {
        val viewModel = viewModel()

        viewModel.effect.test {
            viewModel.onIntent(NotificationsIntent.BackClicked)
            assertEquals(NotificationsEffect.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun notification(id: String) = NotificationItem(
        id = id,
        type = "claim",
        title = "title-$id",
        body = "body-$id",
        createdAt = "2026-01-01T00:00:00",
        isRead = false,
        deeplink = null,
    )
}
