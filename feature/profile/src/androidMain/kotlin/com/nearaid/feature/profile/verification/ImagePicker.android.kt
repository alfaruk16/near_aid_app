package com.nearaid.feature.profile.verification

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.io.File

@Composable
actual fun rememberImagePicker(fileName: String, onPicked: (String) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        uri?.let { copyUriToCache(context, it, fileName)?.let(onPicked) }
    }
    return { launcher.launch("image/*") }
}

private fun copyUriToCache(context: Context, uri: Uri, fileName: String): String? = runCatching {
    val input = context.contentResolver.openInputStream(uri) ?: return null
    val file = File(context.cacheDir, fileName)
    input.use { i -> file.outputStream().use { o -> i.copyTo(o) } }
    file.absolutePath
}.getOrNull()
