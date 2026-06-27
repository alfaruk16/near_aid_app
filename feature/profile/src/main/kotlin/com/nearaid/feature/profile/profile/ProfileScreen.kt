package com.nearaid.feature.profile.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nearaid.core.designsystem.component.Avatar
import com.nearaid.core.designsystem.component.CollectEffect
import com.nearaid.core.designsystem.component.VerifiedBadge
import com.nearaid.core.designsystem.theme.Ink
import com.nearaid.core.designsystem.theme.Ink2
import com.nearaid.core.designsystem.theme.Ink3
import com.nearaid.core.designsystem.theme.Line
import com.nearaid.core.designsystem.theme.Marigold
import com.nearaid.core.designsystem.theme.MarigoldDeep
import com.nearaid.core.designsystem.theme.Teal

@Composable
fun ProfileScreen(
    onOpenVerification: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenPublicProfile: (userId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            ProfileEffect.OpenVerification -> onOpenVerification()
            ProfileEffect.OpenSettings -> onOpenSettings()
            is ProfileEffect.OpenPublicProfile -> onOpenPublicProfile(effect.userId)
        }
    }

    if (state.loading && state.me == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Marigold)
        }
        return
    }

    val me = state.me ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        // Header with Marigold gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Marigold, MarigoldDeep),
                    )
                )
                .padding(horizontal = 24.dp, vertical = 32.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Avatar(
                    name = me.displayName,
                    photoUrl = me.photoUrl,
                    size = 88,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = me.displayName ?: "—",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    if (me.isIdVerified) {
                        VerifiedBadge(tint = Color.White, size = 20)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                val subtitleParts = buildList {
                    if (me.isIdVerified) add("Verified")
                    me.defaultArea?.let { add(it) }
                    add("Trust score ${String.format("%.1f", me.trustScore)}")
                }
                Text(
                    text = subtitleParts.joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f),
                )
            }
        }

        // Stats row
        ProfileStatsRow(
            trustScore = me.trustScore,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
        )

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Line),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Menu items
        ProfileMenuItem(
            icon = Icons.Filled.Badge,
            label = "Verification",
            trailingLabel = if (me.isIdVerified) "Verified" else null,
            trailingColor = Teal,
            onClick = { viewModel.onIntent(ProfileIntent.VerificationClicked) },
        )
        ProfileMenuItem(
            icon = Icons.Filled.Star,
            label = "My ratings",
            onClick = { viewModel.onIntent(ProfileIntent.RatingsClicked) },
        )
        ProfileMenuItem(
            icon = Icons.Filled.Settings,
            label = "Settings & language",
            onClick = { viewModel.onIntent(ProfileIntent.SettingsClicked) },
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ProfileStatsRow(
    trustScore: Double,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        ProfileStatCell(
            value = String.format("%.1f", trustScore),
            label = "Trust",
        )
        ProfileStatCell(
            value = "—",
            label = "Helped",
        )
        ProfileStatCell(
            value = "—",
            label = "Rating",
        )
    }
}

@Composable
private fun ProfileStatCell(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = Ink,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Ink3,
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    trailingLabel: String? = null,
    trailingColor: Color = Ink2,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Line),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Ink2,
                modifier = Modifier.size(20.dp),
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Ink,
            modifier = Modifier.weight(1f),
        )
        if (trailingLabel != null) {
            Text(
                text = trailingLabel,
                style = MaterialTheme.typography.labelMedium,
                color = trailingColor,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = Ink3,
            modifier = Modifier.size(20.dp),
        )
    }
}
