package com.nearaid.feature.messages.conversations

import app.cash.turbine.test
import com.nearaid.core.common.result.AppError
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.GetConversationsUseCase
import com.nearaid.core.model.Author
import com.nearaid.core.model.ClaimRole
import com.nearaid.core.model.Conversation
import com.nearaid.core.model.ListingStatus
import com.nearaid.core.model.ListingType
import com.nearaid.core.model.Page
import com.nearaid.feature.messages.MainDispatcherRule
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
class ConversationsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getConversations = mockk<GetConversationsUseCase>()

    @Before
    fun setUp() {
        // Empty-success default so construction (which triggers Load) never throws.
        coEvery { getConversations(any()) } returns DataResult.Success(page(emptyList()))
    }

    private fun viewModel() = ConversationsViewModel(getConversations = getConversations)

    @Test
    fun `loads conversations on init`() {
        val items = listOf(conversation("c1"), conversation("c2"))
        coEvery { getConversations(any()) } returns DataResult.Success(page(items))

        val state = viewModel().state.value

        assertEquals(items, state.conversations)
        assertFalse(state.loading)
        assertNull(state.error)
    }

    @Test
    fun `load failure surfaces an error and clears loading`() {
        coEvery { getConversations(any()) } returns DataResult.Failure(AppError.Network("offline"))

        val state = viewModel().state.value

        assertEquals("offline", state.error)
        assertFalse(state.loading)
        assertTrue(state.conversations.isEmpty())
    }

    @Test
    fun `empty page yields no conversations and no error`() {
        coEvery { getConversations(any()) } returns DataResult.Success(page(emptyList()))

        val state = viewModel().state.value

        assertTrue(state.conversations.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `Load reloads conversations`() {
        val viewModel = viewModel()

        viewModel.onIntent(ConversationsIntent.Load)

        // Once on init, once on the explicit Load.
        coVerify(exactly = 2) { getConversations(any()) }
    }

    @Test
    fun `ConversationClicked emits an OpenChat effect`() = runTest {
        val viewModel = viewModel()

        viewModel.effect.test {
            viewModel.onIntent(ConversationsIntent.ConversationClicked("c1", "t1", "Rice"))
            assertEquals(ConversationsEffect.OpenChat("c1", "t1", "Rice"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- fixtures ------------------------------------------------------------

    private fun page(items: List<Conversation>) = Page(items = items, nextCursor = null, hasMore = false)

    private fun conversation(claimId: String) = Conversation(
        threadId = "thread-$claimId",
        claimId = claimId,
        listingId = "listing-$claimId",
        listingType = ListingType.REQUEST,
        listingTitle = "title-$claimId",
        listingStatus = ListingStatus.OPEN,
        counterpart = Author(
            id = "author",
            displayName = null,
            photoUrl = null,
            trustScore = null,
            isIdVerified = false,
        ),
        role = ClaimRole.HELPING,
        lastMessageBody = null,
        lastMessageAt = null,
        unreadCount = 0,
    )
}
