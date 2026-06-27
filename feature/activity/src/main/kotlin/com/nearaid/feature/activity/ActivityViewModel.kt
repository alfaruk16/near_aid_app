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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val getMyClaims: GetMyClaimsUseCase,
    private val getMyListings: GetMyListingsUseCase,
    private val markDelivered: MarkDeliveredUseCase,
    private val confirmReceipt: ConfirmReceiptUseCase,
    private val withdrawClaim: WithdrawClaimUseCase,
) : MviViewModel<ActivityState, ActivityIntent, ActivityEffect>() {

    override fun initialState(): ActivityState = ActivityState()

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
            is ActivityIntent.MarkDelivered -> performAction { markDelivered(intent.claimId) }
            is ActivityIntent.ConfirmReceipt -> performAction { confirmReceipt(intent.claimId) }
            is ActivityIntent.Withdraw -> performAction { withdrawClaim(intent.claimId) }
            ActivityIntent.DismissActionError -> setState { copy(actionError = null) }
        }
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

    private fun performAction(action: suspend () -> DataResult<Unit>) {
        setState { copy(actionLoading = true, actionError = null) }
        viewModelScope.launch {
            when (val result = action()) {
                is DataResult.Success -> {
                    setState { copy(actionLoading = false) }
                    loadClaims()
                }
                is DataResult.Failure -> setState {
                    copy(actionLoading = false, actionError = result.error.message)
                }
            }
        }
    }
}
