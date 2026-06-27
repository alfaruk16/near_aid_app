package com.nearaid.feature.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nearaid.core.designsystem.component.CategoryIconBox
import com.nearaid.core.designsystem.component.CollectEffect
import com.nearaid.core.designsystem.component.EmptyState
import com.nearaid.core.designsystem.component.ListingCardView
import com.nearaid.core.designsystem.component.NearAidButton
import com.nearaid.core.designsystem.component.NearAidButtonVariant
import com.nearaid.core.designsystem.component.NearAidSegmentedTabs
import com.nearaid.core.designsystem.component.NearAidTopBar
import com.nearaid.core.designsystem.component.StatusPill
import com.nearaid.core.designsystem.theme.Ink
import com.nearaid.core.designsystem.theme.Ink3
import com.nearaid.core.designsystem.theme.Line
import com.nearaid.core.designsystem.theme.Surface
import com.nearaid.core.designsystem.theme.Teal
import com.nearaid.core.designsystem.theme.TealTint
import com.nearaid.core.model.Claim
import com.nearaid.core.model.ClaimStatus
import com.nearaid.core.model.ListingStatus

@Composable
fun ActivityScreen(
    onListingClick: (id: String) -> Unit,
    onOpenChat: (claimId: String, threadId: String, title: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ActivityViewModel = hiltViewModel(),
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
            NearAidTopBar(title = "Activity")

            NearAidSegmentedTabs(
                options = listOf("Helping", "My posts"),
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
                CircularProgressIndicator(color = Teal)
            }
        }
        state.claimsError != null -> {
            EmptyState(
                icon = Icons.Filled.Inbox,
                title = "Couldn't load",
                message = state.claimsError,
                actionLabel = "Retry",
                onAction = onRefresh,
                modifier = Modifier.fillMaxSize(),
            )
        }
        state.claims.isEmpty() -> {
            EmptyState(
                icon = Icons.Filled.Inbox,
                title = "No activity yet",
                message = "When you help someone, your active claims will appear here.",
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
            .background(Surface)
            .border(1.dp, Line, MaterialTheme.shapes.large)
            .clickable { onOpenChat(claim.id, claim.chatThreadId ?: "", "") }
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
                    color = Ink,
                )
                claim.claimedAt?.let { claimedAt ->
                    Text(
                        text = "Since $claimedAt",
                        style = MaterialTheme.typography.bodySmall,
                        color = Ink3,
                    )
                }
            }

            StatusPill(status = mappedStatus)

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Open chat",
                tint = Ink3,
                modifier = Modifier.size(20.dp),
            )
        }

        when (claim.status) {
            ClaimStatus.ACTIVE -> {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NearAidButton(
                        text = "Mark delivered",
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
                        .background(TealTint)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = "Completed",
                        style = MaterialTheme.typography.labelMedium,
                        color = Teal,
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
                CircularProgressIndicator(color = Teal)
            }
        }
        state.listingsError != null -> {
            EmptyState(
                icon = Icons.Filled.Inbox,
                title = "Couldn't load",
                message = state.listingsError,
                actionLabel = "Retry",
                onAction = onRefresh,
                modifier = Modifier.fillMaxSize(),
            )
        }
        state.myListings.isEmpty() -> {
            EmptyState(
                icon = Icons.Filled.Inbox,
                title = "No posts yet",
                message = "Your requests and offers will appear here once you post them.",
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

private fun claimTitle(status: ClaimStatus): String = when (status) {
    ClaimStatus.ACTIVE -> "You're helping"
    ClaimStatus.COMPLETED -> "You helped"
    ClaimStatus.WITHDRAWN -> "Withdrawn"
    ClaimStatus.CANCELLED -> "Cancelled"
}

private fun ClaimStatus.toListingStatus(): ListingStatus = when (this) {
    ClaimStatus.ACTIVE -> ListingStatus.CLAIMED
    ClaimStatus.COMPLETED -> ListingStatus.COMPLETED
    ClaimStatus.WITHDRAWN -> ListingStatus.CANCELLED
    ClaimStatus.CANCELLED -> ListingStatus.CANCELLED
}
