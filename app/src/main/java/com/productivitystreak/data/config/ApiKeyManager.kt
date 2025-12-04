package com.productivitystreak.data.config

import android.content.Context
import android.util.Log
import com.productivitystreak.BuildConfig
import java.util.Properties

/**
 * Manages API key loading at runtime.
 * Tries to load from assets/api_config.properties first, falls back to BuildConfig.
 */
object ApiKeyManager {
    private const val TAG = "ApiKeyManager"
    private const val ASSETS_FILE = "api_config.properties"
    private const val KEY_NAME = "GEMINI_API_KEY"
    
    @Volatile
    private var cachedKey: String? = null
    
    /**
     * Get the Gemini API key.
     * First tries to load from assets, then falls back to BuildConfig.
     * The key is cached after first load.
     */
    fun getApiKey(context: Context): String {
        // Return cached key if available
        cachedKey?.let { return it }
        
        // Try loading from assets first
        val assetsKey = loadFromAssets(context)
        if (!assetsKey.isNullOrBlank()) {
            Log.d(TAG, "Loaded API key from assets (length: ${assetsKey.length})")
            cachedKey = assetsKey
            return assetsKey
        }
        
        // Fall back to BuildConfig
        val buildConfigKey = BuildConfig.GEMINI_API_KEY
        if (!buildConfigKey.isNullOrBlank()) {
            Log.d(TAG, "Using API key from BuildConfig (length: ${buildConfigKey.length})")
            cachedKey = buildConfigKey
            return buildConfigKey
        }
        
        // No key found
        Log.w(TAG, "No API key found in assets or BuildConfig")
        cachedKey = ""
        return ""
    }
    
    /**
     * Load API key from assets/api_config.properties
     */
    private fun loadFromAssets(context: Context): String? {
        return try {
            context.assets.open(ASSETS_FILE).use { stream ->
                val properties = Properties()
                properties.load(stream)
                properties.getProperty(KEY_NAME)?.trim()
            }
        } catch (e: Exception) {
            Log.d(TAG, "Could not load from assets: ${e.message}")
            null
        }
    }
    
    /**
     * Clear the cached key (useful for testing)
     */
    fun clearCache() {
        cachedKey = null
    }
}
