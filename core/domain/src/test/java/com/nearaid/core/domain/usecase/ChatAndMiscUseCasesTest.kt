package com.nearaid.core.domain.usecase

import com.nearaid.core.domain.repository.ChatRepository
import com.nearaid.core.domain.repository.NotificationRepository
import com.nearaid.core.domain.repository.PreferencesRepository
import com.nearaid.core.domain.repository.SafetyRepository
import com.nearaid.core.model.AppLanguage
import com.nearaid.core.model.ReportTargetType
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ChatAndMiscUseCasesTest {

    private val chat = mockk<ChatRepository>(relaxed = true)
    private val notifications = mockk<NotificationRepository>(relaxed = true)
    private val safety = mockk<SafetyRepository>(relaxed = true)
    private val prefs = mockk<PreferencesRepository>(relaxed = true)

    @Test
    fun sendMessage_trims_the_body() = runTest {
        SendMessageUseCase(chat)("c1", "  hello  ")
        coVerify(exactly = 1) { chat.sendMessage("c1", "hello") }
    }

    @Test
    fun chat_read_paging_and_observe_delegate() = runTest {
        every { chat.observeThread(any()) } returns flowOf()

        GetConversationsUseCase(chat)("cur")
        GetConversationsUseCase(chat)()
        GetMessagesUseCase(chat)("c1", "cur")
        GetMessagesUseCase(chat)("c1")
        MarkThreadReadUseCase(chat)("c1")
        ObserveThreadUseCase(chat)("t1")

        coVerify(exactly = 1) { chat.getConversations("cur") }
        coVerify(exactly = 1) { chat.getConversations(null) }
        coVerify(exactly = 1) { chat.getMessages("c1", "cur") }
        coVerify(exactly = 1) { chat.getMessages("c1", null) }
        coVerify(exactly = 1) { chat.markRead("c1") }
        verify(exactly = 1) { chat.observeThread("t1") }
    }

    @Test
    fun notification_use_cases_delegate() = runTest {
        GetNotificationsUseCase(notifications)()
        MarkNotificationsReadUseCase(notifications)()
        coVerify(exactly = 1) { notifications.getNotifications() }
        coVerify(exactly = 1) { notifications.markAllRead() }
    }

    @Test
    fun safety_use_cases_delegate() = runTest {
        ReportUseCase(safety)(ReportTargetType.LISTING, "l1", "spam")
        BlockUserUseCase(safety)("u1")
        UnblockUserUseCase(safety)("u2")
        GetBlockedUsersUseCase(safety)()

        coVerify(exactly = 1) { safety.report(ReportTargetType.LISTING, "l1", "spam") }
        coVerify(exactly = 1) { safety.block("u1") }
        coVerify(exactly = 1) { safety.unblock("u2") }
        coVerify(exactly = 1) { safety.getBlockedUsers() }
    }

    @Test
    fun preferences_use_cases_read_and_write() = runTest {
        every { prefs.language } returns flowOf(AppLanguage.BN)
        every { prefs.searchRadiusKm } returns flowOf(5.0)

        ObserveLanguageUseCase(prefs)()
        SetLanguageUseCase(prefs)(AppLanguage.EN)
        ObserveSearchRadiusUseCase(prefs)()
        SetSearchRadiusUseCase(prefs)(10.0)

        verify(exactly = 1) { prefs.language }
        coVerify(exactly = 1) { prefs.setLanguage(AppLanguage.EN) }
        verify(exactly = 1) { prefs.searchRadiusKm }
        coVerify(exactly = 1) { prefs.setSearchRadiusKm(10.0) }
    }
}
