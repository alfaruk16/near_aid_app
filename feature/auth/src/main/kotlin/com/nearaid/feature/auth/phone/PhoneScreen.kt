package com.nearaid.feature.auth.phone

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nearaid.core.designsystem.component.CollectEffect
import com.nearaid.core.designsystem.component.NearAidButton
import com.nearaid.core.designsystem.component.NearAidTextField
import com.nearaid.core.designsystem.component.NearAidTopBar
import com.nearaid.core.designsystem.theme.Ink2
import com.nearaid.core.designsystem.theme.Ink3

@Composable
fun PhoneScreen(
    onBack: () -> Unit,
    onCodeSent: (requestId: String, e164: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PhoneViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is PhoneEffect.CodeSent -> onCodeSent(effect.requestId, effect.e164)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        NearAidTopBar(title = "", onBack = onBack)
        Column(
            modifier = Modifier.padding(horizontal = 20.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text("What's your number?", style = MaterialTheme.typography.headlineMedium)
            Text(
                "We'll text a 6-digit code to verify it. Your number stays private.",
                style = MaterialTheme.typography.bodyMedium,
                color = Ink2,
            )
            NearAidTextField(
                value = state.phone,
                onValueChange = { viewModel.onIntent(PhoneIntent.PhoneChanged(it)) },
                label = "Phone (+880)",
                placeholder = "1712 345 678",
                keyboardType = KeyboardType.Phone,
                isError = state.error != null,
                supportingText = state.error,
            )
            NearAidButton(
                text = "Send code",
                onClick = { viewModel.onIntent(PhoneIntent.Submit) },
                enabled = state.canSubmit,
                loading = state.loading,
            )
            Text(
                "By continuing you agree to our Community Guidelines & Safety Pledge",
                style = MaterialTheme.typography.bodySmall,
                color = Ink3,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
