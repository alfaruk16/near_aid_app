package com.nearaid.feature.messages.conversations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nearaid.core.common.util.TimeFormat
import com.nearaid.core.designsystem.component.Avatar
import com.nearaid.core.designsystem.component.CollectEffect
import com.nearaid.core.designsystem.component.EmptyState
import com.nearaid.core.designsystem.component.NearAidTopBar
import com.nearaid.core.designsystem.component.VerifiedBadge
import com.nearaid.core.designsystem.theme.Ink
import com.nearaid.core.designsystem.theme.Ink2
import com.nearaid.core.designsystem.theme.Ink3
import com.nearaid.core.designsystem.theme.Line
import com.nearaid.core.designsystem.theme.Marigold
import com.nearaid.core.designsystem.theme.MarigoldSoft
import com.nearaid.core.designsystem.theme.OnMarigold
import com.nearaid.core.designsystem.theme.Surface
import com.nearaid.core.designsystem.theme.Teal
import com.nearaid.core.model.Conversation

@Composable
fun MessagesScreen(
    onOpenChat: (claimId: String, threadId: String, title: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConversationsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is ConversationsEffect.OpenChat -> onOpenChat(effect.claimId, effect.threadId, effect.title)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        NearAidTopBar(title = "Messages")

        when {
            state.loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Marigold)
                }
            }

            state.conversations.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        icon = Icons.AutoMirrored.Filled.Chat,
                        title = "No messages yet",
                        message = "When you claim or share something, your coordination chats will appear here.",
                        actionLabel = "Refresh",
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
                        HorizontalDivider(color = Line, thickness = 0.5.dp)
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
    val rowBg = if (isUnread) MarigoldSoft else Surface

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(rowBg)
            .clickable(onClick = onClick)
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
                    color = Ink,
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
                color = Teal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            // Last message
            val lastMessage = conversation.lastMessageBody
            if (!lastMessage.isNullOrBlank()) {
                Text(
                    text = lastMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUnread) Ink else Ink2,
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
                color = Ink3,
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
            .background(Marigold)
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
                color = OnMarigold,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                lineHeight = 13.sp,
            )
        }
    }
}
