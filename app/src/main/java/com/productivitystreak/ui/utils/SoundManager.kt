package com.productivitystreak.ui.utils

import android.content.Context
import android.media.AudioManager
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

/**
 * Sound Manager
 * A simple utility to play system UI sounds.
 * This avoids the need for custom assets and ensures consistency with the OS.
 */
class SoundManager(private val view: View) {

    fun playClick() {
        view.playSoundEffect(SoundEffectConstants.CLICK)
    }

    fun playNavigation() {
        view.playSoundEffect(SoundEffectConstants.NAVIGATION_UP) // Often a "whoosh" or distinct click
    }
    
    // Can be expanded with more specific sounds if needed
}

@Composable
fun rememberSoundManager(): SoundManager {
    val view = LocalView.current
    return remember(view) { SoundManager(view) }
}
