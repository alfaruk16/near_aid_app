package com.nearaid.feature.auth.otp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nearaid.core.designsystem.component.CollectEffect
import com.nearaid.core.designsystem.component.NearAidButton
import com.nearaid.core.designsystem.component.NearAidTextField
import com.nearaid.core.designsystem.component.NearAidTopBar
import com.nearaid.core.designsystem.theme.Ink2
import com.nearaid.core.domain.usecase.PhoneNumber

@Composable
fun OtpScreen(
    requestId: String,
    phoneE164: String,
    onBack: () -> Unit,
    onVerified: (isNewUser: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OtpViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is OtpEffect.Verified -> onVerified(effect.isNewUser)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        NearAidTopBar(title = "", onBack = onBack)
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text("Enter the code", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Sent to ${PhoneNumber.formatForDisplay(phoneE164)}",
                style = MaterialTheme.typography.bodyMedium,
                color = Ink2,
            )
            NearAidTextField(
                value = state.code,
                onValueChange = { viewModel.onIntent(OtpIntent.CodeChanged(it)) },
                label = "6-digit code",
                keyboardType = KeyboardType.NumberPassword,
                isError = state.error != null,
                supportingText = state.error,
            )
            NearAidButton(
                text = "Verify",
                onClick = { viewModel.onIntent(OtpIntent.Verify(requestId)) },
                enabled = state.code.length == 6 && !state.loading,
                loading = state.loading,
            )
        }
    }
}
