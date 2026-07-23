package com.nearaid.core.domain.usecase

import com.nearaid.core.domain.FakeCategoryRepository
import com.nearaid.core.domain.FakeNotificationRepository
import com.nearaid.core.domain.FakePreferencesRepository
import com.nearaid.core.domain.FakeSafetyRepository
import com.nearaid.core.model.AppLanguage
import com.nearaid.core.model.Category
import com.nearaid.core.model.ReportTargetType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MiscUseCasesTest {

    // --- Category ---

    @Test
    fun observeCategories_reflects_the_repository_flow() = runTest {
        val repo = FakeCategoryRepository()
        val list = listOf(Category(1, "food", "Food", "খাবার", null))
        repo.categories.value = list
        assertEquals(list, ObserveCategoriesUseCase(repo).invoke().first())
    }

    @Test
    fun refreshCategories_delegates() = runTest {
        val repo = FakeCategoryRepository()
        assertEquals(repo.refreshResult, RefreshCategoriesUseCase(repo).invoke())
    }

    // --- Notification ---

    @Test
    fun markNotificationsRead_calls_markAllRead() = runTest {
        val repo = FakeNotificationRepository()
        MarkNotificationsReadUseCase(repo).invoke()
        assertEquals(1, repo.markAllReadCalls)
    }

    @Test
    fun getNotifications_delegates() = runTest {
        val repo = FakeNotificationRepository()
        assertEquals(repo.notificationsResult, GetNotificationsUseCase(repo).invoke())
    }

    // --- Preferences ---

    @Test
    fun observeLanguage_reflects_the_repository_flow() = runTest {
        val repo = FakePreferencesRepository()
        assertEquals(AppLanguage.BN, ObserveLanguageUseCase(repo).invoke().first())
    }

    @Test
    fun setLanguage_forwards_to_the_repository() = runTest {
        val repo = FakePreferencesRepository()
        SetLanguageUseCase(repo).invoke(AppLanguage.EN)
        assertEquals(AppLanguage.EN, repo.lastSetLanguage)
    }

    @Test
    fun observeSearchRadius_reflects_the_repository_flow() = runTest {
        val repo = FakePreferencesRepository()
        assertEquals(5.0, ObserveSearchRadiusUseCase(repo).invoke().first())
    }

    @Test
    fun setSearchRadius_forwards_to_the_repository() = runTest {
        val repo = FakePreferencesRepository()
        SetSearchRadiusUseCase(repo).invoke(12.5)
        assertEquals(12.5, repo.lastSetRadius)
    }

    // --- Safety ---

    @Test
    fun report_forwards_target_type_id_and_reason() = runTest {
        val repo = FakeSafetyRepository()
        ReportUseCase(repo).invoke(ReportTargetType.LISTING, "l1", "spam")
        assertEquals(ReportTargetType.LISTING, repo.lastReportTargetType)
        assertEquals("l1", repo.lastReportTargetId)
        assertEquals("spam", repo.lastReportReason)
    }

    @Test
    fun block_and_unblock_forward_the_user_id() = runTest {
        val repo = FakeSafetyRepository()
        BlockUserUseCase(repo).invoke("u5")
        UnblockUserUseCase(repo).invoke("u6")
        assertEquals("u5", repo.lastBlockUserId)
        assertEquals("u6", repo.lastUnblockUserId)
    }

    @Test
    fun getBlockedUsers_delegates() = runTest {
        val repo = FakeSafetyRepository()
        assertEquals(repo.blockedUsersResult, GetBlockedUsersUseCase(repo).invoke())
    }
}
