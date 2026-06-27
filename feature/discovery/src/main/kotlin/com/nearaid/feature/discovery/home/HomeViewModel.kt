package com.nearaid.feature.discovery.home

import androidx.lifecycle.viewModelScope
import com.nearaid.core.common.mvi.MviViewModel
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.GetNearbyListingsUseCase
import com.nearaid.core.domain.usecase.ObserveCategoriesUseCase
import com.nearaid.core.domain.usecase.ObserveSearchRadiusUseCase
import com.nearaid.core.domain.usecase.RefreshCategoriesUseCase
import com.nearaid.core.model.DiscoveryQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO: Wire fused location provider when GPS is enabled in v2.
private const val DHAKA_LAT = 23.8103
private const val DHAKA_LNG = 90.4125

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getNearbyListings: GetNearbyListingsUseCase,
    private val observeCategories: ObserveCategoriesUseCase,
    private val refreshCategories: RefreshCategoriesUseCase,
    private val observeSearchRadius: ObserveSearchRadiusUseCase,
) : MviViewModel<HomeState, HomeIntent, HomeEffect>() {

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
                is DataResult.Success -> setState { copy(loading = false, listings = result.data.items) }
                is DataResult.Failure -> setState { copy(loading = false, error = result.error.message) }
            }
        }
    }
}
