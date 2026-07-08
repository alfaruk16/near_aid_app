package com.nearaid.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.jetbrains.compose.resources.stringResource
import com.nearaid.core.designsystem.resources.Res
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nearaid.core.common.util.TimeFormat
import com.nearaid.core.designsystem.resources.*
import com.nearaid.core.designsystem.theme.NearAidTheme
import com.nearaid.core.model.ListingCard
import com.nearaid.core.model.ListingType

@Composable
fun ListingCardView(
    card: ListingCard,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(NearAidTheme.colors.surface)
            .border(1.dp, NearAidTheme.colors.line, MaterialTheme.shapes.large)
            .accessibleClickable(onClickLabel = stringResource(Res.string.action_open_listing), onClick = onClick)
            .padding(14.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CategoryIconBox(categoryKey = card.category?.key)
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = card.title,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    when {
                        card.type == ListingType.OFFER ->
                            TagChip(stringResource(Res.string.tag_offering), NearAidTheme.colors.tealTint, NearAidTheme.colors.teal)
                        card.urgency != null -> UrgencyTag(card.urgency!!)
                    }
                }
                CardMeta(card)
            }
        }
        HorizontalDivider(Modifier.padding(top = 12.dp, bottom = 11.dp), color = NearAidTheme.colors.line2)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Avatar(name = card.author.displayName, photoUrl = card.author.photoUrl, size = 24)
            Text(
                text = card.author.displayName ?: stringResource(Res.string.author_fallback_neighbour),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = NearAidTheme.colors.ink2,
            )
            if (card.author.isIdVerified) {
                VerifiedBadge()
            }
            TrustScore(
                score = card.author.trustScore,
                modifier = Modifier.weight(1f).padding(start = 4.dp),
            )
        }
    }
}

@Composable
private fun CardMeta(card: ListingCard) {
    Row(
        modifier = Modifier.padding(top = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        card.distanceKm?.let {
            Text(
                text = formatDistance(it),
                style = MaterialTheme.typography.bodySmall,
                color = NearAidTheme.colors.teal,
                fontWeight = FontWeight.Bold,
            )
            Text("·", color = NearAidTheme.colors.ink3, style = MaterialTheme.typography.bodySmall)
        }
        val timeText = if (card.type == ListingType.OFFER && card.availableUntil != null) {
            stringResource(Res.string.listing_available_until, TimeFormat.timeOfDay(card.availableUntil))
        } else {
            TimeFormat.relativeFromNow(card.createdAt)
        }
        Text(
            text = timeText,
            style = MaterialTheme.typography.bodySmall,
            color = if (card.type == ListingType.OFFER) NearAidTheme.colors.rust else NearAidTheme.colors.ink3,
            fontWeight = if (card.type == ListingType.OFFER) FontWeight.Bold else FontWeight.Normal,
        )
        card.category?.let {
            Text("· ${it.nameEn}", color = NearAidTheme.colors.ink3, style = MaterialTheme.typography.bodySmall)
        }
    }
}

fun formatDistance(km: Double): String =
    if (km < 1.0) "${(km * 1000).toInt()} m" else "${formatOneDecimal(km)} km"
