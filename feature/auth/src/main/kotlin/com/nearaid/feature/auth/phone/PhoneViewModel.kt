package com.nearaid.feature.auth.phone

import androidx.lifecycle.viewModelScope
import com.nearaid.core.common.mvi.MviViewModel
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.PhoneNumber
import com.nearaid.core.domain.usecase.RequestOtpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhoneViewModel @Inject constructor(
    private val requestOtp: RequestOtpUseCase,
) : MviViewModel<PhoneState, PhoneIntent, PhoneEffect>() {

    override fun initialState() = PhoneState()

    override fun onIntent(intent: PhoneIntent) {
        when (intent) {
            is PhoneIntent.PhoneChanged ->
                setState { copy(phone = intent.value.filter { it.isDigit() }.take(10), error = null) }
            PhoneIntent.Submit -> submit()
        }
    }

    private fun submit() {
        val e164 = PhoneNumber.normalizeBd(currentState.phone) ?: return
        setState { copy(loading = true, error = null) }
        viewModelScope.launch {
            when (val result = requestOtp(e164)) {
                is DataResult.Success -> {
                    setState { copy(loading = false) }
                    sendEffect(PhoneEffect.CodeSent(result.data.requestId, e164))
                }
                is DataResult.Failure -> setState { copy(loading = false, error = result.error.message) }
            }
        }
    }
}
