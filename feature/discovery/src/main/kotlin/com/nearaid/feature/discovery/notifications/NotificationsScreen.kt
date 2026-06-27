package com.nearaid.feature.discovery.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nearaid.core.common.util.TimeFormat
import com.nearaid.core.designsystem.component.CollectEffect
import com.nearaid.core.designsystem.component.EmptyState
import com.nearaid.core.designsystem.component.NearAidTopBar
import com.nearaid.core.designsystem.theme.Ink
import com.nearaid.core.designsystem.theme.Ink2
import com.nearaid.core.designsystem.theme.Ink3
import com.nearaid.core.designsystem.theme.Marigold
import com.nearaid.core.designsystem.theme.MarigoldSoft
import com.nearaid.core.designsystem.theme.Surface
import com.nearaid.core.designsystem.theme.Teal
import com.nearaid.core.designsystem.theme.TealSoft
import com.nearaid.core.model.NotificationItem

@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onIntent(NotificationsIntent.Load)
    }

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            NotificationsEffect.NavigateBack -> onBack()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        NearAidTopBar(title = "Notifications", onBack = onBack)

        when {
            state.loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Marigold)
                }
            }

            state.notifications.isEmpty() && !state.loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    EmptyState(
                        icon = Icons.Filled.NotificationsNone,
                        title = "No notifications yet",
                        message = "You'll be notified when someone responds to your listings or claims.",
                    )
                }
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.notifications, key = { it.id }) { item ->
                        NotificationRow(item = item)
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(item: NotificationItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(if (item.isRead) Surface else MarigoldSoft)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // Leading icon box
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(TealSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = null,
                tint = Teal,
                modifier = Modifier.size(20.dp),
            )
        }

        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (item.isRead) FontWeight.Normal else FontWeight.Bold,
                    color = Ink,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = TimeFormat.relativeFromNow(item.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink3,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
            Text(
                text = item.body,
                style = MaterialTheme.typography.bodySmall,
                color = Ink2,
            )
        }
    }
}
