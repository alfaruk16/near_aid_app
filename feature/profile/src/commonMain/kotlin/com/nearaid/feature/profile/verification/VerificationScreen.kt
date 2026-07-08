package com.nearaid.feature.profile.verification

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nearaid.core.designsystem.component.CollectEffect
import com.nearaid.core.designsystem.component.NearAidButton
import com.nearaid.core.designsystem.component.NearAidTopBar
import com.nearaid.core.designsystem.component.accessibleClickable
import com.nearaid.core.designsystem.theme.NearAidTheme
import com.nearaid.feature.profile.resources.Res
import com.nearaid.feature.profile.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun VerificationScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VerificationViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            VerificationEffect.NavigateBack -> onBack()
        }
    }

    // Local UI state for tracking which slot was picked (front vs selfie).
    var idFrontPath by remember { mutableStateOf<String?>(null) }
    var selfieImagePath by remember { mutableStateOf<String?>(null) }

    val pickIdFront = rememberImagePicker("verification_id_front.jpg") { path ->
        idFrontPath = path
        viewModel.onIntent(VerificationIntent.DocumentPicked(path))
    }
    val pickSelfie = rememberImagePicker("verification_selfie.jpg") { path ->
        selfieImagePath = path
        viewModel.onIntent(VerificationIntent.DocumentPicked(path))
    }

    Column(modifier = modifier.fillMaxSize()) {
        NearAidTopBar(title = stringResource(Res.string.verification_title), onBack = { viewModel.onIntent(VerificationIntent.BackClicked) })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(Res.string.verification_intro),
                style = MaterialTheme.typography.bodyMedium,
                color = NearAidTheme.colors.ink2,
            )

            // National ID upload
            UploadRow(
                label = stringResource(Res.string.verification_national_id_front),
                isUploaded = idFrontPath != null,
                onClick = pickIdFront,
            )

            // Selfie upload
            UploadRow(
                label = stringResource(Res.string.verification_selfie),
                isUploaded = selfieImagePath != null,
                onClick = pickSelfie,
            )

            // Success message
            if (state.success) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, NearAidTheme.colors.teal, RoundedCornerShape(14.dp))
                        .padding(14.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = NearAidTheme.colors.teal,
                            modifier = Modifier.size(20.dp),
                        )
                        Text(
                            text = stringResource(Res.string.verification_submitted),
                            style = MaterialTheme.typography.bodyMedium,
                            color = NearAidTheme.colors.teal,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            // Error message
            state.error?.let { errorMsg ->
                Text(
                    text = errorMsg,
                    style = MaterialTheme.typography.bodySmall,
                    color = NearAidTheme.colors.rust,
                )
            }

            NearAidButton(
                text = stringResource(Res.string.verification_submit),
                onClick = { viewModel.onIntent(VerificationIntent.Submit) },
                enabled = state.documentPath != null && !state.loading && !state.success,
                loading = state.loading,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(Res.string.verification_privacy_note),
                style = MaterialTheme.typography.bodySmall,
                color = NearAidTheme.colors.ink3,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun UploadRow(
    label: String,
    isUploaded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = 1.5.dp,
                color = if (isUploaded) NearAidTheme.colors.teal else NearAidTheme.colors.line,
                shape = RoundedCornerShape(14.dp),
            )
            .accessibleClickable(onClickLabel = stringResource(Res.string.action_open), onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 20.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = if (isUploaded) Icons.Filled.CheckCircle else Icons.Filled.Upload,
                contentDescription = null,
                tint = if (isUploaded) NearAidTheme.colors.teal else NearAidTheme.colors.ink3,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isUploaded) NearAidTheme.colors.teal else NearAidTheme.colors.ink2,
                fontWeight = if (isUploaded) FontWeight.SemiBold else FontWeight.Normal,
            )
        }
    }
}
