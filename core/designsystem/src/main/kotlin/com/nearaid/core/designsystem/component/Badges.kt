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
import com.nearaid.core.designsystem.theme.Ink2
import com.nearaid.core.designsystem.theme.Marigold
import com.nearaid.core.designsystem.theme.Teal
import com.nearaid.core.model.ListingStatus
import com.nearaid.core.model.Urgency

@Composable
fun VerifiedBadge(modifier: Modifier = Modifier, tint: Color = Teal, size: Int = 14) {
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
        Icon(Icons.Filled.Star, contentDescription = null, tint = Marigold, modifier = Modifier.size(13.dp))
        Text(
            text = ratingLabel ?: trimDouble(score),
            style = MaterialTheme.typography.labelSmall,
            color = Ink2,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}

private fun trimDouble(value: Double): String =
    if (value % 1.0 == 0.0) value.toInt().toString() else String.format("%.1f", value)

@Composable
fun UrgencyTag(urgency: Urgency, modifier: Modifier = Modifier) {
    val (container, content, label) = when (urgency) {
        Urgency.LOW -> Triple(com.nearaid.core.designsystem.theme.UrgencyColors.LowContainer, com.nearaid.core.designsystem.theme.UrgencyColors.LowContent, "Low")
        Urgency.MEDIUM -> Triple(com.nearaid.core.designsystem.theme.UrgencyColors.MediumContainer, com.nearaid.core.designsystem.theme.UrgencyColors.MediumContent, "Medium")
        Urgency.HIGH -> Triple(com.nearaid.core.designsystem.theme.UrgencyColors.HighContainer, com.nearaid.core.designsystem.theme.UrgencyColors.HighContent, "High")
        Urgency.CRITICAL -> Triple(com.nearaid.core.designsystem.theme.UrgencyColors.CriticalContainer, com.nearaid.core.designsystem.theme.UrgencyColors.CriticalContent, "Critical")
    }
    TagChip(label = label, container = container, content = content, modifier = modifier)
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
        ListingStatus.OPEN -> Triple(com.nearaid.core.designsystem.theme.TealTint, Teal, "Open")
        ListingStatus.CLAIMED -> Triple(com.nearaid.core.designsystem.theme.MarigoldTint, com.nearaid.core.designsystem.theme.MarigoldDeep, "Claimed")
        ListingStatus.DELIVERED -> Triple(com.nearaid.core.designsystem.theme.MarigoldTint, com.nearaid.core.designsystem.theme.MarigoldDeep, "Delivered")
        ListingStatus.COMPLETED -> Triple(com.nearaid.core.designsystem.theme.TealTint, Teal, "Completed")
        ListingStatus.CANCELLED -> Triple(com.nearaid.core.designsystem.theme.Line2, Ink2, "Cancelled")
        ListingStatus.EXPIRED -> Triple(com.nearaid.core.designsystem.theme.Line2, Ink2, "Expired")
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
