package com.nearaid.core.designsystem.component

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import com.nearaid.core.designsystem.theme.NearAidTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Accessibility guardrails for the shared interactive components. These assert the
 * semantics that screen readers (TalkBack) rely on — role, selected state, and the
 * 48dp minimum touch target — so a11y regressions fail the test suite.
 *
 * Runs on the JVM via Robolectric (no emulator needed).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class AccessibilityTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun hasRole(role: Role) = SemanticsMatcher.expectValue(SemanticsProperties.Role, role)

    @Test
    fun chip_isSelectableTab_withMinimumTouchTarget() {
        composeRule.setContent {
            NearAidTheme {
                NearAidChip(label = "Food", selected = true, onClick = {})
            }
        }

        composeRule.onNodeWithText("Food")
            .assert(hasRole(Role.Tab))
            .assertIsSelected()
            .assertHeightIsAtLeast(48.dp)
            .assertWidthIsAtLeast(48.dp)
    }

    @Test
    fun segmentedTabs_exposeSelectedStateAndTouchTarget() {
        composeRule.setContent {
            NearAidTheme {
                NearAidSegmentedTabs(
                    options = listOf("Needs", "Offers"),
                    selectedIndex = 0,
                    onSelect = {},
                )
            }
        }

        composeRule.onNodeWithText("Needs")
            .assert(hasRole(Role.Tab))
            .assertIsSelected()
            .assertHeightIsAtLeast(48.dp)

        composeRule.onNodeWithText("Offers")
            .assertIsNotSelected()
    }
}
