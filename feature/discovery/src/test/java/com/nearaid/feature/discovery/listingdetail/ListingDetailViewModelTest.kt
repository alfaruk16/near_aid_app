package com.nearaid.feature.discovery.listingdetail

import com.nearaid.feature.discovery.MainDispatcherRule
import app.cash.turbine.test
import com.nearaid.core.common.result.AppError
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.BlockUserUseCase
import com.nearaid.core.domain.usecase.ClaimListingUseCase
import com.nearaid.core.domain.usecase.GetListingUseCase
import com.nearaid.core.domain.usecase.ReportUseCase
import com.nearaid.core.model.Author
import com.nearaid.core.model.Claim
import com.nearaid.core.model.ClaimStatus
import com.nearaid.core.model.ListingDetail
import com.nearaid.core.model.ListingStatus
import com.nearaid.core.model.ListingType
import com.nearaid.core.model.ReportTargetType
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
class ListingDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getListing = mockk<GetListingUseCase>()
    private val claimListing = mockk<ClaimListingUseCase>()
    private val report = mockk<ReportUseCase>()
    private val blockUser = mockk<BlockUserUseCase>()

    @Before
    fun setUp() {
        coEvery { getListing(any()) } returns DataResult.Success(detail())
        coEvery { claimListing(any()) } returns DataResult.Success(claim())
        coEvery { report(any(), any(), any()) } returns DataResult.Success(Unit)
        coEvery { blockUser(any()) } returns DataResult.Success(Unit)
    }

    private fun viewModel() = ListingDetailViewModel(
        getListing = getListing,
        claimListing = claimListing,
        report = report,
        blockUser = blockUser,
    )

    // --- load ----------------------------------------------------------------

    @Test
    fun `Load populates the listing on success`() {
        val detail = detail(id = "l1", title = "Rice")
        coEvery { getListing("l1") } returns DataResult.Success(detail)
        val viewModel = viewModel()

        viewModel.onIntent(ListingDetailIntent.Load("l1"))

        val state = viewModel.state.value
        assertEquals(detail, state.listing)
        assertFalse(state.loading)
        assertNull(state.error)
    }

    @Test
    fun `Load surfaces an error on failure`() {
        coEvery { getListing("l1") } returns DataResult.Failure(AppError.NotFound("gone"))
        val viewModel = viewModel()

        viewModel.onIntent(ListingDetailIntent.Load("l1"))

        val state = viewModel.state.value
        assertEquals("gone", state.error)
        assertFalse(state.loading)
        assertNull(state.listing)
    }

    // --- claim ---------------------------------------------------------------

    @Test
    fun `ClaimClicked is a no-op until a listing is loaded`() {
        val viewModel = viewModel()

        viewModel.onIntent(ListingDetailIntent.ClaimClicked)

        coVerify(exactly = 0) { claimListing(any()) }
    }

    @Test
    fun `ClaimClicked emits an OpenChat effect on success`() = runTest {
        coEvery { getListing("l1") } returns DataResult.Success(detail(id = "l1", title = "Rice"))
        coEvery { claimListing("l1") } returns DataResult.Success(
            claim(id = "claim-1", chatThreadId = "thread-1"),
        )
        val viewModel = viewModel()
        viewModel.onIntent(ListingDetailIntent.Load("l1"))

        viewModel.effect.test {
            viewModel.onIntent(ListingDetailIntent.ClaimClicked)
            assertEquals(
                ListingDetailEffect.OpenChat(claimId = "claim-1", threadId = "thread-1", title = "Rice"),
                awaitItem(),
            )
            cancelAndIgnoreRemainingEvents()
        }
        assertFalse(viewModel.state.value.claiming)
    }

    @Test
    fun `ClaimClicked surfaces a claim error on failure`() {
        coEvery { getListing("l1") } returns DataResult.Success(detail(id = "l1"))
        coEvery { claimListing("l1") } returns DataResult.Failure(AppError.Conflict("already claimed"))
        val viewModel = viewModel()
        viewModel.onIntent(ListingDetailIntent.Load("l1"))

        viewModel.onIntent(ListingDetailIntent.ClaimClicked)

        val state = viewModel.state.value
        assertEquals("already claimed", state.claimError)
        assertFalse(state.claiming)
    }

    // --- report / block ------------------------------------------------------

    @Test
    fun `report sheet open dismiss and reason selection update state`() {
        val viewModel = viewModel()

        viewModel.onIntent(ListingDetailIntent.OpenReportSheet)
        assertTrue(viewModel.state.value.showReportSheet)

        viewModel.onIntent(ListingDetailIntent.SelectReportReason("spam"))
        assertEquals("spam", viewModel.state.value.selectedReportReason)

        viewModel.onIntent(ListingDetailIntent.DismissReportSheet)
        assertFalse(viewModel.state.value.showReportSheet)
    }

    @Test
    fun `SubmitReport sends the report and marks success`() {
        coEvery { getListing("l1") } returns DataResult.Success(detail(id = "l1"))
        val viewModel = viewModel()
        viewModel.onIntent(ListingDetailIntent.Load("l1"))
        viewModel.onIntent(ListingDetailIntent.SelectReportReason("spam"))

        viewModel.onIntent(ListingDetailIntent.SubmitReport)

        coVerify(exactly = 1) { report(ReportTargetType.LISTING, "l1", "spam") }
        val state = viewModel.state.value
        assertTrue(state.reportSuccess)
        assertFalse(state.showReportSheet)
        assertFalse(state.submittingReport)
    }

    @Test
    fun `SubmitReport is a no-op without a selected reason`() {
        coEvery { getListing("l1") } returns DataResult.Success(detail(id = "l1"))
        val viewModel = viewModel()
        viewModel.onIntent(ListingDetailIntent.Load("l1"))

        viewModel.onIntent(ListingDetailIntent.SubmitReport)

        coVerify(exactly = 0) { report(any(), any(), any()) }
    }

    @Test
    fun `BlockAuthor blocks the listing author and closes the sheet`() {
        coEvery { getListing("l1") } returns DataResult.Success(
            detail(id = "l1", authorId = "author-9"),
        )
        val viewModel = viewModel()
        viewModel.onIntent(ListingDetailIntent.Load("l1"))
        viewModel.onIntent(ListingDetailIntent.OpenReportSheet)

        viewModel.onIntent(ListingDetailIntent.BlockAuthor)

        coVerify(exactly = 1) { blockUser("author-9") }
        assertFalse(viewModel.state.value.showReportSheet)
    }

    // --- navigation effects --------------------------------------------------

    @Test
    fun `AuthorClicked emits an OpenAuthor effect`() = runTest {
        coEvery { getListing("l1") } returns DataResult.Success(detail(id = "l1", authorId = "author-9"))
        val viewModel = viewModel()
        viewModel.onIntent(ListingDetailIntent.Load("l1"))

        viewModel.effect.test {
            viewModel.onIntent(ListingDetailIntent.AuthorClicked)
            assertEquals(ListingDetailEffect.OpenAuthor("author-9"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `BackClicked emits a NavigateBack effect`() = runTest {
        val viewModel = viewModel()

        viewModel.effect.test {
            viewModel.onIntent(ListingDetailIntent.BackClicked)
            assertEquals(ListingDetailEffect.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- fixtures ------------------------------------------------------------

    private fun claim(id: String = "claim", chatThreadId: String? = "thread") = Claim(
        id = id,
        listingId = "listing",
        listingType = ListingType.REQUEST,
        status = ClaimStatus.ACTIVE,
        chatThreadId = chatThreadId,
        claimedAt = null,
    )

    private fun detail(
        id: String = "l1",
        title: String = "title",
        authorId: String = "author",
    ) = ListingDetail(
        id = id,
        type = ListingType.REQUEST,
        status = ListingStatus.OPEN,
        title = title,
        description = "description",
        quantity = null,
        category = null,
        urgency = null,
        availableUntil = null,
        areaLabel = null,
        locationFuzzed = null,
        locationExact = null,
        images = emptyList(),
        author = Author(
            id = authorId,
            displayName = null,
            photoUrl = null,
            trustScore = null,
            isIdVerified = false,
        ),
        expiresAt = null,
        createdAt = "2026-01-01T00:00:00",
    )
}
