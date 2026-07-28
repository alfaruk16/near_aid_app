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
    /** Claim currently running a BLE proximity check before delivery (drives the button spinner). */
    val proximityClaimId: String? = null,
    /**
     * Claim the receiving party is actively advertising for over BLE while awaiting the handoff, so
     * the other device's "Mark delivered" proximity check can discover it. Client-only, no backend.
     */
    val receiverListeningClaimId: String? = null,
    /** Set when the proximity check couldn't confirm; the row then offers a manual-confirm fallback. */
    val handoffFallback: HandoffFallback? = null,
) : UiState {
    val isLoading: Boolean get() = claimsLoading || listingsLoading
}

/** Why a proximity check did not confirm — mapped to a localized message by the screen. */
enum class HandoffFailureReason { NotNearby, PermissionOff, Error, Unavailable }

data class HandoffFallback(val claimId: String, val reason: HandoffFailureReason)

sealed interface ActivityIntent : UiIntent {
    data class SelectTab(val index: Int) : ActivityIntent
    data object Refresh : ActivityIntent
    data class ListingClicked(val id: String) : ActivityIntent
    data class ClaimClicked(val claimId: String, val threadId: String, val title: String) : ActivityIntent
    /** In-person handoff: verify the two devices are together over BLE, then mark delivered. */
    data class MarkDelivered(val claimId: String) : ActivityIntent
    /** Bypass the proximity check (fallback offered after it can't confirm). */
    data class MarkDeliveredManually(val claimId: String) : ActivityIntent
    data class ConfirmReceipt(val claimId: String) : ActivityIntent
    /** Author-side handoff from "My posts": giver marks an offer delivered (BLE proximity-gated). */
    data class OwnerMarkDelivered(val claimId: String) : ActivityIntent
    /** Bypass the proximity check for the giver's deliver (fallback offered after it can't confirm). */
    data class OwnerMarkDeliveredManually(val claimId: String) : ActivityIntent
    /** Author-side handoff from "My posts": seeker confirms receipt on a delivered request. */
    data class OwnerConfirmReceipt(val claimId: String) : ActivityIntent
    data class Withdraw(val claimId: String) : ActivityIntent
    /** Receiving party starts advertising over BLE so the giver's deliver can confirm proximity. */
    data class StartReceiverProximity(val claimId: String) : ActivityIntent
    /** Stop the receiving-party advertising (tapped stop, or the row left the screen). */
    data object StopReceiverProximity : ActivityIntent
    data object DismissActionError : ActivityIntent
    data object DismissHandoffFallback : ActivityIntent
}

sealed interface ActivityEffect : UiEffect {
    data class OpenListing(val id: String) : ActivityEffect
    data class OpenChat(val claimId: String, val threadId: String, val title: String) : ActivityEffect
}
