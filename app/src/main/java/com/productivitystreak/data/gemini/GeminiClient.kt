package com.productivitystreak.data.gemini

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.productivitystreak.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiClient private constructor() {

    private val model: GenerativeModel? = BuildConfig.GEMINI_API_KEY.takeIf { it.isNotBlank() }?.let { key ->
        GenerativeModel(
            modelName = MODEL_NAME,
            apiKey = key
        )
    }

    suspend fun generateMotivationPrompt(prompt: String): String = withContext(Dispatchers.IO) {
        val generativeModel = model ?: return@withContext "Keep going! You're doing great."
        val response = generativeModel.generateContent(content { text(prompt) })
        response.text?.trim().takeUnless { it.isNullOrEmpty() } ?: "Keep going! You're doing great."
    }

    companion object {
        private const val MODEL_NAME = "models/gemini-2.5-flash"

        @Volatile
        private var instance: GeminiClient? = null

        fun getInstance(): GeminiClient = instance ?: synchronized(this) {
            instance ?: GeminiClient().also { instance = it }
        }
    }
}
