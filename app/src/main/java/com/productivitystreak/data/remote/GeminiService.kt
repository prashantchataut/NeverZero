package com.productivitystreak.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Retrofit service for the Gemini API.
 * Base URL: https://generativelanguage.googleapis.com/v1beta/
 */
interface GeminiService {

    @POST("models/gemini-1.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

// --- Request Models ---

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<Content>,
    val safetySettings: List<SafetySetting>? = null,
    val generationConfig: GenerationConfig? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>,
    val role: String = "user"
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String
)

@JsonClass(generateAdapter = true)
data class SafetySetting(
    val category: String,
    val threshold: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val temperature: Float? = null,
    val topK: Int? = null,
    val topP: Float? = null,
    val maxOutputTokens: Int? = null,
    @Json(name = "response_mime_type") val responseMimeType: String? = null
)

// --- Response Models ---

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<Candidate>? = null,
    val promptFeedback: PromptFeedback? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content?,
    val finishReason: String?,
    val index: Int?
)

@JsonClass(generateAdapter = true)
data class PromptFeedback(
    val blockReason: String?
)
