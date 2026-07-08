package com.nearaid.feature.profile.verification

import androidx.compose.runtime.Composable

/**
 * iOS image picking is a Phase 5 platform edge — a `PHPickerViewController`/`UIImagePickerController`
 * bridge that writes the chosen image to a temp file and returns its path. Stubbed for now so the
 * shared screen compiles and renders; the upload button simply stays inert on iOS.
 */
@Composable
actual fun rememberImagePicker(fileName: String, onPicked: (String) -> Unit): () -> Unit = {}
