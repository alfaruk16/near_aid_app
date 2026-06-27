package com.nearaid.feature.auth.profilesetup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.nearaid.core.designsystem.component.NearAidChip
import com.nearaid.core.designsystem.component.NearAidTextField
import com.nearaid.core.designsystem.component.SectionLabel
import com.nearaid.core.designsystem.theme.Ink2
import com.nearaid.core.model.AppLanguage

@Composable
fun ProfileSetupScreen(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileSetupViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            ProfileSetupEffect.Done -> onDone()
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("Almost there", style = MaterialTheme.typography.headlineMedium)
        Text("A name and photo help neighbours trust you.", style = MaterialTheme.typography.bodyMedium, color = Ink2)
        NearAidTextField(
            value = state.name,
            onValueChange = { viewModel.onIntent(ProfileSetupIntent.NameChanged(it)) },
            label = "Display name",
            isError = state.error != null,
            supportingText = state.error,
        )
        SectionLabel("Language · ভাষা")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NearAidChip("বাংলা", state.language == AppLanguage.BN, { viewModel.onIntent(ProfileSetupIntent.LanguageChanged(AppLanguage.BN)) })
            NearAidChip("English", state.language == AppLanguage.EN, { viewModel.onIntent(ProfileSetupIntent.LanguageChanged(AppLanguage.EN)) })
        }
        NearAidButton(
            text = "Finish setup",
            onClick = { viewModel.onIntent(ProfileSetupIntent.Finish) },
            loading = state.loading,
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
        )
    }
}
