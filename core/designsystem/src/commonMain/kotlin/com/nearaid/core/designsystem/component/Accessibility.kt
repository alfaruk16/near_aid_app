package com.nearaid.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics

/**
 * Shared accessibility modifiers so every screen applies the same TalkBack conventions.
 */

/** Marks the node as a heading, letting screen-reader users jump between sections. */
fun Modifier.headingSemantics(): Modifier = semantics { heading() }

/**
 * Announces this node when its content appears or changes — use on loading, error,
 * and other transient status containers so blind users hear the update.
 */
fun Modifier.politeLiveRegion(): Modifier = semantics { liveRegion = LiveRegionMode.Polite }

/**
 * A clickable that reads correctly to a screen reader: merges the children into one
 * focusable node, exposes the [role], describes the action via [onClickLabel]
 * (e.g. "Open"), and guarantees the 48dp minimum touch target.
 */
fun Modifier.accessibleClickable(
    onClickLabel: String? = null,
    role: Role = Role.Button,
    onClick: () -> Unit,
): Modifier = this
    .defaultMinSize(minWidth = A11y.MinTouchTarget, minHeight = A11y.MinTouchTarget)
    .clickable(onClickLabel = onClickLabel, role = role, onClick = onClick)

/** Labels an otherwise-silent status node (e.g. a bare progress indicator). */
fun Modifier.statusSemantics(description: String): Modifier =
    semantics {
        this.contentDescription = description
        liveRegion = LiveRegionMode.Polite
    }
