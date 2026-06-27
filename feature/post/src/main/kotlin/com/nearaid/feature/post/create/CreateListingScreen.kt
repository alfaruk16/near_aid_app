package com.nearaid.feature.post.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nearaid.core.designsystem.component.CollectEffect
import com.nearaid.core.designsystem.component.NearAidButton
import com.nearaid.core.designsystem.component.NearAidButtonVariant
import com.nearaid.core.designsystem.component.NearAidChip
import com.nearaid.core.designsystem.component.NearAidTextField
import com.nearaid.core.designsystem.component.NearAidTopBar
import com.nearaid.core.designsystem.component.SectionLabel
import com.nearaid.core.designsystem.theme.Ink3
import com.nearaid.core.model.ListingType
import com.nearaid.core.model.Urgency

// ---------------------------------------------------------------------------
// Category emoji map
// ---------------------------------------------------------------------------

private fun categoryEmoji(key: String): String = when (key) {
    "food"     -> "🍲"
    "clothes"  -> "🧥"
    "medicine" -> "💊"
    "goods"    -> "📦"
    "shelter"  -> "🏠"
    else       -> "•"
}

// ---------------------------------------------------------------------------
// Screen
// ---------------------------------------------------------------------------

/**
 * Full listing-creation form for either a REQUEST or an OFFER.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateListingScreen(
    type: ListingType,
    onBack: () -> Unit,
    onPosted: (listingId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateListingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is CreateListingEffect.Posted -> onPosted(effect.listingId)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        NearAidTopBar(
            title = if (type == ListingType.REQUEST) "New request" else "New offer",
            onBack = onBack,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {

            // ---- Category chips ----
            SectionLabel("Category")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                state.categories.forEach { category ->
                    NearAidChip(
                        label = category.nameEn,
                        selected = state.selectedCategoryId == category.id,
                        onClick = { viewModel.onIntent(CreateListingIntent.CategorySelected(category.id)) },
                        leading = categoryEmoji(category.key),
                    )
                }
            }

            // ---- Title ----
            NearAidTextField(
                value = state.title,
                onValueChange = { viewModel.onIntent(CreateListingIntent.TitleChanged(it)) },
                label = if (type == ListingType.REQUEST) "What do you need?" else "What are you giving?",
                isError = state.error != null && state.title.isBlank(),
                supportingText = if (state.error != null && state.title.isBlank()) state.error else null,
            )

            // ---- Description ----
            NearAidTextField(
                value = state.description,
                onValueChange = { viewModel.onIntent(CreateListingIntent.DescriptionChanged(it)) },
                label = "Description",
                singleLine = false,
                minLines = 3,
            )

            // ---- Quantity ----
            NearAidTextField(
                value = state.quantity,
                onValueChange = { viewModel.onIntent(CreateListingIntent.QuantityChanged(it)) },
                label = "Quantity (optional)",
                placeholder = "e.g. 2 kg, 1 bag",
            )

            // ---- REQUEST-only: Urgency chips ----
            if (type == ListingType.REQUEST) {
                SectionLabel("Urgency")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Urgency.entries.forEach { urgency ->
                        NearAidChip(
                            label = urgency.name.lowercase()
                                .replaceFirstChar { it.uppercase() },
                            selected = state.urgency == urgency,
                            onClick = { viewModel.onIntent(CreateListingIntent.UrgencySelected(urgency)) },
                        )
                    }
                }
            }

            // ---- OFFER-only: Available Until ----
            if (type == ListingType.OFFER) {
                NearAidTextField(
                    value = state.availableUntil,
                    onValueChange = { viewModel.onIntent(CreateListingIntent.AvailableUntilChanged(it)) },
                    label = "Available until (optional)",
                    placeholder = "e.g. 8:00 PM",
                )
            }

            // ---- Location note ----
            Text(
                text = "Mirpur, Dhaka — shown to others as an approximate area only",
                style = MaterialTheme.typography.bodySmall,
                color = Ink3,
                modifier = Modifier.padding(vertical = 4.dp),
            )

            // ---- Submission error (generic) ----
            if (state.error != null && state.title.isNotBlank()) {
                Text(
                    text = state.error!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = androidx.compose.ui.graphics.Color(0xFFC4502E), // Rust inline to avoid extra import
                )
            }

            // ---- Submit button ----
            NearAidButton(
                text = if (type == ListingType.REQUEST) "Post request" else "Post offer",
                onClick = { viewModel.onIntent(CreateListingIntent.Submit(type)) },
                variant = if (type == ListingType.REQUEST) NearAidButtonVariant.Primary else NearAidButtonVariant.Teal,
                enabled = state.canSubmit,
                loading = state.loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            )
        }
    }
}
