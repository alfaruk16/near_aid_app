package com.nearaid.feature.discovery.listingdetail

import androidx.lifecycle.viewModelScope
import com.nearaid.core.common.mvi.MviViewModel
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.BlockUserUseCase
import com.nearaid.core.domain.usecase.ClaimListingUseCase
import com.nearaid.core.domain.usecase.GetListingUseCase
import com.nearaid.core.domain.usecase.ReportUseCase
import com.nearaid.core.model.ReportTargetType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListingDetailViewModel @Inject constructor(
    private val getListing: GetListingUseCase,
    private val claimListing: ClaimListingUseCase,
    private val report: ReportUseCase,
    private val blockUser: BlockUserUseCase,
) : MviViewModel<ListingDetailState, ListingDetailIntent, ListingDetailEffect>() {

    override fun initialState() = ListingDetailState()

    override fun onIntent(intent: ListingDetailIntent) {
        when (intent) {
            is ListingDetailIntent.Load -> loadListing(intent.listingId)
            ListingDetailIntent.ClaimClicked -> claim()
            ListingDetailIntent.OpenReportSheet ->
                setState { copy(showReportSheet = true, selectedReportReason = null, reportSuccess = false) }
            ListingDetailIntent.DismissReportSheet ->
                setState { copy(showReportSheet = false) }
            is ListingDetailIntent.SelectReportReason ->
                setState { copy(selectedReportReason = intent.reason) }
            ListingDetailIntent.SubmitReport -> submitReport()
            ListingDetailIntent.BlockAuthor -> blockAuthor()
            ListingDetailIntent.AuthorClicked -> {
                val authorId = currentState.listing?.author?.id ?: return
                sendEffect(ListingDetailEffect.OpenAuthor(authorId))
            }
            ListingDetailIntent.BackClicked -> sendEffect(ListingDetailEffect.NavigateBack)
        }
    }

    private fun loadListing(id: String) {
        viewModelScope.launch {
            setState { copy(loading = true, error = null) }
            when (val result = getListing(id)) {
                is DataResult.Success -> setState { copy(loading = false, listing = result.data) }
                is DataResult.Failure -> setState { copy(loading = false, error = result.error.message) }
            }
        }
    }

    private fun claim() {
        val listingId = currentState.listing?.id ?: return
        viewModelScope.launch {
            setState { copy(claiming = true, claimError = null) }
            when (val result = claimListing(listingId)) {
                is DataResult.Success -> {
                    setState { copy(claiming = false) }
                    val claim = result.data
                    val title = currentState.listing?.title ?: ""
                    sendEffect(
                        ListingDetailEffect.OpenChat(
                            claimId = claim.id,
                            threadId = claim.chatThreadId ?: "",
                            title = title,
                        )
                    )
                }
                is DataResult.Failure -> setState { copy(claiming = false, claimError = result.error.message) }
            }
        }
    }

    private fun submitReport() {
        val listingId = currentState.listing?.id ?: return
        val reason = currentState.selectedReportReason ?: return
        viewModelScope.launch {
            setState { copy(submittingReport = true) }
            report(ReportTargetType.LISTING, listingId, reason)
            setState { copy(submittingReport = false, reportSuccess = true, showReportSheet = false) }
        }
    }

    private fun blockAuthor() {
        val authorId = currentState.listing?.author?.id ?: return
        viewModelScope.launch {
            blockUser(authorId)
            setState { copy(showReportSheet = false) }
        }
    }
}
