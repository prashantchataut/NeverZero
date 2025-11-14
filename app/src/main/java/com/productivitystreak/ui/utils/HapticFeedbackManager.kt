package com.productivitystreak.ui.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.content.ContextCompat
import com.productivitystreak.data.local.PreferencesManager
import kotlinx.coroutines.flow.first
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Centralised helper for triggering device haptic feedback patterns.
 *
 * Key goals:
 *  - Guard every vibration behind runtime permission checks.
 *  - Allow callers to respect the user preference surfaced via [PreferencesManager].
 *  - Offer a handful of expressive presets that cover the product use-cases.
 */
class HapticFeedbackManager private constructor(private val context: Context) {

    private val hapticsEnabledCache = AtomicBoolean(true)

    private val vibrator: Vibrator?
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(VibratorManager::class.java)
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

    /**
     * Refresh the cached toggle by reading from DataStore. Should be called whenever the
     * user preference changes (e.g. from ViewModel or on app start).
     */
    suspend fun syncPreference(preferencesManager: PreferencesManager) {
        hapticsEnabledCache.set(preferencesManager.hapticFeedbackEnabled.first())
    }

    /**
     * Directly override the cached toggle. Useful when the preference is already in memory.
     */
    fun setEnabled(enabled: Boolean) {
        hapticsEnabledCache.set(enabled)
    }

    fun celebration(forceEnabled: Boolean? = null) {
        performHaptic(HapticPreset.CELEBRATION, forceEnabled)
    }

    fun success(forceEnabled: Boolean? = null) {
        performHaptic(HapticPreset.SUCCESS, forceEnabled)
    }

    fun error(forceEnabled: Boolean? = null) {
        performHaptic(HapticPreset.ERROR, forceEnabled)
    }

    fun light(forceEnabled: Boolean? = null) {
        performHaptic(HapticPreset.LIGHT, forceEnabled)
    }

    fun selection(forceEnabled: Boolean? = null) {
        performHaptic(HapticPreset.SELECTION, forceEnabled)
    }

    private fun performHaptic(preset: HapticPreset, forceEnabled: Boolean?) {
        val shouldVibrate = forceEnabled ?: hapticsEnabledCache.get()
        if (!shouldVibrate || !canVibrate()) return

        val targetVibrator = vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createWaveform(
                preset.timings,
                preset.amplitudes,
                /* repeat */ -1
            )
            targetVibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            targetVibrator.vibrate(preset.timings, -1)
        }
    }

    private fun canVibrate(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.VIBRATE
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return false
        }
        return vibrator?.hasVibrator() == true
    }

    private enum class HapticPreset(val timings: LongArray, val amplitudes: IntArray) {
        CELEBRATION(
            longArrayOf(0, 40, 60, 40, 80, 120),
            intArrayOf(0, 180, 0, 220, 0, 255)
        ),
        SUCCESS(
            longArrayOf(0, 35, 50, 55),
            intArrayOf(0, 180, 0, 200)
        ),
        ERROR(
            longArrayOf(0, 30, 40, 130),
            intArrayOf(0, 255, 0, 200)
        ),
        LIGHT(
            longArrayOf(0, 25),
            intArrayOf(0, 150)
        ),
        SELECTION(
            longArrayOf(0, 20, 40, 25),
            intArrayOf(0, 120, 0, 160)
        )
    }

    companion object {
        @Volatile
        private var instance: HapticFeedbackManager? = null

        fun getInstance(context: Context): HapticFeedbackManager {
            return instance ?: synchronized(this) {
                instance ?: HapticFeedbackManager(context.applicationContext).also { instance = it }
            }
        }
    }
}

/** Shortcut extension so any [Context] can grab the singleton manager easily. */
fun Context.hapticFeedbackManager(): HapticFeedbackManager =
    HapticFeedbackManager.getInstance(this)
