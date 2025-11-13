package com.productivitystreak.ui.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView

/**
 * Haptic Feedback System
 * Provides consistent haptic feedback throughout the app
 */
class HapticFeedbackManager(private val context: Context, private val view: View) {

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    /**
     * Light tap - For general interactions, button presses
     */
    fun lightTap() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
        } else {
            vibratePattern(longArrayOf(0, 10))
        }
    }

    /**
     * Medium impact - For selections, switches
     */
    fun mediumImpact() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        } else {
            vibratePattern(longArrayOf(0, 20))
        }
    }

    /**
     * Heavy impact - For important actions, confirmations
     */
    fun heavyImpact() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        } else {
            vibratePattern(longArrayOf(0, 30))
        }
    }

    /**
     * Success pattern - For completed actions, achievements
     */
    fun success() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pattern = longArrayOf(0, 50, 50, 50)
            val amplitudes = intArrayOf(0, 100, 0, 150)
            vibrator?.vibrate(
                VibrationEffect.createWaveform(pattern, amplitudes, -1)
            )
        } else {
            vibratePattern(longArrayOf(0, 50, 50, 50))
        }
    }

    /**
     * Error pattern - For errors, failed actions
     */
    fun error() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pattern = longArrayOf(0, 30, 30, 30)
            val amplitudes = intArrayOf(0, 80, 0, 120)
            vibrator?.vibrate(
                VibrationEffect.createWaveform(pattern, amplitudes, -1)
            )
        } else {
            vibratePattern(longArrayOf(0, 30, 30, 30))
        }
    }

    /**
     * Selection changed - For navigation, tab switches
     */
    fun selectionChanged() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.GESTURE_START)
        } else {
            vibratePattern(longArrayOf(0, 15))
        }
    }

    /**
     * Long press - For long press interactions
     */
    fun longPress() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        } else {
            vibratePattern(longArrayOf(0, 40))
        }
    }

    /**
     * Reject - For cancellations, swipe dismissals
     */
    fun reject() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.REJECT)
        } else {
            vibratePattern(longArrayOf(0, 20, 20, 20))
        }
    }

    /**
     * Celebration - For achievements, milestones
     */
    fun celebration() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pattern = longArrayOf(0, 50, 50, 80, 50, 100)
            val amplitudes = intArrayOf(0, 100, 0, 150, 0, 255)
            vibrator?.vibrate(
                VibrationEffect.createWaveform(pattern, amplitudes, -1)
            )
        } else {
            vibratePattern(longArrayOf(0, 50, 50, 80, 50, 100))
        }
    }

    /**
     * Soft pulse - For notifications, gentle reminders
     */
    fun softPulse() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(
                VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            vibratePattern(longArrayOf(0, 25))
        }
    }

    /**
     * Tick - For picker scrolling, incremental changes
     */
    fun tick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
        } else {
            vibratePattern(longArrayOf(0, 5))
        }
    }

    private fun vibratePattern(pattern: LongArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(
                VibrationEffect.createWaveform(pattern, -1)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, -1)
        }
    }
}

/**
 * Composable to remember haptic feedback manager
 */
@Composable
fun rememberHapticFeedback(): HapticFeedbackManager {
    val context = LocalContext.current
    val view = LocalView.current
    return remember(context, view) {
        HapticFeedbackManager(context, view)
    }
}
