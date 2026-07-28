package com.nearaid.feature.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nearaid.core.designsystem.component.CategoryIconBox
import com.nearaid.core.designsystem.component.CollectEffect
import com.nearaid.core.designsystem.component.accessibleClickable
import com.nearaid.core.designsystem.component.statusSemantics
import com.nearaid.core.designsystem.component.EmptyState
import com.nearaid.core.designsystem.component.ListingCardView
import com.nearaid.core.designsystem.component.NearAidButton
import com.nearaid.core.designsystem.component.NearAidButtonVariant
import com.nearaid.core.designsystem.component.NearAidSegmentedTabs
import com.nearaid.core.designsystem.component.NearAidTopBar
import com.nearaid.core.designsystem.component.StatusPill
import com.nearaid.core.common.util.TimeFormat
import com.nearaid.core.designsystem.theme.NearAidTheme
import com.nearaid.core.model.Claim
import com.nearaid.core.model.ClaimStatus
import com.nearaid.core.model.ListingStatus
import com.nearaid.core.model.ListingType
import com.nearaid.feature.activity.resources.Res
import com.nearaid.feature.activity.resources.*

@Composable
fun ActivityScreen(
    onListingClick: (id: String) -> Unit,
    onOpenChat: (claimId: String, threadId: String, title: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ActivityViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is ActivityEffect.OpenListing -> onListingClick(effect.id)
            is ActivityEffect.OpenChat -> onOpenChat(effect.claimId, effect.threadId, effect.title)
        }
    }

    // Drive the snackbar from state; dismiss via intent once shown.
    val actionError = state.actionError
    if (!actionError.isNullOrBlank()) {
        LaunchedEffect(actionError) {
            snackbarHostState.showSnackbar(actionError)
            viewModel.onIntent(ActivityIntent.DismissActionError)
        }
    }

    // Proximity couldn't confirm the handoff: inform the user; the row keeps a manual-confirm button.
    val fallback = state.handoffFallback
    if (fallback != null) {
        val message = when (fallback.reason) {
            HandoffFailureReason.NotNearby -> stringResource(Res.string.proximity_not_nearby)
            HandoffFailureReason.PermissionOff -> stringResource(Res.string.proximity_permission_off)
            HandoffFailureReason.Error -> stringResource(Res.string.proximity_error)
            HandoffFailureReason.Unavailable -> stringResource(Res.string.proximity_unavailable)
        }
        LaunchedEffect(fallback) { snackbarHostState.showSnackbar(message) }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            NearAidTopBar(title = stringResource(Res.string.activity_title))

            NearAidSegmentedTabs(
                options = listOf(
                    stringResource(Res.string.tab_helping),
                    stringResource(Res.string.tab_my_posts),
                ),
                selectedIndex = state.selectedTab,
                onSelect = { viewModel.onIntent(ActivityIntent.SelectTab(it)) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            when (state.selectedTab) {
                0 -> HelpingTab(
                    state = state,
                    onOpenChat = { claimId, threadId, title ->
                        viewModel.onIntent(ActivityIntent.ClaimClicked(claimId, threadId, title))
                    },
                    onMarkDelivered = { viewModel.onIntent(ActivityIntent.MarkDelivered(it)) },
                    onMarkDeliveredManually = { viewModel.onIntent(ActivityIntent.MarkDeliveredManually(it)) },
                    onConfirmReceipt = { viewModel.onIntent(ActivityIntent.ConfirmReceipt(it)) },
                    onStartReceiver = { viewModel.onIntent(ActivityIntent.StartReceiverProximity(it)) },
                    onStopReceiver = { viewModel.onIntent(ActivityIntent.StopReceiverProximity) },
                    onRefresh = { viewModel.onIntent(ActivityIntent.Refresh) },
                )
                1 -> MyPostsTab(
                    state = state,
                    onListingClick = { viewModel.onIntent(ActivityIntent.ListingClicked(it)) },
                    onMarkDelivered = { viewModel.onIntent(ActivityIntent.OwnerMarkDelivered(it)) },
                    onMarkDeliveredManually = { viewModel.onIntent(ActivityIntent.OwnerMarkDeliveredManually(it)) },
                    onConfirmReceipt = { viewModel.onIntent(ActivityIntent.OwnerConfirmReceipt(it)) },
                    onStartReceiver = { viewModel.onIntent(ActivityIntent.StartReceiverProximity(it)) },
                    onStopReceiver = { viewModel.onIntent(ActivityIntent.StopReceiverProximity) },
                    onRefresh = { viewModel.onIntent(ActivityIntent.Refresh) },
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        ) { data ->
            Snackbar(snackbarData = data)
        }
    }
}

@Composable
private fun HelpingTab(
    state: ActivityState,
    onOpenChat: (claimId: String, threadId: String, title: String) -> Unit,
    onMarkDelivered: (claimId: String) -> Unit,
    onMarkDeliveredManually: (claimId: String) -> Unit,
    onConfirmReceipt: (claimId: String) -> Unit,
    onStartReceiver: (claimId: String) -> Unit,
    onStopReceiver: () -> Unit,
    onRefresh: () -> Unit,
) {
    when {
        state.claimsLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    color = NearAidTheme.colors.teal,
                    modifier = Modifier.statusSemantics(stringResource(Res.string.status_loading)),
                )
            }
        }
        state.claimsError != null -> {
            EmptyState(
                icon = Icons.Filled.Inbox,
                title = stringResource(Res.string.empty_couldnt_load_title),
                message = state.claimsError,
                actionLabel = stringResource(Res.string.action_retry),
                onAction = onRefresh,
                modifier = Modifier.fillMaxSize(),
            )
        }
        state.claims.isEmpty() -> {
            EmptyState(
                icon = Icons.Filled.Inbox,
                title = stringResource(Res.string.empty_no_activity_title),
                message = stringResource(Res.string.empty_no_activity_message),
                modifier = Modifier.fillMaxSize(),
            )
        }
        else -> {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(state.claims, key = { it.id }) { claim ->
                    ClaimRow(
                        claim = claim,
                        actionLoading = state.actionLoading,
                        proximityChecking = state.proximityClaimId == claim.id,
                        showManualFallback = state.handoffFallback?.claimId == claim.id,
                        receiverListening = state.receiverListeningClaimId == claim.id,
                        onOpenChat = onOpenChat,
                        onMarkDelivered = onMarkDelivered,
                        onMarkDeliveredManually = onMarkDeliveredManually,
                        onConfirmReceipt = onConfirmReceipt,
                        onStartReceiver = onStartReceiver,
                        onStopReceiver = onStopReceiver,
                    )
                }
            }
        }
    }
}

