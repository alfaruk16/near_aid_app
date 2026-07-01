package com.nearaid.feature.messages.chat

import app.cash.turbine.test
import com.nearaid.core.common.result.AppError
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.GetMessagesUseCase
import com.nearaid.core.domain.usecase.MarkThreadReadUseCase
import com.nearaid.core.domain.usecase.ObserveCurrentUserUseCase
import com.nearaid.core.domain.usecase.ObserveThreadUseCase
import com.nearaid.core.domain.usecase.SendMessageUseCase
import com.nearaid.core.model.AccountStatus
import com.nearaid.core.model.AppLanguage
import com.nearaid.core.model.ChatMessage
import com.nearaid.core.model.Me
import com.nearaid.core.model.MessageType
import com.nearaid.core.model.Page
import com.nearaid.feature.messages.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getMessages = mockk<GetMessagesUseCase>()
    private val sendMessage = mockk<SendMessageUseCase>()
    private val markThreadRead = mockk<MarkThreadReadUseCase>()
    private val observeThread = mockk<ObserveThreadUseCase>()
    private val observeCurrentUser = mockk<ObserveCurrentUserUseCase>()

    @Before
    fun setUp() {
        // Safe defaults so init (observes user) and Start (loads + streams) never throw.
        every { observeCurrentUser() } returns flowOf(null)
        every { observeThread(any()) } returns emptyFlow()
        coEvery { getMessages(any(), any()) } returns DataResult.Success(page(emptyList()))
        coEvery { markThreadRead(any()) } returns DataResult.Success(Unit)
    }

    private fun viewModel() = ChatViewModel(
        getMessages = getMessages,
        sendMessage = sendMessage,
        markThreadRead = markThreadRead,
        observeThread = observeThread,
        observeCurrentUser = observeCurrentUser,
    )

    // --- init ----------------------------------------------------------------

    @Test
    fun `init resolves the current user id`() {
        every { observeCurrentUser() } returns flowOf(me("u-42"))

        assertEquals("u-42", viewModel().state.value.myUserId)
    }

    // --- Start: history + mark-read + streaming ------------------------------

    @Test
    fun `Start loads message history and marks the thread read`() {
        val history = listOf(message("m1"), message("m2"))
        coEvery { getMessages("c1", any()) } returns DataResult.Success(page(history))

        val viewModel = viewModel()
        viewModel.onIntent(ChatIntent.Start("c1", "t1"))

        val state = viewModel.state.value
        assertEquals(history, state.messages)
        assertFalse(state.loading)
        assertNull(state.error)
        coVerify(exactly = 1) { markThreadRead("c1") }
    }

    @Test
    fun `Start failure surfaces an error and clears loading`() {
        coEvery { getMessages("c1", any()) } returns DataResult.Failure(AppError.Network("down"))

        val viewModel = viewModel()
        viewModel.onIntent(ChatIntent.Start("c1", "t1"))

        val state = viewModel.state.value
        assertEquals("down", state.error)
        assertFalse(state.loading)
    }

    @Test
    fun `an incoming thread message is appended to state`() {
        coEvery { getMessages("c1", any()) } returns DataResult.Success(page(listOf(message("m1"))))
        every { observeThread("t1") } returns flowOf(message("m2"))

        val viewModel = viewModel()
        viewModel.onIntent(ChatIntent.Start("c1", "t1"))

        assertEquals(listOf(message("m1"), message("m2")), viewModel.state.value.messages)
    }

    @Test
    fun `a duplicate incoming message is ignored`() {
        coEvery { getMessages("c1", any()) } returns DataResult.Success(page(listOf(message("m1"))))
        every { observeThread("t1") } returns flowOf(message("m1")) // same id already present

        val viewModel = viewModel()
        viewModel.onIntent(ChatIntent.Start("c1", "t1"))

        assertEquals(1, viewModel.state.value.messages.size)
    }

    // --- input + send --------------------------------------------------------

    @Test
    fun `InputChanged updates the input text`() {
        val viewModel = viewModel()

        viewModel.onIntent(ChatIntent.InputChanged("hello"))

        assertEquals("hello", viewModel.state.value.inputText)
    }

    @Test
    fun `Send trims the input, clears it, and appends the sent message`() {
        val sent = message("s1")
        coEvery { sendMessage("c1", "hi") } returns DataResult.Success(sent)

        val viewModel = viewModel()
        viewModel.onIntent(ChatIntent.InputChanged("  hi  "))
        viewModel.onIntent(ChatIntent.Send("c1"))

        val state = viewModel.state.value
        assertEquals("", state.inputText)
        assertFalse(state.sending)
        assertEquals(listOf(sent), state.messages)
        coVerify(exactly = 1) { sendMessage("c1", "hi") }
    }

    @Test
    fun `Send with blank input is a no-op`() {
        val viewModel = viewModel()

        viewModel.onIntent(ChatIntent.InputChanged("   "))
        viewModel.onIntent(ChatIntent.Send("c1"))

        coVerify(exactly = 0) { sendMessage(any(), any()) }
        assertFalse(viewModel.state.value.sending)
    }

    @Test
    fun `Send failure surfaces an error and clears sending`() {
        coEvery { sendMessage("c1", "hi") } returns DataResult.Failure(AppError.Server(500, "boom"))

        val viewModel = viewModel()
        viewModel.onIntent(ChatIntent.InputChanged("hi"))
        viewModel.onIntent(ChatIntent.Send("c1"))

        val state = viewModel.state.value
        assertEquals("boom", state.error)
        assertFalse(state.sending)
    }

    // --- effects -------------------------------------------------------------

    @Test
    fun `BackClicked emits a NavigateBack effect`() = runTest {
        val viewModel = viewModel()

        viewModel.effect.test {
            viewModel.onIntent(ChatIntent.BackClicked)
            assertEquals(ChatEffect.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- fixtures ------------------------------------------------------------

    private fun page(items: List<ChatMessage>) = Page(items = items, nextCursor = null, hasMore = false)

    private fun message(id: String) = ChatMessage(
        id = id,
        senderId = "sender",
        type = MessageType.TEXT,
        body = "body-$id",
        imageUrl = null,
        createdAt = "2026-01-01T00:00:00",
        readAt = null,
    )

    private fun me(id: String) = Me(
        id = id,
        phone = "+8801712345678",
        email = null,
        displayName = null,
        photoUrl = null,
        language = AppLanguage.EN,
        defaultArea = null,
        isPhoneVerified = true,
        isIdVerified = false,
        trustScore = 0.0,
        status = AccountStatus.ACTIVE,
    )
}
