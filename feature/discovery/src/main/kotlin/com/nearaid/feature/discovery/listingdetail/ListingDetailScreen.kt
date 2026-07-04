package com.nearaid.feature.discovery.listingdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nearaid.feature.discovery.R
import com.nearaid.core.common.util.TimeFormat
import com.nearaid.core.designsystem.component.Avatar
import com.nearaid.core.designsystem.component.accessibleClickable
import com.nearaid.core.designsystem.component.headingSemantics
import com.nearaid.core.designsystem.component.statusSemantics
import com.nearaid.core.designsystem.component.CategoryIconBox
import com.nearaid.core.designsystem.component.CollectEffect
import com.nearaid.core.designsystem.component.NearAidButton
import com.nearaid.core.designsystem.component.NearAidButtonVariant
import com.nearaid.core.designsystem.component.NearAidTopBar
import com.nearaid.core.designsystem.component.StatusPill
import com.nearaid.core.designsystem.component.TagChip
import com.nearaid.core.designsystem.component.TrustScore
import com.nearaid.core.designsystem.component.UrgencyTag
import com.nearaid.core.designsystem.component.VerifiedBadge
import com.nearaid.core.designsystem.theme.NearAidTheme
import com.nearaid.core.model.ListingType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingDetailScreen(
    listingId: String,
    onBack: () -> Unit,
    onOpenChat: (claimId: String, threadId: String, title: String) -> Unit,
    onAuthorClick: (userId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ListingDetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(listingId) {
        viewModel.onIntent(ListingDetailIntent.Load(listingId))
    }

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is ListingDetailEffect.OpenChat -> onOpenChat(effect.claimId, effect.threadId, effect.title)
            is ListingDetailEffect.OpenAuthor -> onAuthorClick(effect.userId)
            ListingDetailEffect.NavigateBack -> onBack()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            NearAidTopBar(
                title = "",
                onBack = onBack,
                actions = {
                    if (state.listing != null) {
                        IconButton(onClick = { viewModel.onIntent(ListingDetailIntent.OpenReportSheet) }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = stringResource(R.string.cd_more_options), tint = NearAidTheme.colors.ink2)
                        }
                    }
                },
            )

            when {
                state.loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            color = NearAidTheme.colors.marigold,
                            modifier = Modifier.statusSemantics(stringResource(R.string.loading)),
                        )
                    }
                }

                state.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = state.error ?: stringResource(R.string.detail_error_generic),
                            style = MaterialTheme.typography.bodyMedium,
                            color = NearAidTheme.colors.rust,
                            modifier = Modifier.padding(24.dp),
                        )
                    }
                }

                state.listing != null -> {
                    val listing = state.listing!!
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                    ) {
                        Spacer(Modifier.height(8.dp))

                        // Category icon + status + urgency/until tag row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            CategoryIconBox(categoryKey = listing.category?.key, boxSize = 52)
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                listing.category?.let {
                                    Text(
                                        text = it.nameEn,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NearAidTheme.colors.ink3,
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    StatusPill(status = listing.status)
                                    when {
                                        listing.type == ListingType.REQUEST && listing.urgency != null ->
                                            UrgencyTag(urgency = listing.urgency!!)
                                        listing.type == ListingType.OFFER && listing.availableUntil != null ->
                                            TagChip(
                                                label = stringResource(R.string.detail_offer_available_until, TimeFormat.timeOfDay(listing.availableUntil)),
                                                container = NearAidTheme.colors.tealTint,
                                                content = NearAidTheme.colors.teal,
                                            )
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        // Title
                        Text(
                            text = listing.title,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.headingSemantics(),
                        )

                        Spacer(Modifier.height(10.dp))

                        // Description
                        Text(
                            text = listing.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = NearAidTheme.colors.ink2,
                        )

                        Spacer(Modifier.height(16.dp))

                        // Quantity & distance info row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .background(NearAidTheme.colors.line2)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            listing.quantity?.let { qty ->
                                Column {
                                    Text(stringResource(R.string.detail_label_quantity), style = MaterialTheme.typography.labelSmall, color = NearAidTheme.colors.ink3)
                                    Text(qty, style = MaterialTheme.typography.titleSmall)
                                }
                            }
                            listing.areaLabel?.let { area ->
                                Column {
                                    Text(stringResource(R.string.detail_label_area), style = MaterialTheme.typography.labelSmall, color = NearAidTheme.colors.ink3)
                                    Text(area, style = MaterialTheme.typography.titleSmall)
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        HorizontalDivider(color = NearAidTheme.colors.line)

                        Spacer(Modifier.height(14.dp))

                        // Author row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .background(NearAidTheme.colors.surface)
                                .accessibleClickable(onClickLabel = stringResource(R.string.cd_view_profile)) { viewModel.onIntent(ListingDetailIntent.AuthorClicked) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Avatar(
                                name = listing.author.displayName,
                                photoUrl = listing.author.photoUrl,
                                size = 44,
                            )
                            Column(Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Text(
                                        text = listing.author.displayName ?: stringResource(R.string.detail_author_fallback_name),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    if (listing.author.isIdVerified) {
                                        VerifiedBadge()
                                    }
                                }
                                TrustScore(score = listing.author.trustScore)
                            }
                        }

                        // Error from claim attempt
                        state.claimError?.let { err ->
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = err,
                                style = MaterialTheme.typography.bodySmall,
                                color = NearAidTheme.colors.rust,
                            )
                        }

                        // Extra bottom padding for the bottom bar
                        Spacer(Modifier.height(90.dp))
                    }

                    // Bottom action bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NearAidTheme.colors.paper)
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .navigationBarsPadding(),
                    ) {
                        val actionLabel = if (listing.type == ListingType.REQUEST) stringResource(R.string.detail_action_offer_help) else stringResource(R.string.detail_action_request_this)
                        NearAidButton(
                            text = actionLabel,
                            onClick = { viewModel.onIntent(ListingDetailIntent.ClaimClicked) },
                            loading = state.claiming,
                            enabled = !state.claiming,
                        )
                    }
                }
            }
        }
    }

    // Report bottom sheet
    if (state.showReportSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { viewModel.onIntent(ListingDetailIntent.DismissReportSheet) },
            sheetState = sheetState,
            containerColor = NearAidTheme.colors.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    stringResource(R.string.report_title),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp).headingSemantics(),
                )
                val reportReasons = listOf(
                    stringResource(R.string.report_reason_scam),
                    stringResource(R.string.report_reason_money),
                    stringResource(R.string.report_reason_offensive),
                    stringResource(R.string.report_reason_spam),
                )
                Column(Modifier.selectableGroup()) {
                    reportReasons.forEach { reason ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = state.selectedReportReason == reason,
                                    onClick = { viewModel.onIntent(ListingDetailIntent.SelectReportReason(reason)) },
                                    role = Role.RadioButton,
                                )
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            RadioButton(
                                selected = state.selectedReportReason == reason,
                                onClick = null,
                            )
                            Text(reason, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                NearAidButton(
                    text = stringResource(R.string.report_submit),
                    onClick = { viewModel.onIntent(ListingDetailIntent.SubmitReport) },
                    variant = NearAidButtonVariant.Rust,
                    enabled = state.selectedReportReason != null && !state.submittingReport,
                    loading = state.submittingReport,
                )
                Spacer(Modifier.height(8.dp))
                NearAidButton(
                    text = stringResource(R.string.report_block_user),
                    onClick = { viewModel.onIntent(ListingDetailIntent.BlockAuthor) },
                    variant = NearAidButtonVariant.Ghost,
                    enabled = !state.submittingReport,
                )
                Spacer(Modifier.height(8.dp))
                NearAidButton(
                    text = stringResource(R.string.report_cancel),
                    onClick = { viewModel.onIntent(ListingDetailIntent.DismissReportSheet) },
                    variant = NearAidButtonVariant.Ghost,
                    enabled = !state.submittingReport,
                )
            }
        }
    }
}
