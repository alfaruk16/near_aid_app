package com.nearaid.feature.profile.verification

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nearaid.core.designsystem.component.CollectEffect
import com.nearaid.core.designsystem.component.NearAidButton
import com.nearaid.core.designsystem.component.NearAidTopBar
import com.nearaid.core.designsystem.theme.Ink2
import com.nearaid.core.designsystem.theme.Ink3
import com.nearaid.core.designsystem.theme.Line
import com.nearaid.core.designsystem.theme.Rust
import com.nearaid.core.designsystem.theme.Teal
import java.io.File

@Composable
fun VerificationScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VerificationViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            VerificationEffect.NavigateBack -> onBack()
        }
    }

    // Local UI state for tracking which slot was picked (front vs selfie)
    var idFrontPath by remember { mutableStateOf<String?>(null) }
    var selfieImagePath by remember { mutableStateOf<String?>(null) }

    val idFrontLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        uri?.let {
            val path = copyUriToCache(context, it, "verification_id_front.jpg")
            if (path != null) {
                idFrontPath = path
                viewModel.onIntent(VerificationIntent.DocumentPicked(path))
            }
        }
    }

    val selfieLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        uri?.let {
            val path = copyUriToCache(context, it, "verification_selfie.jpg")
            if (path != null) {
                selfieImagePath = path
                viewModel.onIntent(VerificationIntent.DocumentPicked(path))
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        NearAidTopBar(title = "Get verified", onBack = { viewModel.onIntent(VerificationIntent.BackClicked) })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "A verified badge raises your trust score and gets your requests seen faster.",
                style = MaterialTheme.typography.bodyMedium,
                color = Ink2,
            )

            // National ID upload
            UploadRow(
                label = "National ID — front",
                isUploaded = idFrontPath != null,
                onClick = { idFrontLauncher.launch("image/*") },
            )

            // Selfie upload
            UploadRow(
                label = "Selfie",
                isUploaded = selfieImagePath != null,
                onClick = { selfieLauncher.launch("image/*") },
            )

            // Success message
            if (state.success) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, Teal, RoundedCornerShape(14.dp))
                        .padding(14.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Teal,
                            modifier = Modifier.size(20.dp),
                        )
                        Text(
                            text = "Submitted — status: pending",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Teal,
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
                    color = Rust,
                )
            }

            NearAidButton(
                text = "Submit for review",
                onClick = { viewModel.onIntent(VerificationIntent.Submit) },
                enabled = state.documentPath != null && !state.loading && !state.success,
                loading = state.loading,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Documents are encrypted and never shown to other users.",
                style = MaterialTheme.typography.bodySmall,
                color = Ink3,
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
                color = if (isUploaded) Teal else Line,
                shape = RoundedCornerShape(14.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 20.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = if (isUploaded) Icons.Filled.CheckCircle else Icons.Filled.Upload,
                contentDescription = null,
                tint = if (isUploaded) Teal else Ink3,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isUploaded) Teal else Ink2,
                fontWeight = if (isUploaded) FontWeight.SemiBold else FontWeight.Normal,
            )
        }
    }
}

private fun copyUriToCache(context: android.content.Context, uri: Uri, fileName: String): String? {
    return runCatching {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.cacheDir, fileName)
        inputStream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    }.getOrNull()
}
