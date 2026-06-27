package com.nearaid.feature.profile.verification

import androidx.lifecycle.viewModelScope
import com.nearaid.core.common.mvi.MviViewModel
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.SubmitVerificationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val submitVerification: SubmitVerificationUseCase,
) : MviViewModel<VerificationState, VerificationIntent, VerificationEffect>() {

    override fun initialState(): VerificationState = VerificationState()

    override fun onIntent(intent: VerificationIntent) {
        when (intent) {
            is VerificationIntent.DocumentPicked -> setState { copy(documentPath = intent.path, error = null) }
            VerificationIntent.Submit -> submit()
            VerificationIntent.BackClicked -> sendEffect(VerificationEffect.NavigateBack)
        }
    }

    private fun submit() {
        val path = currentState.documentPath ?: return
        setState { copy(loading = true, error = null, success = false) }
        viewModelScope.launch {
            when (val result = submitVerification(path)) {
                is DataResult.Success -> setState { copy(loading = false, success = true) }
                is DataResult.Failure -> setState { copy(loading = false, error = result.error.message) }
            }
        }
    }
}
