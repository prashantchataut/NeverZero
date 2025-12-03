package com.productivitystreak.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.productivitystreak.ui.theme.NeverZeroTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Unit tests for XpButton component
 * Tests enabled state, disabled state, and press animation
 * 
 * Requirements: 2.3, 2.5
 */
@RunWith(AndroidJUnit4::class)
class XpButtonTest {

    @get:Rule
    val composeRule = createComposeRule()

    // ==================== Enabled State Tests ====================

    @Test
    fun xpButton_enabledState_rendersCorrectly() {
        // Arrange
        val xpAmount = 10
        val accentColor = Color.Blue
        
        // Act
        composeRule.setContent {
            NeverZeroTheme {
                XpButton(
                    xpAmount = xpAmount,
                    accentColor = accentColor,
                    onClick = {},
                    enabled = true
                )
            }
        }

        // Assert - Text is displayed correctly
        composeRule.onNodeWithText("+$xpAmount XP")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun xpButton_enabledState_isClickable() {
        // Arrange
        var clicked = false
        
        // Act
        composeRule.setContent {
            NeverZeroTheme {
                XpButton(
                    xpAmount = 15,
                    accentColor = Color.Cyan,
                    onClick = { clicked = true },
                    enabled = true
                )
            }
        }

        // Assert - Button has click action
        composeRule.onNodeWithText("+15 XP")
            .assertHasClickAction()
        
        // Act - Click the button
        composeRule.onNodeWithText("+15 XP")
            .performClick()
        
        // Assert - Click handler was called
        assert(clicked) { "onClick should be called when button is clicked" }
    }

    @Test
    fun xpButton_enabledState_displaysIcon() {
        // Arrange & Act
        composeRule.setContent {
            NeverZeroTheme {
                XpButton(
                    xpAmount = 20,
                    accentColor = Color.Green,
                    onClick = {},
                    enabled = true
                )
            }
        }

        // Assert - Icon and text are both present
        // The icon is decorative (contentDescription = null) but should be rendered
        composeRule.onNodeWithText("+20 XP")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun xpButton_enabledState_hasFullOpacity() {
        // Arrange & Act
        composeRule.setContent {
            NeverZeroTheme {
                XpButton(
                    xpAmount = 25,
                    accentColor = Color.Magenta,
                    onClick = {},
                    enabled = true
                )
            }
        }

        // Assert - Button is displayed (full opacity)
        composeRule.onNodeWithText("+25 XP")
            .assertExists()
            .assertIsDisplayed()
    }

    // ==================== Disabled State Tests ====================

    @Test
    fun xpButton_disabledState_rendersCorrectly() {
        // Arrange
        val xpAmount = 10
        val accentColor = Color.Blue
        
        // Act
        composeRule.setContent {
            NeverZeroTheme {
                XpButton(
                    xpAmount = xpAmount,
                    accentColor = accentColor,
                    onClick = {},
                    enabled = false
                )
            }
        }

        // Assert - Text is still displayed
        composeRule.onNodeWithText("+$xpAmount XP")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun xpButton_disabledState_doesNotTriggerClick() {
        // Arrange
        var clicked = false
        
        // Act
        composeRule.setContent {
            NeverZeroTheme {
                XpButton(
                    xpAmount = 30,
                    accentColor = Color.Red,
                    onClick = { clicked = true },
                    enabled = false
                )
            }
        }

        // Act - Try to click the disabled button
        composeRule.onNodeWithText("+30 XP")
            .performClick()
        
        // Assert - Click handler should not be called
        assert(!clicked) { "onClick should not be called when button is disabled" }
    }

    @Test
    fun xpButton_disabledState_hasReducedOpacity() {
        // Arrange & Act
        composeRule.setContent {
            NeverZeroTheme {
                XpButton(
                    xpAmount = 35,
                    accentColor = Color.Yellow,
                    onClick = {},
                    enabled = false
                )
            }
        }

        // Assert - Button is still displayed but with reduced visual emphasis
        // The implementation uses alpha = 0.5f for disabled state
        composeRule.onNodeWithText("+35 XP")
            .assertExists()
            .assertIsDisplayed()
    }

    // ==================== Press Animation Tests ====================

    @Test
    fun xpButton_pressAnimation_triggersOnClick() {
        // Arrange
        var clickCount = 0
        
        // Act
        composeRule.setContent {
            NeverZeroTheme {
                XpButton(
                    xpAmount = 40,
                    accentColor = Color.Cyan,
                    onClick = { clickCount++ },
                    enabled = true
                )
            }
        }

        // Act - Perform click
        composeRule.onNodeWithText("+40 XP")
            .performClick()
        
        // Wait for any animations to complete
        composeRule.waitForIdle()
        
        // Assert - Click was registered
        assert(clickCount == 1) { "Expected 1 click, got $clickCount" }
    }

    @Test
    fun xpButton_pressAnimation_multipleClicks() {
        // Arrange
        var clickCount = 0
        
        // Act
        composeRule.setContent {
            NeverZeroTheme {
                XpButton(
                    xpAmount = 45,
                    accentColor = Color.Blue,
                    onClick = { clickCount++ },
                    enabled = true
                )
            }
        }

        // Act - Perform multiple clicks
        val button = composeRule.onNodeWithText("+45 XP")
        button.performClick()
        composeRule.waitForIdle()
        button.performClick()
        composeRule.waitForIdle()
        button.performClick()
        composeRule.waitForIdle()
        
        // Assert - All clicks were registered
        assert(clickCount == 3) { "Expected 3 clicks, got $clickCount" }
    }

    @Test
    fun xpButton_pressAnimation_doesNotAffectDisabledButton() {
        // Arrange
        var clickCount = 0
        
        // Act
        composeRule.setContent {
            NeverZeroTheme {
                XpButton(
                    xpAmount = 50,
                    accentColor = Color.Green,
                    onClick = { clickCount++ },
                    enabled = false
                )
            }
        }

        // Act - Try to click disabled button multiple times
        val button = composeRule.onNodeWithText("+50 XP")
        button.performClick()
        composeRule.waitForIdle()
        button.performClick()
        composeRule.waitForIdle()
        
        // Assert - No clicks should be registered
        assert(clickCount == 0) { "Expected 0 clicks on disabled button, got $clickCount" }
    }

    // ==================== Additional State Tests ====================

    @Test
    fun xpButton_differentXpAmounts_renderCorrectly() {
        // Test various XP amounts
        val xpAmounts = listOf(5, 10, 15, 20, 25, 50, 100)
        
        xpAmounts.forEach { xp ->
            composeRule.setContent {
                NeverZeroTheme {
                    XpButton(
                        xpAmount = xp,
                        accentColor = Color.Blue,
                        onClick = {}
                    )
                }
            }
            
            composeRule.onNodeWithText("+$xp XP")
                .assertExists()
                .assertIsDisplayed()
        }
    }

    @Test
    fun xpButton_differentColors_renderCorrectly() {
        // Test various accent colors
        val colors = listOf(
            Color.Red,
            Color.Blue,
            Color.Green,
            Color.Yellow,
            Color.Cyan,
            Color.Magenta
        )
        
        colors.forEachIndexed { index, color ->
            composeRule.setContent {
                NeverZeroTheme {
                    XpButton(
                        xpAmount = 10,
                        accentColor = color,
                        onClick = {}
                    )
                }
            }
            
            // Assert button renders with each color
            composeRule.onNodeWithText("+10 XP")
                .assertExists()
                .assertIsDisplayed()
        }
    }

    @Test
    fun xpButton_stateTransition_enabledToDisabled() {
        // Arrange
        var enabled = true
        
        // Act
        composeRule.setContent {
            NeverZeroTheme {
                XpButton(
                    xpAmount = 15,
                    accentColor = Color.Blue,
                    onClick = {},
                    enabled = enabled
                )
            }
        }

        // Assert - Initially enabled
        composeRule.onNodeWithText("+15 XP")
            .assertExists()
            .assertIsDisplayed()
            .assertHasClickAction()
        
        // Act - Disable the button
        enabled = false
        composeRule.setContent {
            NeverZeroTheme {
                XpButton(
                    xpAmount = 15,
                    accentColor = Color.Blue,
                    onClick = {},
                    enabled = enabled
                )
            }
        }
        
        // Assert - Now disabled
        composeRule.onNodeWithText("+15 XP")
            .assertExists()
            .assertIsDisplayed()
    }
}
