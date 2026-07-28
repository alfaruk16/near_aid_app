package com.nearaid.feature.activity

import androidx.lifecycle.viewModelScope
import com.nearaid.core.common.mvi.MviViewModel
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.ConfirmReceiptUseCase
import com.nearaid.core.domain.usecase.GetMyClaimsUseCase
import com.nearaid.core.domain.usecase.GetMyListingsUseCase
import com.nearaid.core.domain.usecase.MarkDeliveredUseCase
import com.nearaid.core.domain.usecase.WithdrawClaimUseCase
import com.nearaid.core.model.ClaimStatus
import com.nearaid.core.model.ListingType
import com.nearaid.core.proximity.HandoffToken
import com.nearaid.core.proximity.ProximityConfirmer
import com.nearaid.core.proximity.ProximityResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ActivityViewModel(
    private val getMyClaims: GetMyClaimsUseCase,
    private val getMyListings: GetMyListingsUseCase,
    private val markDelivered: MarkDeliveredUseCase,
    private val confirmReceipt: ConfirmReceiptUseCase,
    private val withdrawClaim: WithdrawClaimUseCase,
    private val proximityConfirmer: ProximityConfirmer,
) : MviViewModel<ActivityState, ActivityIntent, ActivityEffect>() {

    override fun initialState(): ActivityState = ActivityState()

    /** The receiving party's advertise/scan loop, kept so it can be cancelled when it stops. */
    private var receiverJob: Job? = null

    init {
        loadAll()
    }

    override fun onIntent(intent: ActivityIntent) {
        when (intent) {
            is ActivityIntent.SelectTab -> setState { copy(selectedTab = intent.index) }
            ActivityIntent.Refresh -> loadAll()
            is ActivityIntent.ListingClicked -> sendEffect(ActivityEffect.OpenListing(intent.id))
            is ActivityIntent.ClaimClicked -> sendEffect(
                ActivityEffect.OpenChat(intent.claimId, intent.threadId, intent.title)
            )
            is ActivityIntent.MarkDelivered -> confirmProximityThenDeliver(intent.claimId)
            is ActivityIntent.MarkDeliveredManually -> {
                setState { copy(handoffFallback = null) }
                performAction { markDelivered(intent.claimId) }
            }
            is ActivityIntent.ConfirmReceipt -> performAction { confirmReceipt(intent.claimId) }
            is ActivityIntent.OwnerMarkDelivered -> confirmProximityThenDeliver(intent.claimId, reloadListings = true)
            is ActivityIntent.OwnerMarkDeliveredManually -> {
                setState { copy(handoffFallback = null) }
                performAction(reloadListings = true) { markDelivered(intent.claimId) }
            }
            is ActivityIntent.OwnerConfirmReceipt -> performAction(reloadListings = true) { confirmReceipt(intent.claimId) }
            is ActivityIntent.Withdraw -> performAction { withdrawClaim(intent.claimId) }
            is ActivityIntent.StartReceiverProximity -> startReceiverProximity(intent.claimId)
            ActivityIntent.StopReceiverProximity -> stopReceiverProximity()
            ActivityIntent.DismissActionError -> setState { copy(actionError = null) }
            ActivityIntent.DismissHandoffFallback -> setState { copy(handoffFallback = null) }
        }
    }

    /**
     * Gate the in-person handoff on a BLE proximity check: the two devices on this claim must be
     * physically together (Tier-0, client-only — matched via the claim id, no backend token).
     * Proximity *strengthens* the handoff but never *blocks* it: when BLE is unavailable (iOS, no
     * adapter, turned off) we proceed silently; when it's available but can't confirm, we surface a
     * manual-confirm fallback rather than trapping a legitimate handoff.
     */
    private fun confirmProximityThenDeliver(claimId: String, reloadListings: Boolean = false) {
        setState { copy(proximityClaimId = claimId, actionError = null, handoffFallback = null) }
        viewModelScope.launch {
            val result = proximityConfirmer.confirmNearby(HandoffToken(claimId))
            setState { copy(proximityClaimId = null) }
            when (result) {
                is ProximityResult.Confirmed,
                ProximityResult.Unavailable -> performAction(reloadListings) { markDelivered(claimId) }
                ProximityResult.Timeout -> setState {
                    copy(handoffFallback = HandoffFallback(claimId, HandoffFailureReason.NotNearby))
                }
                ProximityResult.PermissionDenied -> setState {
                    copy(handoffFallback = HandoffFallback(claimId, HandoffFailureReason.PermissionOff))
                }
                is ProximityResult.Error -> setState {
                    copy(handoffFallback = HandoffFallback(claimId, HandoffFailureReason.Error))
                }
            }
        }
    }

    /**
     * The receiving party advertises the claim's [HandoffToken] (and scans) in a loop so the other
     * device's "Mark delivered" proximity check can see it. The loop restarts on each timeout window
     * and stops once the two devices confirm they're together (then refreshes, in case the giver has
     * already delivered), the radio is unusable, or the caller stops it.
     */
    private fun startReceiverProximity(claimId: String) {
        if (state.value.receiverListeningClaimId == claimId) return
        receiverJob?.cancel()
        setState { copy(receiverListeningClaimId = claimId, actionError = null) }
        receiverJob = viewModelScope.launch {
            try {
                while (isActive) {
                    when (proximityConfirmer.confirmNearby(HandoffToken(claimId))) {
                        is ProximityResult.Confirmed -> { loadAll(); break }
                        ProximityResult.Timeout -> Unit // keep listening
                        // Terminal, non-recoverable: surface why so the tap isn't a silent no-op
                        // (e.g. Bluetooth off, or the device can't BLE-advertise).
                        ProximityResult.Unavailable -> {
                            setState { copy(handoffFallback = HandoffFallback(claimId, HandoffFailureReason.Unavailable)) }
                            break
                        }
                        ProximityResult.PermissionDenied -> {
                            setState { copy(handoffFallback = HandoffFallback(claimId, HandoffFailureReason.PermissionOff)) }
                            break
                        }
                        is ProximityResult.Error -> {
                            setState { copy(handoffFallback = HandoffFallback(claimId, HandoffFailureReason.Error)) }
                            break
                        }
                    }
                }
            } finally {
                setState { copy(receiverListeningClaimId = null) }
            }
        }
    }

    private fun stopReceiverProximity() {
        receiverJob?.cancel()
        receiverJob = null
    }

    private fun loadAll() {
        loadClaims()
        loadMyListings()
    }

    private fun loadClaims() {
        setState { copy(claimsLoading = true, claimsError = null) }
        viewModelScope.launch {
            when (val result = getMyClaims()) {
                is DataResult.Success -> {
                    val sorted = result.data.sortedWith(
                        compareBy { if (it.status == ClaimStatus.ACTIVE) 0 else 1 }
                    )
                    setState { copy(claimsLoading = false, claims = sorted) }
                }
                is DataResult.Failure -> setState {
                    copy(claimsLoading = false, claimsError = result.error.message)
                }
            }
        }
    }

    private fun loadMyListings() {
        setState { copy(listingsLoading = true, listingsError = null) }
        viewModelScope.launch {
            val requestsDeferred = async { getMyListings(ListingType.REQUEST) }
            val offersDeferred = async { getMyListings(ListingType.OFFER) }

            val requestsResult = requestsDeferred.await()
            val offersResult = offersDeferred.await()

            val requests = when (requestsResult) {
                is DataResult.Success -> requestsResult.data
                is DataResult.Failure -> emptyList()
            }
            val offers = when (offersResult) {
                is DataResult.Success -> offersResult.data
                is DataResult.Failure -> emptyList()
            }

            val errorMessage = when {
                requestsResult is DataResult.Failure && offersResult is DataResult.Failure ->
                    requestsResult.error.message
                requestsResult is DataResult.Failure -> requestsResult.error.message
                offersResult is DataResult.Failure -> offersResult.error.message
                else -> null
            }

            setState {
                copy(
                    listingsLoading = false,
                    myListings = requests + offers,
                    listingsError = errorMessage,
                )
            }
        }
    }

    private fun performAction(
        reloadListings: Boolean = false,
        action: suspend () -> DataResult<Unit>,
    ) {
        setState { copy(actionLoading = true, actionError = null) }
        viewModelScope.launch {
            when (val result = action()) {
                is DataResult.Success -> {
                    setState { copy(actionLoading = false) }
                    // Handoff steps flip both a claim's state and its listing's status, so refresh
                    // the listings too when the action originated from the "My posts" (author) side.
                    if (reloadListings) loadMyListings()
                    loadClaims()
                }
                is DataResult.Failure -> setState {
                    copy(actionLoading = false, actionError = result.error.message)
                }
            }
        }
    }
}
