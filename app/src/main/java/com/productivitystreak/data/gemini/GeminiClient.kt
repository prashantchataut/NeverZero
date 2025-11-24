package com.productivitystreak.data.gemini

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.productivitystreak.BuildConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@JsonClass(generateAdapter = true)
data class WordOfTheDayResponse(
    val word: String,
    val definition: String,
    val example: String,
    val type: String? = null,
    val pronunciation: String? = null
)

@JsonClass(generateAdapter = true)
data class TeachingPlanResponse(
    val word: String,
    val definition: String,
    val analogy: String?,
    val mnemonic: String?,
    val example: String?,
    val practice: List<String>?
)

data class TeachingLesson(
    val word: String,
    val definition: String,
    val analogy: String,
    val mnemonic: String,
    val example: String,
    val practicePrompts: List<String>
)

class GeminiClient private constructor() {

    private val model: GenerativeModel? = BuildConfig.GEMINI_API_KEY.takeIf { it.isNotBlank() }?.let { key ->
        Log.d(TAG, "Gemini API initialized successfully")
        GenerativeModel(
            modelName = MODEL_NAME,
            apiKey = key
        )
    } ?: run {
        Log.w(TAG, "Gemini API key not configured - AI features will use fallback responses")
        null
    }

    suspend fun generateTeachingLesson(word: String, learnerContext: String?): TeachingLesson = withContext(Dispatchers.IO) {
        val fallback = fallbackTeachingLesson(word)
        val generativeModel = model ?: return@withContext fallback
        val focus = learnerContext?.takeIf { it.isNotBlank() } ?: "general mastery"
        val prompt = """
            You are a high-agency vocabulary coach. Teach the word "$word" to a busy professional who cares about $focus.
            Respond with ONLY valid JSON:
            {
              "word": "...",
              "definition": "short, modern explanation",
              "analogy": "memorable comparison or metaphor",
              "mnemonic": "a sticky memory trick",
              "example": "a vivid sentence",
              "practice": ["micro exercise 1", "micro exercise 2"]
            }
            Keep language mature and pragmatic. No markdown.
        """.trimIndent()

        return@withContext try {
            val response = generativeModel.generateContent(content { text(prompt) })
            val json = response.text?.cleanJsonBlock() ?: return@withContext fallback
            val adapter = moshi.adapter(TeachingPlanResponse::class.java)
            val plan = adapter.fromJson(json) ?: return@withContext fallback
            TeachingLesson(
                word = plan.word.ifBlank { word },
                definition = plan.definition.trim(),
                analogy = plan.analogy?.trim().takeUnless { it.isNullOrEmpty() }
                    ?: "Imagine $word as a strategic lever you pull to shift the situation in your favor.",
                mnemonic = plan.mnemonic?.trim().takeUnless { it.isNullOrEmpty() }
                    ?: "Think '$word' = '${word.take(3).uppercase()} advantage'.",
                example = plan.example?.trim().takeUnless { it.isNullOrEmpty() }
                    ?: "${word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }} became the anchor of her weekly briefing.",
                practicePrompts = plan.practice?.takeIf { it.isNotEmpty() }
                    ?: listOf(
                        "Use '$word' in a sentence that describes today's priority.",
                        "Teach '$word' to a teammate in under 30 seconds."
                    )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate teaching lesson", e)
            fallback
        }
    }

    private fun fallbackTeachingLesson(word: String) = TeachingLesson(
        word = word,
        definition = "Use '$word' to describe deliberate, precise action—it's the opposite of winging it.",
        analogy = "Think of '$word' as the zoom lens you twist to bring an idea into crisp focus.",
        mnemonic = "Tie '$word' to '${word.take(3).uppercase()}': three letters, three beats to remember it.",
        example = "She chose '$word' framing so the client could internalize the concept instantly.",
        practicePrompts = listOf(
            "Write a two-sentence lesson using '$word' for a junior teammate.",
            "Record a 15-second voice note where you apply '$word' to your current project."
        )
    )

    private fun String.cleanJsonBlock(): String = this.trim()
        .removePrefix("```json")
        .removePrefix("```")
        .removeSuffix("```")
        .trim()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    suspend fun generateMotivationPrompt(prompt: String): String = withContext(Dispatchers.IO) {
        val generativeModel = model ?: return@withContext "Keep going! You're doing great."
        val response = generativeModel.generateContent(content { text(prompt) })
        response.text?.trim().takeUnless { it.isNullOrEmpty() } ?: "Keep going! You're doing great."
    }

    suspend fun generateHabitSuggestions(interests: String): List<String> = withContext(Dispatchers.IO) {
        val generativeModel = model ?: return@withContext emptyList()
        val prompt = "Suggest 5 simple, daily habits for someone interested in: $interests. Format as a simple list, one per line, no numbering or bullets."
        try {
            val response = generativeModel.generateContent(content { text(prompt) })
            response.text?.lines()
                ?.map { it.trim().removePrefix("- ").removePrefix("* ").trim() }
                ?.filter { it.isNotBlank() }
                ?.take(5)
                ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun generateWordOfTheDay(interests: String = "general knowledge"): com.productivitystreak.ui.state.vocabulary.VocabularyWord? = withContext(Dispatchers.IO) {
        val generativeModel = model ?: return@withContext null
        val prompt = """
            Generate a unique, sophisticated 'Word of the Day' for someone interested in $interests.
            Return ONLY a valid JSON object with the following fields:
            {
                "word": "The Word",
                "definition": "A concise definition.",
                "example": "A sentence using the word.",
                "type": "noun/verb/adjective",
                "pronunciation": "/phonetic/"
            }
            Do not include markdown formatting like ```json. Just the raw JSON string.
        """.trimIndent()

        try {
            val response = generativeModel.generateContent(content { text(prompt) })
            val json = response.text?.trim()
                ?.removePrefix("```json")
                ?.removePrefix("```")
                ?.removeSuffix("```")
                ?.trim() 
                ?: return@withContext null
            
            // Use Moshi for proper JSON parsing
            val adapter = moshi.adapter(WordOfTheDayResponse::class.java)
            val wordResponse = adapter.fromJson(json) ?: return@withContext null
            
            com.productivitystreak.ui.state.vocabulary.VocabularyWord(
                word = wordResponse.word,
                definition = wordResponse.definition,
                example = wordResponse.example,
                addedToday = false
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate word of the day", e)
            null
        }
    }

    suspend fun generateBuddhaInsight(context: String = "general"): String = withContext(Dispatchers.IO) {
        val generativeModel = model ?: return@withContext "The obstacle is the way."
        val prompt = "Give me a short, profound, stoic or buddhist insight about $context. Max 20 words. No quotes."
        try {
            val response = generativeModel.generateContent(content { text(prompt) })
            response.text?.trim() ?: "The obstacle is the way."
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate Buddha insight", e)
            "The obstacle is the way."
        }
    }

    companion object {
        private const val TAG = "GeminiClient"
        private const val MODEL_NAME = "models/gemini-2.5-flash"

        @Volatile
        private var instance: GeminiClient? = null

        fun getInstance(): GeminiClient = instance ?: synchronized(this) {
            instance ?: GeminiClient().also { instance = it }
        }
    }
}
