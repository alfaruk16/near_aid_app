package com.nearaid.feature.profile.publicprofile

import androidx.lifecycle.viewModelScope
import com.nearaid.core.common.mvi.MviViewModel
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.GetPublicUserUseCase
import com.nearaid.core.domain.usecase.GetUserRatingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PublicProfileViewModel @Inject constructor(
    private val getPublicUser: GetPublicUserUseCase,
    private val getUserRatings: GetUserRatingsUseCase,
) : MviViewModel<PublicProfileState, PublicProfileIntent, PublicProfileEffect>() {

    override fun initialState(): PublicProfileState = PublicProfileState()

    override fun onIntent(intent: PublicProfileIntent) {
        when (intent) {
            is PublicProfileIntent.Load -> load(intent.userId)
            PublicProfileIntent.BackClicked -> sendEffect(PublicProfileEffect.NavigateBack)
        }
    }

    private fun load(userId: String) {
        setState { copy(loading = true, error = null) }
        viewModelScope.launch {
            val userResult = getPublicUser(userId)
            val ratingsResult = getUserRatings(userId)

            val user = when (userResult) {
                is DataResult.Success -> userResult.data
                is DataResult.Failure -> {
                    setState { copy(loading = false, error = userResult.error.message) }
                    return@launch
                }
            }

            val ratings = when (ratingsResult) {
                is DataResult.Success -> ratingsResult.data.items
                is DataResult.Failure -> emptyList()
            }

            setState { copy(loading = false, user = user, ratings = ratings) }
        }
    }
}
