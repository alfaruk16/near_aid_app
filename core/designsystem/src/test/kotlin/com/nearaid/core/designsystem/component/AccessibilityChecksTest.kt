package com.nearaid.core.designsystem.component

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import com.nearaid.core.designsystem.theme.NearAidTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Automated accessibility guardrail: renders the shared interactive components and asserts
 * two invariants across the whole semantics tree, so a regression anywhere fails the suite:
 *   1. every clickable node carries a screen-reader label (contentDescription / text / state)
 *   2. every clickable node meets the 48dp minimum touch target
 *
 * Runs on the JVM via Robolectric (no emulator needed).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class AccessibilityChecksTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Composable
    private fun Sample() {
        NearAidTheme {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                NearAidButton(text = "Primary action", onClick = {})
                NearAidButton(text = "Ghost action", onClick = {}, variant = NearAidButtonVariant.Ghost)
                NearAidChip(label = "Food", selected = true, onClick = {})
                NearAidChip(label = "Clothes", selected = false, onClick = {})
                NearAidSegmentedTabs(options = listOf("Needs", "Offers"), selectedIndex = 0, onSelect = {})
                TextChooserRow(title = "Pick a category", subtitle = "Choose what you need", onClick = {})
                EmptyState(
                    icon = Icons.Filled.Info,
                    title = "Nothing here",
                    message = "Try again later.",
                    actionLabel = "Retry",
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun everyClickable_isLabeled_andMeetsTouchTarget() {
        composeRule.setContent { Sample() }

        val minPx = with(composeRule.density) { 48.dp.roundToPx() }
        val nodes = composeRule.onAllNodes(hasClickAction()).fetchSemanticsNodes()
        assertTrue("expected clickable nodes to be present", nodes.isNotEmpty())

        nodes.forEach { node ->
            val cfg = node.config
            val labeled =
                cfg.getOrNull(SemanticsProperties.ContentDescription)?.any { it.isNotBlank() } == true ||
                    cfg.getOrNull(SemanticsProperties.Text)?.any { it.text.isNotBlank() } == true ||
                    cfg.getOrNull(SemanticsProperties.StateDescription)?.isNotBlank() == true
            assertTrue("Clickable node has no accessibility label: $cfg", labeled)

            assertTrue(
                "Clickable node smaller than 48dp: ${node.size}",
                node.size.width >= minPx && node.size.height >= minPx,
            )
        }
    }
}
