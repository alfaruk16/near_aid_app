package com.nearaid.feature.profile.publicprofile

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nearaid.core.designsystem.component.Avatar
import com.nearaid.core.designsystem.component.CollectEffect
import com.nearaid.core.designsystem.component.NearAidTopBar
import com.nearaid.core.designsystem.component.SectionLabel
import com.nearaid.core.designsystem.component.VerifiedBadge
import com.nearaid.core.designsystem.theme.Ink
import com.nearaid.core.designsystem.theme.Ink2
import com.nearaid.core.designsystem.theme.Ink3
import com.nearaid.core.designsystem.theme.Line
import com.nearaid.core.designsystem.theme.Marigold
import com.nearaid.core.designsystem.theme.Surface
import com.nearaid.core.designsystem.theme.Teal
import com.nearaid.core.model.Rating

@Composable
fun PublicProfileScreen(
    userId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PublicProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            PublicProfileEffect.NavigateBack -> onBack()
        }
    }

    LaunchedEffect(userId) {
        viewModel.onIntent(PublicProfileIntent.Load(userId))
    }

    Column(modifier = modifier.fillMaxSize()) {
        NearAidTopBar(title = "Profile", onBack = { viewModel.onIntent(PublicProfileIntent.BackClicked) })

        if (state.loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Teal)
            }
            return@Column
        }

        state.error?.let { errorMsg ->
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = errorMsg,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink2,
                )
            }
            return@Column
        }

        val user = state.user ?: return@Column

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // Teal gradient header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Teal, Color(0xFF155C4E)),
                        )
                    )
                    .padding(horizontal = 24.dp, vertical = 28.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Avatar(
                        name = user.displayName,
                        photoUrl = user.photoUrl,
                        size = 80,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = user.displayName ?: "—",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                        if (user.isIdVerified) {
                            VerifiedBadge(tint = Color.White, size = 20)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Verified neighbour",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                    )
                }
            }

            // Stats row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                PublicStatCell(
                    value = user.completedHelpCount?.toString() ?: "—",
                    label = "Helped",
                )
                PublicStatCell(
                    value = user.aggregateRating?.let { String.format("%.1f", it) } ?: "—",
                    label = "Rating",
                )
                PublicStatCell(
                    value = String.format("%.1f", user.trustScore),
                    label = "Trust",
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Line),
            )

            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                SectionLabel("Recent ratings")
                Spacer(modifier = Modifier.height(4.dp))

                if (state.ratings.isEmpty()) {
                    Text(
                        text = "No ratings yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ink3,
                        modifier = Modifier.padding(vertical = 12.dp),
                    )
                } else {
                    state.ratings.forEach { rating ->
                        RatingCard(rating = rating)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PublicStatCell(
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
private fun RatingCard(
    rating: Rating,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Avatar(
            name = rating.raterName,
            photoUrl = rating.raterPhotoUrl,
            size = 40,
        )
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = rating.raterName ?: "Anonymous",
                    style = MaterialTheme.typography.titleSmall,
                    color = Ink,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "★".repeat(rating.score),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Marigold,
                )
            }
            rating.comment?.let { comment ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = comment,
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink2,
                )
            }
        }
    }
}
