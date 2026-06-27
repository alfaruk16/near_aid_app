package com.nearaid.feature.profile.publicprofile

import com.nearaid.core.common.mvi.UiEffect
import com.nearaid.core.common.mvi.UiIntent
import com.nearaid.core.common.mvi.UiState
import com.nearaid.core.model.PublicUser
import com.nearaid.core.model.Rating

data class PublicProfileState(
    val user: PublicUser? = null,
    val ratings: List<Rating> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null,
) : UiState

sealed interface PublicProfileIntent : UiIntent {
    data class Load(val userId: String) : PublicProfileIntent
    data object BackClicked : PublicProfileIntent
}

sealed interface PublicProfileEffect : UiEffect {
    data object NavigateBack : PublicProfileEffect
}
