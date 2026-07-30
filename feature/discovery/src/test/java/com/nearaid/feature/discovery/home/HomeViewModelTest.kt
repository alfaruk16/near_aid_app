package com.nearaid.feature.discovery.home

import com.nearaid.feature.discovery.MainDispatcherRule
import app.cash.turbine.test
import com.nearaid.core.common.result.AppError
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.ai.TextEmbedder
import com.nearaid.core.domain.usecase.GetNearbyListingsUseCase
import com.nearaid.core.domain.usecase.ObserveCategoriesUseCase
import com.nearaid.core.domain.usecase.ObserveSearchRadiusUseCase
import com.nearaid.core.domain.usecase.RankListingsBySimilarityUseCase
import com.nearaid.core.domain.usecase.RefreshCategoriesUseCase
import com.nearaid.core.model.Author
import com.nearaid.core.model.Category
import com.nearaid.core.model.ListingCard
import com.nearaid.core.model.ListingStatus
import com.nearaid.core.model.ListingType
import com.nearaid.core.model.Page
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getNearbyListings = mockk<GetNearbyListingsUseCase>()
    private val observeCategories = mockk<ObserveCategoriesUseCase>()
    private val refreshCategories = mockk<RefreshCategoriesUseCase>()
    private val observeSearchRadius = mockk<ObserveSearchRadiusUseCase>()

    @Before
    fun setUp() {
        // Defaults so construction (which observes categories/radius and loads listings) never throws.
        every { observeCategories() } returns flowOf(emptyList())
        every { observeSearchRadius() } returns flowOf(5.0)
        coEvery { refreshCategories() } returns DataResult.Success(emptyList())
        coEvery { getNearbyListings(any(), any()) } returns DataResult.Success(emptyPage())
    }

    // Real ranking use case with a no-op embedder: a blank query (the default) short-circuits
    // to the original order, so paging/reload assertions see the server order untouched.
    private val noopEmbedder = object : TextEmbedder {
        override suspend fun embed(text: String): FloatArray? = null
    }
    private val rankBySimilarity = RankListingsBySimilarityUseCase(noopEmbedder)

    private fun viewModel() = HomeViewModel(
        getNearbyListings = getNearbyListings,
        observeCategories = observeCategories,
        refreshCategories = refreshCategories,
        observeSearchRadius = observeSearchRadius,
        rankBySimilarity = rankBySimilarity,
    )

    // --- initial load --------------------------------------------------------

    @Test
    fun `observes categories and radius and loads listings on init`() {
        val categories = listOf(category("food"))
        val listings = listOf(listing("l1"))
        every { observeCategories() } returns flowOf(categories)
        every { observeSearchRadius() } returns flowOf(7.5)
        coEvery { getNearbyListings(any(), any()) } returns DataResult.Success(page(listings))

        val state = viewModel().state.value

        assertEquals(categories, state.categories)
        assertEquals(7.5, state.radiusKm, 0.0)
        assertEquals(listings, state.listings)
        assertFalse(state.loading)
        assertNull(state.error)
        coVerify(exactly = 1) { refreshCategories() }
    }

    @Test
    fun `defaults to the needs tab and requests listings of type REQUEST`() {
        viewModel()

        coVerify { getNearbyListings(match { it.type == ListingType.REQUEST }, any()) }
    }

    @Test
    fun `listings failure surfaces an error and clears loading`() {
        coEvery { getNearbyListings(any(), any()) } returns DataResult.Failure(AppError.Network("offline"))

        val state = viewModel().state.value

        assertEquals("offline", state.error)
        assertFalse(state.loading)
        assertEquals(emptyList<ListingCard>(), state.listings)
    }

    // --- reducer + reload intents --------------------------------------------

    @Test
    fun `SelectTab switches to offers clears category and reloads`() {
        val viewModel = viewModel()

        viewModel.onIntent(HomeIntent.SelectTab(1))

        val state = viewModel.state.value
        assertEquals(1, state.selectedTabIndex)
        assertNull(state.selectedCategoryKey)
        coVerify { getNearbyListings(match { it.type == ListingType.OFFER }, any()) }
    }

    @Test
    fun `SelectCategory sets the filter and reloads with that category`() {
        val viewModel = viewModel()

        viewModel.onIntent(HomeIntent.SelectCategory("medicine"))

        assertEquals("medicine", viewModel.state.value.selectedCategoryKey)
        coVerify { getNearbyListings(match { it.categories == listOf("medicine") }, any()) }
    }

    @Test
    fun `Refresh reloads listings`() {
        val viewModel = viewModel()

        viewModel.onIntent(HomeIntent.Refresh)

        // Once during init, once for the explicit refresh.
        coVerify(exactly = 2) { getNearbyListings(any(), any()) }
    }

    // --- paging --------------------------------------------------------------

    @Test
    fun `LoadMore appends the next page and requests it with the cursor`() {
        every { observeSearchRadius() } returns flowOf(5.0)
        coEvery { getNearbyListings(any(), null) } returns
            DataResult.Success(page(listOf(listing("l1")), nextCursor = "c1", hasMore = true))
        coEvery { getNearbyListings(any(), "c1") } returns
            DataResult.Success(page(listOf(listing("l2")), nextCursor = null, hasMore = false))

        val viewModel = viewModel()
        assertEquals(listOf("l1"), viewModel.state.value.listings.map { it.id })

        viewModel.onIntent(HomeIntent.LoadMore)

        val state = viewModel.state.value
        assertEquals(listOf("l1", "l2"), state.listings.map { it.id })
        assertFalse(state.hasMore)
        assertFalse(state.loadingMore)
        coVerify { getNearbyListings(any(), "c1") }
    }

    @Test
    fun `LoadMore is a no-op when there is no next page`() {
        // emptyPage() has nextCursor = null, so there is nothing more to fetch.
        val viewModel = viewModel()

        viewModel.onIntent(HomeIntent.LoadMore)

        // Only the init load happened; no cursored fetch.
        coVerify(exactly = 1) { getNearbyListings(any(), any()) }
    }

    // --- effects -------------------------------------------------------------

    @Test
    fun `ListingClicked emits an OpenListing effect`() = runTest {
        val viewModel = viewModel()

        viewModel.effect.test {
            viewModel.onIntent(HomeIntent.ListingClicked("listing-1"))
            assertEquals(HomeEffect.OpenListing("listing-1"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OpenNotificationsClicked emits an OpenNotifications effect`() = runTest {
        val viewModel = viewModel()

        viewModel.effect.test {
            viewModel.onIntent(HomeIntent.OpenNotificationsClicked)
            assertEquals(HomeEffect.OpenNotifications, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- fixtures ------------------------------------------------------------

    private fun emptyPage() = page(emptyList())

    private fun page(
        items: List<ListingCard>,
        nextCursor: String? = null,
        hasMore: Boolean = false,
    ) = Page(items = items, nextCursor = nextCursor, hasMore = hasMore)

    private fun category(key: String) = Category(
        id = key.hashCode(),
        key = key,
        nameEn = key,
        nameBn = key,
        icon = null,
    )

    private fun listing(id: String) = ListingCard(
        id = id,
        type = ListingType.REQUEST,
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
