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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nearaid.core.designsystem.component.CollectEffect
import com.nearaid.core.designsystem.component.NearAidButton
import com.nearaid.core.designsystem.component.NearAidTextField
import com.nearaid.core.designsystem.component.NearAidTopBar
import com.nearaid.core.designsystem.component.headingSemantics
import com.nearaid.core.designsystem.theme.NearAidTheme
import com.nearaid.feature.auth.R

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
            Text(stringResource(R.string.phone_title), style = MaterialTheme.typography.headlineMedium, modifier = Modifier.headingSemantics())
            Text(
                stringResource(R.string.phone_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = NearAidTheme.colors.ink2,
            )
            NearAidTextField(
                value = state.phone,
                onValueChange = { viewModel.onIntent(PhoneIntent.PhoneChanged(it)) },
                label = stringResource(R.string.phone_label),
                placeholder = stringResource(R.string.phone_placeholder),
                keyboardType = KeyboardType.Phone,
                isError = state.error != null,
                supportingText = state.error,
            )
            NearAidButton(
                text = stringResource(R.string.phone_send_code),
                onClick = { viewModel.onIntent(PhoneIntent.Submit) },
                enabled = state.canSubmit,
                loading = state.loading,
            )
            Text(
                stringResource(R.string.phone_terms),
                style = MaterialTheme.typography.bodySmall,
                color = NearAidTheme.colors.ink3,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
