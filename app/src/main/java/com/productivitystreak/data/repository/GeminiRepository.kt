package com.productivitystreak.data.repository

import android.util.Log
import com.productivitystreak.data.local.entity.UserStats
import com.productivitystreak.data.remote.Content
import com.productivitystreak.data.remote.GeminiRequest
import com.productivitystreak.data.remote.GeminiService
import com.productivitystreak.data.remote.GenerationConfig
import com.productivitystreak.data.remote.Part
import com.productivitystreak.data.remote.SafetySetting
import com.productivitystreak.ui.state.vocabulary.VocabularyWord
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Repository for interacting with the Gemini AI API.
 * Implements the "Digital Ascetic" persona and handles strict JSON parsing.
 */
class GeminiRepository(
    private val apiKey: String
) {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val service: GeminiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/v1beta/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        retrofit.create(GeminiService::class.java)
    }

    /**
     * Generates a personalized daily wisdom quote using the "Digital Ascetic" persona.
     */
    suspend fun getDailyWisdom(userStats: UserStats): RepositoryResult<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext RepositoryResult.PermissionError(Exception("API Key is missing"))
        }

        val systemPrompt = """
            You are The Digital Ascetic. Do not say "I am Buddha." Speak in riddles, stoic imperatives, and short sentences. 
            You are not a friendly assistant; you are a harsh but loving discipline master. 
            Your goal is to make the user act, not chat. 
            If the user says "I am tired," reply with "Fatigue is a feeling. Action is a choice. Protocol requires action."
        """.trimIndent()

        val userContext = """
            User Stats:
            Level: ${userStats.level}
            Discipline: ${userStats.discipline}
            Wisdom: ${userStats.wisdom}
            Strength: ${userStats.strength}
            
            Generate a short, piercing insight for this user today.
        """.trimIndent()

        try {
            val response = service.generateContent(
                apiKey = apiKey,
                request = GeminiRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = systemPrompt + "\n\n" + userContext)))
                    ),
                    safetySettings = defaultSafetySettings
                )
            )

            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
                ?: return@withContext RepositoryResult.UnknownError(Exception("Empty response from AI"))

            RepositoryResult.Success(text)
        } catch (e: Exception) {
            Log.e("GeminiRepository", "Failed to get wisdom", e)
            RepositoryResult.NetworkError(e)
        }
    }

    /**
     * Fetches a vocabulary word. If Intelligence is low (< 5), fetches a complex word to help them improve.
     */
    suspend fun getVocabularyWord(userStats: UserStats): RepositoryResult<VocabularyWord> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext RepositoryResult.PermissionError(Exception("API Key is missing"))
        }

        val intelligenceLevel = userStats.intelligence
        val complexity = if (intelligenceLevel < 5) "complex and sophisticated" else "useful and precise"
        
        val prompt = """
            Generate a single English word that is $complexity.
            Return ONLY a valid JSON object with this exact structure:
            {
                "word": "The Word",
                "definition": "Concise definition",
                "example": "A sentence using the word",
                "type": "noun/verb/adj",
                "pronunciation": "/phonetic/"
            }
        """.trimIndent()

        try {
            val response = service.generateContent(
                apiKey = apiKey,
                request = GeminiRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                    generationConfig = GenerationConfig(responseMimeType = "application/json")
                )
            )

            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
                ?: return@withContext RepositoryResult.UnknownError(Exception("Empty response from AI"))

            val adapter = moshi.adapter(WordResponse::class.java)
            val wordData = adapter.fromJson(jsonText) 
                ?: return@withContext RepositoryResult.UnknownError(Exception("Failed to parse JSON"))

            val domainWord = VocabularyWord(
                word = wordData.word,
                definition = wordData.definition,
                example = wordData.example,
                addedToday = false
            )

            RepositoryResult.Success(domainWord)
        } catch (e: Exception) {
            Log.e("GeminiRepository", "Failed to get word", e)
            RepositoryResult.NetworkError(e)
        }
    }

    private val defaultSafetySettings = listOf(
        SafetySetting("HARM_CATEGORY_HARASSMENT", "BLOCK_MEDIUM_AND_ABOVE"),
        SafetySetting("HARM_CATEGORY_HATE_SPEECH", "BLOCK_MEDIUM_AND_ABOVE"),
        SafetySetting("HARM_CATEGORY_SEXUALLY_EXPLICIT", "BLOCK_MEDIUM_AND_ABOVE"),
        SafetySetting("HARM_CATEGORY_DANGEROUS_CONTENT", "BLOCK_MEDIUM_AND_ABOVE")
    )

    @JsonClass(generateAdapter = true)
    data class WordResponse(
        val word: String,
        val definition: String,
        val example: String,
        val type: String?,
        val pronunciation: String?
    )
}
