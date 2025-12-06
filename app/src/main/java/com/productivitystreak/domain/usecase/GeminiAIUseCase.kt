package com.productivitystreak.domain.usecase

import android.util.Log
import com.productivitystreak.data.gemini.GeminiClient

/**
 * Shared use case for Gemini AI integration.
 * Standardizes error handling and loading states across ViewModels.
 */
class GeminiAIUseCase(private val geminiClient: GeminiClient) {

    suspend fun generateWithErrorHandling(
        operation: suspend () -> String
    ): Result<String> {
        return try {
            val result = operation()
            Result.success(result)
        } catch (e: Exception) {
            Log.e("GeminiAIUseCase", "Error generating AI content", e)
            Result.failure(e)
        }
    }

    fun handleAIError(error: Throwable): String {
        return when {
            error.message?.contains("API key", ignoreCase = true) == true -> "Invalid API key"
            error.message?.contains("quota", ignoreCase = true) == true -> "API quota exceeded"
            error.message?.contains("network", ignoreCase = true) == true -> "Network error"
            error.message?.contains("timeout", ignoreCase = true) == true -> "Request timeout"
            else -> "Connection weak, meditating..."
        }
    }

    suspend fun generateBuddhaInsight(forceRefresh: Boolean = false): String {
        return generateWithErrorHandling {
            geminiClient.generateBuddhaInsight(forceRefresh)
        }.getOrElse { handleAIError(it) }
    }

    suspend fun generateJournalFeedback(text: String): String {
        return generateWithErrorHandling {
            geminiClient.generateJournalFeedback(text)
        }.getOrElse { handleAIError(it) }
    }

    suspend fun generateHabitSuggestions(categories: String): List<String> {
        return generateWithErrorHandling {
            geminiClient.generateHabitSuggestions(categories).joinToString()
        }.map { it.split(",").map { s -> s.trim() } }.getOrElse { emptyList() }
    }
}
