package com.nearaid.feature.activity

import com.nearaid.core.common.mvi.UiEffect
import com.nearaid.core.common.mvi.UiIntent
import com.nearaid.core.common.mvi.UiState
import com.nearaid.core.model.Claim
import com.nearaid.core.model.ListingCard

data class ActivityState(
    val claims: List<Claim> = emptyList(),
    val myListings: List<ListingCard> = emptyList(),
    val claimsLoading: Boolean = false,
    val listingsLoading: Boolean = false,
    val claimsError: String? = null,
    val listingsError: String? = null,
    val actionError: String? = null,
    val actionLoading: Boolean = false,
    val selectedTab: Int = 0,
) : UiState {
    val isLoading: Boolean get() = claimsLoading || listingsLoading
}

sealed interface ActivityIntent : UiIntent {
    data class SelectTab(val index: Int) : ActivityIntent
    data object Refresh : ActivityIntent
    data class ListingClicked(val id: String) : ActivityIntent
    data class ClaimClicked(val claimId: String, val threadId: String, val title: String) : ActivityIntent
    data class MarkDelivered(val claimId: String) : ActivityIntent
    data class ConfirmReceipt(val claimId: String) : ActivityIntent
    data class Withdraw(val claimId: String) : ActivityIntent
    data object DismissActionError : ActivityIntent
}

sealed interface ActivityEffect : UiEffect {
    data class OpenListing(val id: String) : ActivityEffect
    data class OpenChat(val claimId: String, val threadId: String, val title: String) : ActivityEffect
}
