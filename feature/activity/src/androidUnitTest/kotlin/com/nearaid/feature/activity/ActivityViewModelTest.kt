package com.nearaid.feature.activity

import app.cash.turbine.test
import com.nearaid.core.common.result.AppError
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.ConfirmReceiptUseCase
import com.nearaid.core.domain.usecase.GetMyClaimsUseCase
import com.nearaid.core.domain.usecase.GetMyListingsUseCase
import com.nearaid.core.domain.usecase.MarkDeliveredUseCase
import com.nearaid.core.domain.usecase.WithdrawClaimUseCase
import com.nearaid.core.model.Author
import com.nearaid.core.model.Claim
import com.nearaid.core.model.ClaimStatus
import com.nearaid.core.model.ListingCard
import com.nearaid.core.model.ListingStatus
import com.nearaid.core.model.ListingType
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
class ActivityViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getMyClaims = mockk<GetMyClaimsUseCase>()
    private val getMyListings = mockk<GetMyListingsUseCase>()
    private val markDelivered = mockk<MarkDeliveredUseCase>()
    private val confirmReceipt = mockk<ConfirmReceiptUseCase>()
    private val withdrawClaim = mockk<WithdrawClaimUseCase>()

    @Before
    fun setUp() {
        // Sensible empty-success defaults so construction (which triggers loadAll) never throws.
        coEvery { getMyClaims(any()) } returns DataResult.Success(emptyList())
        coEvery { getMyListings(any(), any()) } returns DataResult.Success(emptyList())
    }

    private fun viewModel() = ActivityViewModel(
        getMyClaims = getMyClaims,
        getMyListings = getMyListings,
        markDelivered = markDelivered,
        confirmReceipt = confirmReceipt,
        withdrawClaim = withdrawClaim,
    )

    // --- initial load --------------------------------------------------------

    @Test
    fun `loads claims and listings on init`() {
        val claims = listOf(claim("c1", ClaimStatus.ACTIVE))
        val requests = listOf(listing("r1", ListingType.REQUEST))
        val offers = listOf(listing("o1", ListingType.OFFER))
        coEvery { getMyClaims(any()) } returns DataResult.Success(claims)
        coEvery { getMyListings(ListingType.REQUEST, any()) } returns DataResult.Success(requests)
        coEvery { getMyListings(ListingType.OFFER, any()) } returns DataResult.Success(offers)

        val state = viewModel().state.value

        assertEquals(claims, state.claims)
        assertEquals(requests + offers, state.myListings)
        assertFalse(state.claimsLoading)
        assertFalse(state.listingsLoading)
        assertFalse(state.isLoading)
        assertNull(state.claimsError)
        assertNull(state.listingsError)
    }

    @Test
    fun `claims are sorted with active ones first`() {
        val completed = claim("c1", ClaimStatus.COMPLETED)
        val active = claim("c2", ClaimStatus.ACTIVE)
        val withdrawn = claim("c3", ClaimStatus.WITHDRAWN)
        coEvery { getMyClaims(any()) } returns DataResult.Success(listOf(completed, active, withdrawn))

        val state = viewModel().state.value

        // Active floats to the top; the rest keep their relative order (stable sort).
        assertEquals(listOf(active, completed, withdrawn), state.claims)
    }

    @Test
    fun `claims failure surfaces an error and clears loading`() {
        coEvery { getMyClaims(any()) } returns DataResult.Failure(AppError.Network("offline"))

        val state = viewModel().state.value

        assertEquals("offline", state.claimsError)
        assertFalse(state.claimsLoading)
        assertTrue(state.claims.isEmpty())
    }

    @Test
    fun `listings show offers when the requests call fails`() {
        val offers = listOf(listing("o1", ListingType.OFFER))
        coEvery { getMyListings(ListingType.REQUEST, any()) } returns DataResult.Failure(AppError.Server(500, "boom"))
        coEvery { getMyListings(ListingType.OFFER, any()) } returns DataResult.Success(offers)

        val state = viewModel().state.value

        assertEquals(offers, state.myListings)
        assertEquals("boom", state.listingsError)
    }

    @Test
    fun `listings are empty when both calls fail`() {
        coEvery { getMyListings(ListingType.REQUEST, any()) } returns DataResult.Failure(AppError.Network("req-fail"))
        coEvery { getMyListings(ListingType.OFFER, any()) } returns DataResult.Failure(AppError.Network("offer-fail"))

        val state = viewModel().state.value

        assertTrue(state.myListings.isEmpty())
        assertEquals("req-fail", state.listingsError)
    }

    // --- reducer-only intents ------------------------------------------------

    @Test
    fun `SelectTab updates the selected tab`() {
        val viewModel = viewModel()

        viewModel.onIntent(ActivityIntent.SelectTab(2))

        assertEquals(2, viewModel.state.value.selectedTab)
    }

    @Test
    fun `Refresh reloads claims and listings`() {
        val viewModel = viewModel()

        viewModel.onIntent(ActivityIntent.Refresh)

        // Once on init, once on refresh.
        coVerify(exactly = 2) { getMyClaims(any()) }
        coVerify(exactly = 2) { getMyListings(ListingType.REQUEST, any()) }
        coVerify(exactly = 2) { getMyListings(ListingType.OFFER, any()) }
    }

    // --- effects -------------------------------------------------------------

    @Test
    fun `ListingClicked emits an OpenListing effect`() = runTest {
        val viewModel = viewModel()

        viewModel.effect.test {
            viewModel.onIntent(ActivityIntent.ListingClicked("listing-1"))
            assertEquals(ActivityEffect.OpenListing("listing-1"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ClaimClicked emits an OpenChat effect`() = runTest {
        val viewModel = viewModel()

        viewModel.effect.test {
            viewModel.onIntent(ActivityIntent.ClaimClicked("c1", "t1", "Rice"))
            assertEquals(ActivityEffect.OpenChat("c1", "t1", "Rice"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- claim actions -------------------------------------------------------

    @Test
    fun `MarkDelivered success reloads claims`() {
        coEvery { markDelivered("c1") } returns DataResult.Success(Unit)
        val viewModel = viewModel()

        viewModel.onIntent(ActivityIntent.MarkDelivered("c1"))

        coVerify(exactly = 1) { markDelivered("c1") }
        coVerify(exactly = 2) { getMyClaims(any()) } // init + reload after the action
        assertFalse(viewModel.state.value.actionLoading)
    }

    @Test
    fun `ConfirmReceipt delegates to the confirm-receipt use case`() {
        coEvery { confirmReceipt("c1") } returns DataResult.Success(Unit)
        val viewModel = viewModel()

        viewModel.onIntent(ActivityIntent.ConfirmReceipt("c1"))

        coVerify(exactly = 1) { confirmReceipt("c1") }
    }

    @Test
    fun `Withdraw delegates to the withdraw use case`() {
        coEvery { withdrawClaim("c9") } returns DataResult.Success(Unit)
        val viewModel = viewModel()

        viewModel.onIntent(ActivityIntent.Withdraw("c9"))

        coVerify(exactly = 1) { withdrawClaim("c9") }
    }

    @Test
    fun `a failing action surfaces an action error that can be dismissed`() {
        coEvery { markDelivered("c1") } returns DataResult.Failure(AppError.Conflict("already delivered"))
        val viewModel = viewModel()

        viewModel.onIntent(ActivityIntent.MarkDelivered("c1"))
        assertEquals("already delivered", viewModel.state.value.actionError)
        assertFalse(viewModel.state.value.actionLoading)

        viewModel.onIntent(ActivityIntent.DismissActionError)
        assertNull(viewModel.state.value.actionError)
    }

    // --- fixtures ------------------------------------------------------------

    private fun claim(id: String, status: ClaimStatus) = Claim(
        id = id,
        listingId = "listing-$id",
        listingType = ListingType.REQUEST,
        status = status,
        chatThreadId = null,
        claimedAt = null,
    )

    private fun listing(id: String, type: ListingType) = ListingCard(
        id = id,
        type = type,
        title = "title-$id",
        category = null,
        urgency = null,
        availableUntil = null,
        quantity = null,
        distanceKm = null,
        areaLabel = null,
        locationFuzzed = null,
        thumbnailUrl = null,
        author = Author(
            id = "author",
            displayName = null,
            photoUrl = null,
            trustScore = null,
            isIdVerified = false,
        ),
        status = ListingStatus.OPEN,
        createdAt = "2026-01-01T00:00:00",
    )
}
