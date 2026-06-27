package com.nearaid.feature.discovery.listingdetail

import com.nearaid.core.model.ListingDetail
import com.nearaid.core.common.mvi.UiEffect
import com.nearaid.core.common.mvi.UiIntent
import com.nearaid.core.common.mvi.UiState

data class ListingDetailState(
    val listing: ListingDetail? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val claiming: Boolean = false,
    val claimError: String? = null,
    val showReportSheet: Boolean = false,
    val selectedReportReason: String? = null,
    val submittingReport: Boolean = false,
    val reportSuccess: Boolean = false,
) : UiState

sealed interface ListingDetailIntent : UiIntent {
    data class Load(val listingId: String) : ListingDetailIntent
    data object ClaimClicked : ListingDetailIntent
    data object OpenReportSheet : ListingDetailIntent
    data object DismissReportSheet : ListingDetailIntent
    data class SelectReportReason(val reason: String) : ListingDetailIntent
    data object SubmitReport : ListingDetailIntent
    data object BlockAuthor : ListingDetailIntent
    data object AuthorClicked : ListingDetailIntent
    data object BackClicked : ListingDetailIntent
}

sealed interface ListingDetailEffect : UiEffect {
    data class OpenChat(val claimId: String, val threadId: String, val title: String) : ListingDetailEffect
    data class OpenAuthor(val userId: String) : ListingDetailEffect
    data object NavigateBack : ListingDetailEffect
}