@Composable
private fun ClaimRow(
    claim: Claim,
    actionLoading: Boolean,
    proximityChecking: Boolean,
    showManualFallback: Boolean,
    receiverListening: Boolean,
    onOpenChat: (claimId: String, threadId: String, title: String) -> Unit,
    onMarkDelivered: (claimId: String) -> Unit,
    onMarkDeliveredManually: (claimId: String) -> Unit,
    onConfirmReceipt: (claimId: String) -> Unit,
    onStartReceiver: (claimId: String) -> Unit,
    onStopReceiver: () -> Unit,
) {
    val mappedStatus = claim.status.toListingStatus()
    // On a REQUEST the claimant is the fulfilling party (marks delivered); on an OFFER the claimant
    // is the receiving party (confirms receipt). Backend enforces this — calling the wrong action 403s.
    val isFulfiller = claim.listingType == ListingType.REQUEST

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(NearAidTheme.colors.surface)
            .border(1.dp, NearAidTheme.colors.line, MaterialTheme.shapes.large)
            .accessibleClickable(onClickLabel = stringResource(Res.string.action_open_chat)) { onOpenChat(claim.id, claim.chatThreadId ?: "", "") }
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CategoryIconBox(categoryKey = null)

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // The item name is the headline; the status label sits below it (StatusPill also
                // shows state). Fall back to the status label when the title is missing.
                Text(
                    text = claim.listingTitle ?: claimTitle(claim.status),
                    style = MaterialTheme.typography.titleSmall,
                    color = NearAidTheme.colors.ink,
                )
                if (claim.listingTitle != null) {
                    Text(
                        text = claimTitle(claim.status),
                        style = MaterialTheme.typography.bodySmall,
                        color = NearAidTheme.colors.ink3,
                    )
                }
                claim.claimedAt?.let { claimedAt ->
                    Text(
                        text = stringResource(Res.string.claim_since, TimeFormat.dateTime(claimedAt)),
                        style = MaterialTheme.typography.bodySmall,
                        color = NearAidTheme.colors.ink3,
                    )
                }
            }

            StatusPill(status = mappedStatus)

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = NearAidTheme.colors.ink3,
                modifier = Modifier.size(20.dp),
            )
        }

        when (claim.status) {
            ClaimStatus.ACTIVE -> if (isFulfiller) {
                // Fulfilling party (helper on a request). The proximity-gated deliver runs the BLE
                // handoff and degrades gracefully so the handoff is never blocked.
                DeliverHandoffButton(
                    claimId = claim.id,
                    actionLoading = actionLoading,
                    proximityChecking = proximityChecking,
                    showManualFallback = showManualFallback,
                    onDeliver = onMarkDelivered,
                    onDeliverManually = onMarkDeliveredManually,
                )
            } else {
                // Receiving party (recipient on an offer). Can't deliver, but can advertise over BLE
                // so the giver's "Mark delivered" proximity check confirms you're together.
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AwaitingChip(text = stringResource(Res.string.claim_awaiting_delivery))
                    ReadyToReceiveButton(
                        claimId = claim.id,
                        listening = receiverListening,
                        onStart = onStartReceiver,
                        onStop = onStopReceiver,
                    )
                }
            }
            ClaimStatus.DELIVERED -> if (isFulfiller) {
                // Fulfilling party already delivered; waiting on the recipient's receipt confirmation.
                // No deliver button here — tapping it again would 409 on the backend.
                AwaitingChip(text = stringResource(Res.string.claim_awaiting_confirmation))
            } else {
                // Receiving party: the giver marked delivered, so confirm receipt → COMPLETED.
                NearAidButton(
                    text = stringResource(Res.string.action_confirm_receipt),
                    onClick = { onConfirmReceipt(claim.id) },
                    enabled = !actionLoading,
                    loading = actionLoading,
                    variant = NearAidButtonVariant.Teal,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            ClaimStatus.COMPLETED -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(NearAidTheme.colors.tealTint)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.claim_completed),
                        style = MaterialTheme.typography.labelMedium,
                        color = NearAidTheme.colors.teal,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            else -> Unit
        }
    }
}

