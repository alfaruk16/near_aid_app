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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
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
import com.nearaid.core.designsystem.theme.NearAidTheme
import com.nearaid.core.model.Claim
import com.nearaid.core.model.ClaimStatus
import com.nearaid.core.model.ListingStatus
import com.nearaid.feature.activity.R

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

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            NearAidTopBar(title = stringResource(R.string.activity_title))

            NearAidSegmentedTabs(
                options = listOf(
                    stringResource(R.string.tab_helping),
                    stringResource(R.string.tab_my_posts),
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
                    onRefresh = { viewModel.onIntent(ActivityIntent.Refresh) },
                )
                1 -> MyPostsTab(
                    state = state,
                    onListingClick = { viewModel.onIntent(ActivityIntent.ListingClicked(it)) },
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
    onRefresh: () -> Unit,
) {
    when {
        state.claimsLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    color = NearAidTheme.colors.teal,
                    modifier = Modifier.statusSemantics(stringResource(R.string.status_loading)),
                )
            }
        }
        state.claimsError != null -> {
            EmptyState(
                icon = Icons.Filled.Inbox,
                title = stringResource(R.string.empty_couldnt_load_title),
                message = state.claimsError,
                actionLabel = stringResource(R.string.action_retry),
                onAction = onRefresh,
                modifier = Modifier.fillMaxSize(),
            )
        }
        state.claims.isEmpty() -> {
            EmptyState(
                icon = Icons.Filled.Inbox,
                title = stringResource(R.string.empty_no_activity_title),
                message = stringResource(R.string.empty_no_activity_message),
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
                        onOpenChat = onOpenChat,
                        onMarkDelivered = onMarkDelivered,
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
    onOpenChat: (claimId: String, threadId: String, title: String) -> Unit,
    onMarkDelivered: (claimId: String) -> Unit,
) {
    val mappedStatus = claim.status.toListingStatus()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(NearAidTheme.colors.surface)
            .border(1.dp, NearAidTheme.colors.line, MaterialTheme.shapes.large)
            .accessibleClickable(onClickLabel = stringResource(R.string.action_open_chat)) { onOpenChat(claim.id, claim.chatThreadId ?: "", "") }
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CategoryIconBox(categoryKey = null)

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = claimTitle(claim.status),
                    style = MaterialTheme.typography.titleSmall,
                    color = NearAidTheme.colors.ink,
                )
                claim.claimedAt?.let { claimedAt ->
                    Text(
                        text = stringResource(R.string.claim_since, claimedAt),
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
            ClaimStatus.ACTIVE -> {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NearAidButton(
                        text = stringResource(R.string.action_mark_delivered),
                        onClick = { onMarkDelivered(claim.id) },
                        enabled = !actionLoading,
                        loading = actionLoading,
                        variant = NearAidButtonVariant.Teal,
                        modifier = Modifier.weight(1f),
                    )
                }
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
                        text = stringResource(R.string.claim_completed),
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
    onRefresh: () -> Unit,
) {
    when {
        state.listingsLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    color = NearAidTheme.colors.teal,
                    modifier = Modifier.statusSemantics(stringResource(R.string.status_loading)),
                )
            }
        }
        state.listingsError != null -> {
            EmptyState(
                icon = Icons.Filled.Inbox,
                title = stringResource(R.string.empty_couldnt_load_title),
                message = state.listingsError,
                actionLabel = stringResource(R.string.action_retry),
                onAction = onRefresh,
                modifier = Modifier.fillMaxSize(),
            )
        }
        state.myListings.isEmpty() -> {
            EmptyState(
                icon = Icons.Filled.Inbox,
                title = stringResource(R.string.empty_no_posts_title),
                message = stringResource(R.string.empty_no_posts_message),
                modifier = Modifier.fillMaxSize(),
            )
        }
        else -> {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(state.myListings, key = { it.id }) { card ->
                    ListingCardView(
                        card = card,
                        onClick = { onListingClick(card.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun claimTitle(status: ClaimStatus): String = when (status) {
    ClaimStatus.ACTIVE -> stringResource(R.string.claim_status_active)
    ClaimStatus.COMPLETED -> stringResource(R.string.claim_status_completed)
    ClaimStatus.WITHDRAWN -> stringResource(R.string.claim_status_withdrawn)
    ClaimStatus.CANCELLED -> stringResource(R.string.claim_status_cancelled)
}

private fun ClaimStatus.toListingStatus(): ListingStatus = when (this) {
    ClaimStatus.ACTIVE -> ListingStatus.CLAIMED
    ClaimStatus.COMPLETED -> ListingStatus.COMPLETED
    ClaimStatus.WITHDRAWN -> ListingStatus.CANCELLED
    ClaimStatus.CANCELLED -> ListingStatus.CANCELLED
}
