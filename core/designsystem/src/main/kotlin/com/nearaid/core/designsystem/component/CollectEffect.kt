package com.nearaid.core.designsystem.component

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.nearaid.core.common.mvi.UiEffect
import kotlinx.coroutines.flow.Flow
import androidx.compose.runtime.LaunchedEffect

/**
 * Lifecycle-aware one-shot effect collector for MVI screens. Collects [effects]
 * only while the view is at least STARTED, so navigation/toasts never replay.
 */
@Composable
fun <E : UiEffect> CollectEffect(effects: Flow<E>, onEffect: (E) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(effects, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            effects.collect(onEffect)
        }
    }
}
