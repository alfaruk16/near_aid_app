package com.nearaid.feature.messages.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nearaid.core.common.util.TimeFormat
import com.nearaid.core.designsystem.component.Avatar
import com.nearaid.core.designsystem.component.CollectEffect
import com.nearaid.core.designsystem.component.statusSemantics
import com.nearaid.core.designsystem.theme.NearAidTheme
import com.nearaid.core.model.ChatMessage
import com.nearaid.feature.messages.resources.Res
import com.nearaid.feature.messages.resources.*

@Composable
fun ChatScreen(
    claimId: String,
    threadId: String,
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(claimId, threadId) {
        viewModel.onIntent(ChatIntent.Start(claimId, threadId))
    }

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            ChatEffect.NavigateBack -> onBack()
        }
    }

    val listState = rememberLazyListState()

    // Auto-scroll to bottom when a new message arrives
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
    ) {
        // ── Chat Header ──────────────────────────────────────────────────────
        ChatHeader(
            title = title,
            onBack = { viewModel.onIntent(ChatIntent.BackClicked) },
        )

        // ── Safety Bar (FR-19) ───────────────────────────────────────────────
        SafetyBar(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))

        // ── Message List ─────────────────────────────────────────────────────
        Box(modifier = Modifier.weight(1f)) {
            when {
                state.loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier.statusSemantics(stringResource(Res.string.status_loading)),
                            color = NearAidTheme.colors.marigold,
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(items = state.messages, key = { it.id }) { message ->
                            val isOutgoing = message.senderId == state.myUserId
                            ChatBubble(
                                message = message,
                                isOutgoing = isOutgoing,
                            )
                        }
                    }
                }
            }
        }

        // ── Input Row ────────────────────────────────────────────────────────
        MessageInputRow(
            text = state.inputText,
            onTextChange = { viewModel.onIntent(ChatIntent.InputChanged(it)) },
            onSend = { viewModel.onIntent(ChatIntent.Send(claimId)) },
            sending = state.sending,
            modifier = Modifier
                .fillMaxWidth()
                .background(NearAidTheme.colors.surface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
        )
    }
}

// ─── Chat Header ─────────────────────────────────────────────────────────────

@Composable
private fun ChatHeader(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(NearAidTheme.colors.surface)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.cd_back),
                tint = NearAidTheme.colors.ink,
            )
        }
        Avatar(
            name = title,
            photoUrl = null,
            size = 36,
            modifier = Modifier.padding(end = 8.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = NearAidTheme.colors.ink,
                maxLines = 1,
            )
            Text(
                text = stringResource(Res.string.chat_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = NearAidTheme.colors.ink2,
            )
        }
    }
}

// ─── Safety Bar (FR-19) ──────────────────────────────────────────────────────

@Composable
private fun SafetyBar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NearAidTheme.colors.tealSoft)
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Text(
            text = stringResource(Res.string.chat_safety_bar),
            style = MaterialTheme.typography.bodySmall,
            color = NearAidTheme.colors.teal,
            fontWeight = FontWeight.Medium,
        )
    }
}

// ─── Chat Bubble ─────────────────────────────────────────────────────────────

@Composable
private fun ChatBubble(
    message: ChatMessage,
    isOutgoing: Boolean,
    modifier: Modifier = Modifier,
) {
    val bubbleBg = if (isOutgoing) NearAidTheme.colors.marigold else NearAidTheme.colors.surface
    val textColor = if (isOutgoing) NearAidTheme.colors.onMarigold else NearAidTheme.colors.ink
    val alignment = if (isOutgoing) Alignment.End else Alignment.Start
    val bubbleShape = if (isOutgoing) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    val spokenSender =
        if (isOutgoing) stringResource(Res.string.chat_sender_you) else stringResource(Res.string.chat_sender_received)
    val spokenMessage = buildString {
        append(spokenSender)
        message.body?.takeIf { it.isNotBlank() }?.let { append(": $it") }
        append(", ${TimeFormat.timeOfDay(message.createdAt)}")
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { contentDescription = spokenMessage },
        horizontalAlignment = alignment,
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(bubbleShape)
                .background(bubbleBg)
                .then(
                    if (!isOutgoing) Modifier.border(1.dp, NearAidTheme.colors.line, bubbleShape)
                    else Modifier
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Text(
                text = message.body ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = TimeFormat.timeOfDay(message.createdAt),
            style = MaterialTheme.typography.labelSmall,
            color = NearAidTheme.colors.ink2,
            fontSize = 10.sp,
        )
    }
}

// ─── Message Input Row ───────────────────────────────────────────────────────

@Composable
private fun MessageInputRow(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    sending: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            placeholder = {
                Text(
                    text = stringResource(Res.string.chat_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = NearAidTheme.colors.ink2,
                )
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(24.dp),
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NearAidTheme.colors.marigold,
                unfocusedBorderColor = NearAidTheme.colors.line,
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
        )

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (text.isBlank()) NearAidTheme.colors.line else NearAidTheme.colors.marigold),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(
                onClick = onSend,
                enabled = text.isNotBlank() && !sending,
            ) {
                if (sending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = NearAidTheme.colors.onMarigold,
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = stringResource(Res.string.cd_send),
                        tint = if (text.isBlank()) NearAidTheme.colors.ink2 else NearAidTheme.colors.onMarigold,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}
