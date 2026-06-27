package com.nearaid.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nearaid.core.designsystem.theme.Ink
import com.nearaid.core.designsystem.theme.Ink3
import com.nearaid.core.designsystem.theme.Line
import com.nearaid.core.designsystem.theme.Marigold
import com.nearaid.core.designsystem.theme.MarigoldDeep
import com.nearaid.core.designsystem.theme.Surface
import com.nearaid.core.designsystem.theme.Teal
import com.nearaid.core.designsystem.theme.TealSoft

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = Ink3,
        letterSpacing = 1.2.sp,
        modifier = modifier.padding(vertical = 6.dp),
    )
}

@Composable
fun NearAidChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leading: String? = null,
) {
    val bg = if (selected) Ink else Surface
    val fg = if (selected) Surface else com.nearaid.core.designsystem.theme.Ink2
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .border(1.dp, if (selected) Ink else Line, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 7.dp),
    ) {
        Text(
            text = if (leading != null) "$leading  $label" else label,
            color = fg,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun Avatar(
    name: String?,
    photoUrl: String?,
    modifier: Modifier = Modifier,
    size: Int = 24,
) {
    val shape = CircleShape
    if (!photoUrl.isNullOrBlank()) {
        AsyncImage(
            model = photoUrl,
            contentDescription = name,
            modifier = modifier.size(size.dp).clip(shape),
        )
    } else {
        Box(
            modifier = modifier
                .size(size.dp)
                .clip(shape)
                .background(Brush.linearGradient(listOf(Color(0xFFE8C9A0), Color(0xFFC99B6B)))),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = name?.trim()?.firstOrNull()?.uppercase() ?: "?",
                color = Surface,
                fontWeight = FontWeight.Bold,
                fontSize = (size * 0.42f).sp,
            )
        }
    }
}

@Composable
fun NearAidTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Ink)
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f),
        )
        actions()
    }
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier.size(88.dp).clip(RoundedCornerShape(28.dp)).background(TealSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = Teal, modifier = Modifier.size(40.dp))
        }
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = com.nearaid.core.designsystem.theme.Ink2,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        if (actionLabel != null && onAction != null) {
            NearAidButton(
                text = actionLabel,
                onClick = onAction,
                variant = NearAidButtonVariant.Ghost,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
fun NearAidSegmentedTabs(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(13.dp))
            .background(com.nearaid.core.designsystem.theme.Line2)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        options.forEachIndexed { index, label ->
            val active = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (active) Surface else Color.Transparent)
                    .clickable { onSelect(index) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (active) Ink else Ink3,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
fun TextChooserRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    highlighted: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.5.dp, if (highlighted) Teal else Line, RoundedCornerShape(18.dp))
            .background(if (highlighted) TealSoft else Surface)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = if (highlighted) Teal else Ink3, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}
