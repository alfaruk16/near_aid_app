package com.nearaid.feature.post.create

import com.nearaid.core.common.mvi.UiEffect
import com.nearaid.core.common.mvi.UiIntent
import com.nearaid.core.common.mvi.UiState
import com.nearaid.core.model.Category
import com.nearaid.core.model.ListingType
import com.nearaid.core.model.Urgency

// ---------------------------------------------------------------------------
// UI State
// ---------------------------------------------------------------------------

data class CreateListingState(
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: Int? = null,
    val title: String = "",
    val description: String = "",
    val quantity: String = "",
    val urgency: Urgency = Urgency.MEDIUM,
    val availableUntil: String = "",
    val loading: Boolean = false,
    val error: String? = null,
) : UiState {
    val canSubmit: Boolean
        get() = selectedCategoryId != null && title.isNotBlank() && !loading
}

// ---------------------------------------------------------------------------
// Intent
// ---------------------------------------------------------------------------

sealed interface CreateListingIntent : UiIntent {
    data class CategorySelected(val id: Int) : CreateListingIntent
    data class TitleChanged(val value: String) : CreateListingIntent
    data class DescriptionChanged(val value: String) : CreateListingIntent
    data class QuantityChanged(val value: String) : CreateListingIntent
    data class UrgencySelected(val urgency: Urgency) : CreateListingIntent
    data class AvailableUntilChanged(val value: String) : CreateListingIntent
    data class Submit(val type: ListingType) : CreateListingIntent
}

// ---------------------------------------------------------------------------
// Effect
// ---------------------------------------------------------------------------

sealed interface CreateListingEffect : UiEffect {
    data class Posted(val listingId: String) : CreateListingEffect
}
