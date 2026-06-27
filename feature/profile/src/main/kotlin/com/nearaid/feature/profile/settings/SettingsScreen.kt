package com.nearaid.feature.profile.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.nearaid.core.designsystem.component.NearAidButtonVariant
import com.nearaid.core.designsystem.component.NearAidChip
import com.nearaid.core.designsystem.component.NearAidTopBar
import com.nearaid.core.designsystem.component.SectionLabel
import com.nearaid.core.designsystem.theme.Rust
import com.nearaid.core.model.AppLanguage

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            SettingsEffect.LoggedOut -> onLoggedOut()
            SettingsEffect.NavigateBack -> onBack()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        NearAidTopBar(title = "Settings & language", onBack = { viewModel.onIntent(SettingsIntent.BackClicked) })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SectionLabel("Language · ভাষা")

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NearAidChip(
                    label = "বাংলা",
                    selected = state.language == AppLanguage.BN,
                    onClick = { viewModel.onIntent(SettingsIntent.SelectLanguage(AppLanguage.BN)) },
                )
                NearAidChip(
                    label = "English",
                    selected = state.language == AppLanguage.EN,
                    onClick = { viewModel.onIntent(SettingsIntent.SelectLanguage(AppLanguage.EN)) },
                )
            }

            state.error?.let { errorMsg ->
                Text(
                    text = errorMsg,
                    style = MaterialTheme.typography.bodySmall,
                    color = Rust,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            NearAidButton(
                text = "Log out",
                onClick = { viewModel.onIntent(SettingsIntent.LogOut) },
                variant = NearAidButtonVariant.Rust,
                loading = state.loading,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