@Composable
private fun MyPostsTab(
    state: ActivityState,
    onListingClick: (id: String) -> Unit,
    onMarkDelivered: (claimId: String) -> Unit,
    onMarkDeliveredManually: (claimId: String) -> Unit,
    onConfirmReceipt: (claimId: String) -> Unit,
    onStartReceiver: (claimId: String) -> Unit,
    onStopReceiver: () -> Unit,
    onRefresh: () -> Unit,
) {
    when {
        state.listingsLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    color = NearAidTheme.colors.teal,
                    modifier = Modifier.statusSemantics(stringResource(Res.string.status_loading)),
                )
            }
        }
        state.listingsError != null -> {
            EmptyState(
                icon = Icons.Filled.Inbox,
                title = stringResource(Res.string.empty_couldnt_load_title),
                message = state.listingsError,
                actionLabel = stringResource(Res.string.action_retry),
                onAction = onRefresh,
                modifier = Modifier.fillMaxSize(),
            )
        }
        state.myListings.isEmpty() -> {
            EmptyState(
                icon = Icons.Filled.Inbox,
                title = stringResource(Res.string.empty_no_posts_title),
                message = stringResource(Res.string.empty_no_posts_message),
                modifier = Modifier.fillMaxSize(),
            )
        }
        else -> {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(state.myListings, key = { it.id }) { card ->
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ListingCardView(
                            card = card,
                            onClick = { onListingClick(card.id) },
                        )
                        // Author's side of the two-step handoff. The giver marks an offer delivered;
                        // the seeker confirms receipt once a request has been delivered. Both need
                        // the active claim id (owner-only field), so guard on it.
                        val claimId = card.activeClaimId
                        if (claimId != null) {
                            when {
                                card.type == ListingType.OFFER && card.status == ListingStatus.CLAIMED ->
                                    // Giver's side of the handoff: same BLE proximity check as the helper.
                                    DeliverHandoffButton(
                                        claimId = claimId,
                                        actionLoading = state.actionLoading,
                                        proximityChecking = state.proximityClaimId == claimId,
                                        showManualFallback = state.handoffFallback?.claimId == claimId,
                                        onDeliver = onMarkDelivered,
                                        onDeliverManually = onMarkDeliveredManually,
                                    )
                                card.type == ListingType.REQUEST && card.status == ListingStatus.DELIVERED ->
                                    NearAidButton(
                                        text = stringResource(Res.string.action_confirm_receipt),
                                        onClick = { onConfirmReceipt(claimId) },
                                        enabled = !state.actionLoading,
                                        loading = state.actionLoading,
                                        variant = NearAidButtonVariant.Teal,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                // Seeker awaiting the helper's delivery: advertise so their proximity
                                // check confirms you're together.
                                card.type == ListingType.REQUEST && card.status == ListingStatus.CLAIMED ->
                                    ReadyToReceiveButton(
                                        claimId = claimId,
                                        listening = state.receiverListeningClaimId == claimId,
                                        onStart = onStartReceiver,
                                        onStop = onStopReceiver,
                                    )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Proximity-gated "Mark delivered" button for the fulfilling party (request helper or offer giver).
 * Tapping first prompts for BLE permission (Android), then runs the proximity check; if it can't
 * confirm, a manual-confirm fallback appears so a legitimate handoff is never trapped.
 */
@Composable
private fun DeliverHandoffButton(
    claimId: String,
    actionLoading: Boolean,
    proximityChecking: Boolean,
    showManualFallback: Boolean,
    onDeliver: (claimId: String) -> Unit,
    onDeliverManually: (claimId: String) -> Unit,
) {
    val launchHandoff = rememberHandoffPermissionGate(onReady = { onDeliver(claimId) })
    val busy = actionLoading || proximityChecking
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NearAidButton(
            text = if (proximityChecking) {
                stringResource(Res.string.proximity_checking)
            } else {
                stringResource(Res.string.action_mark_delivered)
            },
            onClick = launchHandoff,
            enabled = !busy,
            loading = busy,
            variant = NearAidButtonVariant.Teal,
            modifier = Modifier.fillMaxWidth(),
        )
        if (showManualFallback) {
            NearAidButton(
                text = stringResource(Res.string.action_confirm_manually),
                onClick = { onDeliverManually(claimId) },
                enabled = !actionLoading,
                variant = NearAidButtonVariant.Ghost,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/**
 * The receiving party's side of the proximity handoff: advertise/scan over BLE while awaiting the
 * other device's "Mark delivered", so its proximity check can confirm you're together. Toggles on
 * tap (permission-gated) and auto-stops when the row leaves the screen.
 */
@Composable
private fun ReadyToReceiveButton(
    claimId: String,
    listening: Boolean,
    onStart: (claimId: String) -> Unit,
    onStop: () -> Unit,
) {
    val launch = rememberHandoffPermissionGate(onReady = { onStart(claimId) })
    // Stop advertising when this row leaves composition (tab switch / navigation away).
    DisposableEffect(claimId) { onDispose { onStop() } }
    if (listening) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(NearAidTheme.colors.tealTint)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                CircularProgressIndicator(
                    color = NearAidTheme.colors.teal,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = stringResource(Res.string.proximity_listening),
                    style = MaterialTheme.typography.labelMedium,
                    color = NearAidTheme.colors.teal,
                    fontWeight = FontWeight.Bold,
                )
            }
            NearAidButton(
                text = stringResource(Res.string.action_stop),
                onClick = onStop,
                variant = NearAidButtonVariant.Ghost,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    } else {
        NearAidButton(
            text = stringResource(Res.string.action_ready_to_receive),
            onClick = launch,
            variant = NearAidButtonVariant.Teal,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** Neutral "waiting on the other party" pill shown when the claimant has no action to take yet. */
@Composable
private fun AwaitingChip(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(NearAidTheme.colors.marigoldTint)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = NearAidTheme.colors.marigoldDeep,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun claimTitle(status: ClaimStatus): String = when (status) {
    ClaimStatus.ACTIVE -> stringResource(Res.string.claim_status_active)
    ClaimStatus.DELIVERED -> stringResource(Res.string.claim_status_delivered)
    ClaimStatus.COMPLETED -> stringResource(Res.string.claim_status_completed)
    ClaimStatus.WITHDRAWN -> stringResource(Res.string.claim_status_withdrawn)
    ClaimStatus.CANCELLED -> stringResource(Res.string.claim_status_cancelled)
}

private fun ClaimStatus.toListingStatus(): ListingStatus = when (this) {
    ClaimStatus.ACTIVE -> ListingStatus.CLAIMED
    ClaimStatus.DELIVERED -> ListingStatus.DELIVERED
    ClaimStatus.COMPLETED -> ListingStatus.COMPLETED
    ClaimStatus.WITHDRAWN -> ListingStatus.CANCELLED
    ClaimStatus.CANCELLED -> ListingStatus.CANCELLED
}
