package com.nearaid.feature.discovery.home

import com.nearaid.core.model.Category
import com.nearaid.core.model.ListingCard
import com.nearaid.core.model.ListingType
import com.nearaid.core.common.mvi.UiEffect
import com.nearaid.core.common.mvi.UiIntent
import com.nearaid.core.common.mvi.UiState

data class HomeState(
    val selectedTabIndex: Int = 0,           // 0 = Needs/REQUEST, 1 = Offers/OFFER
    val categories: List<Category> = emptyList(),
    val selectedCategoryKey: String? = null, // null means "All"
    val listings: List<ListingCard> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val radiusKm: Double = 5.0,
) : UiState

internal val HomeState.selectedType: ListingType
    get() = if (selectedTabIndex == 0) ListingType.REQUEST else ListingType.OFFER

sealed interface HomeIntent : UiIntent {
    data class SelectTab(val index: Int) : HomeIntent
    data class SelectCategory(val key: String?) : HomeIntent
    data class ListingClicked(val id: String) : HomeIntent
    data object OpenNotificationsClicked : HomeIntent
    data object Refresh : HomeIntent
}

sealed interface HomeEffect : UiEffect {
    data class OpenListing(val id: String) : HomeEffect
    data object OpenNotifications : HomeEffect
}
