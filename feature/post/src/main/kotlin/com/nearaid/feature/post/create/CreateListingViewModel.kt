package com.nearaid.feature.post.create

import androidx.lifecycle.viewModelScope
import com.nearaid.core.common.mvi.MviViewModel
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.CreateListingUseCase
import com.nearaid.core.domain.usecase.ObserveCategoriesUseCase
import com.nearaid.core.domain.usecase.RefreshCategoriesUseCase
import com.nearaid.core.model.ListingType
import com.nearaid.core.model.NewListing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateListingViewModel @Inject constructor(
    private val observeCategories: ObserveCategoriesUseCase,
    private val refreshCategories: RefreshCategoriesUseCase,
    private val createListing: CreateListingUseCase,
) : MviViewModel<CreateListingState, CreateListingIntent, CreateListingEffect>() {

    override fun initialState(): CreateListingState = CreateListingState()

    init {
        // Observe cached categories immediately
        observeCategories()
            .onEach { list ->
                setState {
                    copy(
                        categories = list,
                        // Auto-select the first category if none selected yet
                        selectedCategoryId = selectedCategoryId ?: list.firstOrNull()?.id,
                    )
                }
            }
            .launchIn(viewModelScope)

        // Refresh from network in the background
        viewModelScope.launch {
            refreshCategories()
        }
    }

    override fun onIntent(intent: CreateListingIntent) {
        when (intent) {
            is CreateListingIntent.CategorySelected -> setState { copy(selectedCategoryId = intent.id, error = null) }
            is CreateListingIntent.TitleChanged -> setState { copy(title = intent.value, error = null) }
            is CreateListingIntent.DescriptionChanged -> setState { copy(description = intent.value, error = null) }
            is CreateListingIntent.QuantityChanged -> setState { copy(quantity = intent.value) }
            is CreateListingIntent.UrgencySelected -> setState { copy(urgency = intent.urgency) }
            is CreateListingIntent.AvailableUntilChanged -> setState { copy(availableUntil = intent.value) }
            is CreateListingIntent.Submit -> submit(intent.type)
        }
    }

    private fun submit(type: ListingType) {
        val s = currentState
        val categoryId = s.selectedCategoryId ?: return
        if (s.title.isBlank()) {
            setState { copy(error = "Please enter a title") }
            return
        }

        setState { copy(loading = true, error = null) }
        viewModelScope.launch {
            // TODO: replace with real device location once location is wired in v2
            val input = NewListing(
                type = type,
                categoryId = categoryId,
                title = s.title.trim(),
                description = s.description.trim(),
                quantity = s.quantity.trim().takeIf { it.isNotBlank() },
                urgency = if (type == ListingType.REQUEST) s.urgency else null,
                availableUntil = if (type == ListingType.OFFER) s.availableUntil.trim().takeIf { it.isNotBlank() } else null,
                lat = 23.8103,   // TODO: replace with real GPS lat in v2
                lng = 90.4125,   // TODO: replace with real GPS lng in v2
                areaLabel = "Mirpur, Dhaka",
                imageUrls = emptyList(),
            )
            when (val result = createListing(input)) {
                is DataResult.Success -> {
                    setState { copy(loading = false) }
                    sendEffect(CreateListingEffect.Posted(result.data.id))
                }
                is DataResult.Failure -> setState { copy(loading = false, error = result.error.message) }
            }
        }
    }
}
