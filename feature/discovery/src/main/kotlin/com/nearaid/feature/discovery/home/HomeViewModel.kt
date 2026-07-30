package com.nearaid.feature.discovery.home

import androidx.lifecycle.viewModelScope
import com.nearaid.core.common.mvi.MviViewModel
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.GetNearbyListingsUseCase
import com.nearaid.core.domain.usecase.ObserveCategoriesUseCase
import com.nearaid.core.domain.usecase.ObserveSearchRadiusUseCase
import com.nearaid.core.domain.usecase.RankListingsBySimilarityUseCase
import com.nearaid.core.domain.usecase.RefreshCategoriesUseCase
import com.nearaid.core.model.DiscoveryQuery
import com.nearaid.core.model.ListingCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO: Wire fused location provider when GPS is enabled in v2.
private const val DHAKA_LAT = 23.8103
private const val DHAKA_LNG = 90.4125

/** Wait this long after the last keystroke before re-ranking, to avoid churn while typing. */
private const val SEARCH_DEBOUNCE_MS = 250L

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getNearbyListings: GetNearbyListingsUseCase,
    private val observeCategories: ObserveCategoriesUseCase,
    private val refreshCategories: RefreshCategoriesUseCase,
    private val observeSearchRadius: ObserveSearchRadiusUseCase,
    private val rankBySimilarity: RankListingsBySimilarityUseCase,
) : MviViewModel<HomeState, HomeIntent, HomeEffect>() {

    /** Server-ordered listings as fetched, before on-device semantic re-ranking. */
    private var sourceListings: List<ListingCard> = emptyList()

    /** Latest re-rank coroutine; cancelled on each keystroke so only the last one runs. */
    private var rankJob: Job? = null

    override fun initialState() = HomeState()

    init {
        viewModelScope.launch {
            observeCategories().collect { cats ->
                setState { copy(categories = cats) }
            }
        }
        viewModelScope.launch {
            observeSearchRadius().collect { radius ->
                setState { copy(radiusKm = radius) }
                loadListings()
            }
        }
        viewModelScope.launch {
            refreshCategories()
        }
    }

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.SelectTab -> {
                setState { copy(selectedTabIndex = intent.index, selectedCategoryKey = null) }
                loadListings()
            }
            is HomeIntent.SelectCategory -> {
                setState { copy(selectedCategoryKey = intent.key) }
                loadListings()
            }
            is HomeIntent.SearchChanged -> {
                setState { copy(searchQuery = intent.query) }
                applyRanking(debounce = true)
            }
            is HomeIntent.ListingClicked -> sendEffect(HomeEffect.OpenListing(intent.id))
            HomeIntent.OpenNotificationsClicked -> sendEffect(HomeEffect.OpenNotifications)
            HomeIntent.Refresh -> loadListings()
        }
    }

    private fun loadListings() {
        viewModelScope.launch {
            val radius = observeSearchRadius().first()
            val categoryFilter = listOfNotNull(currentState.selectedCategoryKey)
            val query = DiscoveryQuery(
                type = currentState.selectedType,
                lat = DHAKA_LAT,
                lng = DHAKA_LNG,
                radiusKm = radius,
                categories = categoryFilter,
            )
            setState { copy(loading = true, error = null) }
            when (val result = getNearbyListings(query)) {
                is DataResult.Success -> {
                    sourceListings = result.data.items
                    setState { copy(loading = false) }
                    applyRanking()
                }
                is DataResult.Failure -> setState { copy(loading = false, error = result.error.message) }
            }
        }
    }

    /**
     * Re-orders [sourceListings] by on-device semantic similarity to the current search
     * query and publishes the result. A blank query (or unavailable model) keeps the
     * server's distance-based order. Runs off the main thread inside the use case.
     *
     * @param debounce when true (typing), waits [SEARCH_DEBOUNCE_MS] and supersedes any
     * in-flight re-rank; when false (fresh fetch), re-ranks immediately.
     */
    private fun applyRanking(debounce: Boolean = false) {
        rankJob?.cancel()
        rankJob = viewModelScope.launch {
            if (debounce) delay(SEARCH_DEBOUNCE_MS)
            val ranked = rankBySimilarity(currentState.searchQuery, sourceListings)
            setState { copy(listings = ranked) }
        }
    }
}
