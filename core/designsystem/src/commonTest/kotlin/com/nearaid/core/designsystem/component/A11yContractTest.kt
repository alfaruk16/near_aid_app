package com.nearaid.core.designsystem.component

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Verifies the shared [A11y] contract on every platform (Android JVM + iOS native). The rendering
 * guardrails in `AccessibilityChecksTest`/`AccessibilityTest` (Android/Robolectric) enforce these same
 * rules against a real semantics tree; this locks the rules themselves as one cross-platform source
 * of truth.
 */
class A11yContractTest {

    @Test
    fun minTouchTarget_meetsWcagMinimum() {
        assertEquals(48.dp, A11y.MinTouchTarget)
    }

    @Test
    fun labeled_whenContentDescriptionPresent() {
        assertTrue(A11y.isLabeled(contentDescriptions = listOf("Back"), texts = null, stateDescription = null))
    }

    @Test
    fun labeled_whenTextPresent() {
        assertTrue(A11y.isLabeled(contentDescriptions = null, texts = listOf("Submit"), stateDescription = null))
    }

    @Test
    fun labeled_whenStateDescriptionPresent() {
        assertTrue(A11y.isLabeled(contentDescriptions = null, texts = null, stateDescription = "Selected"))
    }

    @Test
    fun unlabeled_whenAllBlankOrMissing() {
        assertFalse(A11y.isLabeled(contentDescriptions = null, texts = null, stateDescription = null))
        assertFalse(A11y.isLabeled(contentDescriptions = listOf("  "), texts = listOf(""), stateDescription = ""))
    }
}
