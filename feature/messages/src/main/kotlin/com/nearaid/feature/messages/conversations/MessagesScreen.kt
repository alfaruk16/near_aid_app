package com.nearaid.feature.messages.conversations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nearaid.core.common.util.TimeFormat
import com.nearaid.core.designsystem.component.Avatar
import com.nearaid.core.designsystem.component.CollectEffect
import com.nearaid.core.designsystem.component.EmptyState
import com.nearaid.core.designsystem.component.NearAidTopBar
import com.nearaid.core.designsystem.component.VerifiedBadge
import com.nearaid.core.designsystem.component.accessibleClickable
import com.nearaid.core.designsystem.component.statusSemantics
import com.nearaid.core.designsystem.theme.NearAidTheme
import com.nearaid.core.model.Conversation
import com.nearaid.feature.messages.R

@Composable
fun MessagesScreen(
    onOpenChat: (claimId: String, threadId: String, title: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConversationsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is ConversationsEffect.OpenChat -> onOpenChat(effect.claimId, effect.threadId, effect.title)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        NearAidTopBar(title = stringResource(R.string.messages_title))

        when {
            state.loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.statusSemantics(stringResource(R.string.status_loading)),
                        color = NearAidTheme.colors.marigold,
                    )
                }
            }

            state.conversations.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        icon = Icons.AutoMirrored.Filled.Chat,
                        title = stringResource(R.string.messages_empty_title),
                        message = stringResource(R.string.messages_empty_message),
                        actionLabel = stringResource(R.string.messages_empty_action),
                        onAction = { viewModel.onIntent(ConversationsIntent.Load) },
                    )
                }
            }

            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(items = state.conversations, key = { it.threadId }) { conversation ->
                        ConversationRow(
                            conversation = conversation,
                            onClick = {
                                viewModel.onIntent(
                                    ConversationsIntent.ConversationClicked(
                                        claimId = conversation.claimId,
                                        threadId = conversation.threadId,
                                        title = conversation.listingTitle,
                                    )
                                )
                            },
                        )
                        HorizontalDivider(color = NearAidTheme.colors.line, thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

// ─── Conversation Row ─────────────────────────────────────────────────────────

@Composable
private fun ConversationRow(
    conversation: Conversation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isUnread = conversation.unreadCount > 0
    val rowBg = if (isUnread) NearAidTheme.colors.marigoldSoft else NearAidTheme.colors.surface
    val openConversationLabel = stringResource(R.string.cd_open_conversation)
    val readStateDescription =
        if (isUnread) stringResource(R.string.state_unread) else stringResource(R.string.state_read)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(rowBg)
            .accessibleClickable(onClickLabel = openConversationLabel, onClick = onClick)
            .semantics { stateDescription = readStateDescription }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Avatar
        Avatar(
            name = conversation.counterpart.displayName,
            photoUrl = conversation.counterpart.photoUrl,
            size = 48,
        )

        // Text block
        Column(modifier = Modifier.weight(1f)) {
            // Name row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = conversation.counterpart.displayName ?: "Unknown",
                    style = MaterialTheme.typography.titleSmall,
                    color = NearAidTheme.colors.ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (conversation.counterpart.isIdVerified) {
                    VerifiedBadge(size = 14)
                }
            }

            // Context line: listing title · role
            Text(
                text = "${conversation.listingTitle} · ${conversation.role.name.lowercase().replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.bodySmall,
                color = NearAidTheme.colors.teal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            // Last message
            val lastMessage = conversation.lastMessageBody
            if (!lastMessage.isNullOrBlank()) {
                Text(
                    text = lastMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUnread) NearAidTheme.colors.ink else NearAidTheme.colors.ink2,
                    fontWeight = if (isUnread) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        // Right column: time + unread badge
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = TimeFormat.relativeFromNow(conversation.lastMessageAt),
                style = MaterialTheme.typography.labelSmall,
                color = NearAidTheme.colors.ink3,
            )
            if (isUnread) {
                UnreadBadge(count = conversation.unreadCount)
            }
        }
    }
}

// ─── Unread Badge ─────────────────────────────────────────────────────────────

@Composable
private fun UnreadBadge(count: Int, modifier: Modifier = Modifier) {
    val label = if (count > 99) "99+" else count.toString()
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(NearAidTheme.colors.marigold)
            .padding(horizontal = if (count > 9) 6.dp else 0.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = if (count <= 9) Modifier
                .size(20.dp)
                .clip(CircleShape)
            else Modifier
                .padding(vertical = 2.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                color = NearAidTheme.colors.onMarigold,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                lineHeight = 13.sp,
            )
        }
    }
}
