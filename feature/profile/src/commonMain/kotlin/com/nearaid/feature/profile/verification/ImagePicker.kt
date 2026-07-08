package com.nearaid.feature.profile.verification

import androidx.compose.runtime.Composable

/**
 * Platform image picker. Returns a trigger lambda; invoking it opens the platform picker and,
 * once the user picks an image, calls [onPicked] with an on-disk file path the repository can read.
 * [fileName] disambiguates the cached copy (e.g. ID-front vs selfie).
 *
 * Android uses `GetContent` + a cache copy. iOS is a Phase 5 platform edge (stub for now).
 */
@Composable
expect fun rememberImagePicker(fileName: String, onPicked: (String) -> Unit): () -> Unit
