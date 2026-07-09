package com.nearaid.core.designsystem.component

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The shared accessibility contract — the single source of truth for the invariants both the
 * production components and the a11y test suites enforce, so a threshold or labeling rule is defined
 * exactly once. Pure Kotlin, so it lives in `commonMain` and is verified from `commonTest` on every
 * platform (see `A11yContractTest`).
 */
object A11y {

    /** WCAG 2.5.5 / Material minimum interactive touch target. */
    val MinTouchTarget: Dp = 48.dp

    /**
     * A clickable node is properly labeled for a screen reader when it exposes at least one non-blank
     * content description, visible text, or state description. Fed the raw semantics values so the
     * same rule can be checked against a rendered tree (Robolectric/UI test) or in a plain unit test.
     */
    fun isLabeled(
        contentDescriptions: List<String>?,
        texts: List<String>?,
        stateDescription: String?,
    ): Boolean =
        contentDescriptions?.any { it.isNotBlank() } == true ||
            texts?.any { it.isNotBlank() } == true ||
            !stateDescription.isNullOrBlank()
}
