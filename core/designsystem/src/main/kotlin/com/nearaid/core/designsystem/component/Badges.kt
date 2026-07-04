package com.nearaid.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nearaid.core.designsystem.theme.NearAidTheme
import com.nearaid.core.model.ListingStatus
import com.nearaid.core.model.Urgency

@Composable
fun VerifiedBadge(modifier: Modifier = Modifier, tint: Color = NearAidTheme.colors.teal, size: Int = 14) {
    Icon(
        imageVector = Icons.Filled.Verified,
        contentDescription = "Verified",
        tint = tint,
        modifier = modifier.size(size.dp),
    )
}

@Composable
fun TrustScore(score: Double?, ratingLabel: String? = null, modifier: Modifier = Modifier) {
    if (score == null) return
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Icon(Icons.Filled.Star, contentDescription = null, tint = NearAidTheme.colors.marigold, modifier = Modifier.size(13.dp))
        Text(
            text = ratingLabel ?: trimDouble(score),
            style = MaterialTheme.typography.labelSmall,
            color = NearAidTheme.colors.ink2,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}

private fun trimDouble(value: Double): String =
    if (value % 1.0 == 0.0) value.toInt().toString() else String.format("%.1f", value)

@Composable
fun UrgencyTag(urgency: Urgency, modifier: Modifier = Modifier) {
    val accent = com.nearaid.core.designsystem.theme.urgencyAccentFor(urgency)
    val label = when (urgency) {
        Urgency.LOW -> "Low"
        Urgency.MEDIUM -> "Medium"
        Urgency.HIGH -> "High"
        Urgency.CRITICAL -> "Critical"
    }
    TagChip(label = label, container = accent.container, content = accent.content, modifier = modifier)
}

@Composable
fun TagChip(label: String, container: Color, content: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(container)
            .padding(horizontal = 9.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label.uppercase(),
            color = content,
            fontWeight = FontWeight.Bold,
            fontSize = 10.5.sp,
            letterSpacing = 0.4.sp,
        )
    }
}

@Composable
fun StatusPill(status: ListingStatus, modifier: Modifier = Modifier) {
    val (container, content, label) = when (status) {
        ListingStatus.OPEN -> Triple(NearAidTheme.colors.tealTint, NearAidTheme.colors.teal, "Open")
        ListingStatus.CLAIMED -> Triple(NearAidTheme.colors.marigoldTint, NearAidTheme.colors.marigoldDeep, "Claimed")
        ListingStatus.DELIVERED -> Triple(NearAidTheme.colors.marigoldTint, NearAidTheme.colors.marigoldDeep, "Delivered")
        ListingStatus.COMPLETED -> Triple(NearAidTheme.colors.tealTint, NearAidTheme.colors.teal, "Completed")
        ListingStatus.CANCELLED -> Triple(NearAidTheme.colors.line2, NearAidTheme.colors.ink2, "Cancelled")
        ListingStatus.EXPIRED -> Triple(NearAidTheme.colors.line2, NearAidTheme.colors.ink2, "Expired")
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(container)
            .padding(horizontal = 11.dp, vertical = 5.dp),
    ) {
        Text("● $label", color = content, style = MaterialTheme.typography.labelSmall)
    }
}
