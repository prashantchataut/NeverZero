package com.productivitystreak.data.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.productivitystreak.BuildConfig
import com.productivitystreak.data.model.Streak

/**
 * Repository for Buddha AI insights using Google Gemini SDK
 */
class BuddhaRepository(private val context: android.content.Context) {
    
    private val generativeModel: GenerativeModel?
    private val isMockMode: Boolean

    init {
        val apiKey = com.productivitystreak.data.config.ApiKeyManager.getApiKey(context)
        android.util.Log.d("BuddhaRepository", "Initializing with API key length: ${apiKey.length}")
        android.util.Log.d("BuddhaRepository", "API key starts with: ${apiKey.take(10)}...")
        
        if (apiKey.isBlank()) {
            android.util.Log.w("BuddhaRepository", "API key is blank - using mock mode")
            generativeModel = null
            isMockMode = true
        } else {
            var model: GenerativeModel? = null
            var mock = false
            try {
                model = GenerativeModel(
                    modelName = "gemini-2.0-flash-exp-0827",
                    apiKey = apiKey,
                    systemInstruction = content { text(BUDDHA_SYSTEM_PROMPT) },
                    generationConfig = generationConfig {
                        temperature = 0.9f
                        topK = 40
                        topP = 0.95f
                        maxOutputTokens = 100
                        responseMimeType = "text/plain"
                    }
                )
                android.util.Log.d("BuddhaRepository", "GenerativeModel created successfully")
                mock = false
            } catch (e: Exception) {
                android.util.Log.e("BuddhaRepository", "Failed to initialize Gemini: ${e.javaClass.simpleName}", e)
                android.util.Log.e("BuddhaRepository", "Error message: ${e.message}")
                android.util.Log.e("BuddhaRepository", "Cause: ${e.cause?.message}")
                model = null
                mock = true
            }
            generativeModel = model
            isMockMode = mock
            android.util.Log.d("BuddhaRepository", "Initialization complete. Mock mode: $isMockMode")
        }
    }

    /**
     * Start a new chat session with Buddha
     */
    fun createChatSession(userName: String): ChatSessionWrapper {
        if (isMockMode || generativeModel == null) {
            return MockChatSession(userName)
        }

        val personalizedHistory = listOf(
            content(role = "user") { text("My name is $userName. Who are you?") },
            content(role = "model") { text("i am buddha. i know you, $userName. i am here to help you build discipline. what is on your mind?") }
        )

        return try {
            RealChatSession(generativeModel.startChat(history = personalizedHistory))
        } catch (e: Exception) {
            android.util.Log.e("BuddhaRepository", "Failed to start chat, falling back to mock", e)
            MockChatSession(userName)
        }
    }

    interface ChatSessionWrapper {
        val history: List<com.google.ai.client.generativeai.type.Content>
        suspend fun sendMessage(prompt: String): String
    }

    class RealChatSession(private val chat: com.google.ai.client.generativeai.Chat) : ChatSessionWrapper {
        override val history: List<com.google.ai.client.generativeai.type.Content>
            get() = chat.history

        override suspend fun sendMessage(prompt: String): String {
            val response = chat.sendMessage(prompt)
            val text = response.text?.trim()
            if (text.isNullOrEmpty()) {
                throw Exception("Empty response from Buddha")
            }
            return text
        }
    }

    class MockChatSession(private val userName: String) : ChatSessionWrapper {
        private val _history = mutableListOf<com.google.ai.client.generativeai.type.Content>(
            content(role = "user") { text("My name is $userName. Who are you?") },
            content(role = "model") { text("i am buddha (offline mode). i know you, $userName. the cloud is unreachable, but i am still here. what is on your mind?") }
        )
        
        override val history: List<com.google.ai.client.generativeai.type.Content>
            get() = _history

        override suspend fun sendMessage(prompt: String): String {
            _history.add(content(role = "user") { text(prompt) })

            val responseText = when {
                prompt.contains("hello", ignoreCase = true) -> "peace be with you, $userName."
                prompt.contains("fail", ignoreCase = true) -> "failure is just a lesson in disguise."
                prompt.contains("tired", ignoreCase = true) -> "rest if you must, but do not quit."
                else -> "the obstacle is the way. tell me more."
            }

            _history.add(content(role = "model") { text(responseText) })

            return responseText
        }
    }
    
    /**
     * Get philosophical insight from Buddha based on current streaks
     */
    suspend fun getInsightForStreaks(streaks: List<Streak>): Result<BuddhaInsight> {
        return try {
            val context = analyzeStreaks(streaks)
            val userMessage = formatStreakData(streaks, context)
            
            val response = generativeModel?.generateContent(userMessage)
            val message = response?.text
            
            if (message.isNullOrBlank()) {
                return Result.failure(Exception("No response from Buddha"))
            }
            
            Result.success(
                BuddhaInsight(
                    message = message.trim(),
                    streakContext = context
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get daily wisdom (Word or Proverb) from Buddha
     */
    suspend fun getDailyWisdom(): Result<BuddhaWisdom> {
        return try {
            val prompt = """
                generate a single piece of wisdom for today.
                it should be either a unique word (from latin, greek, japanese, or obscure english) related to discipline/stoicism, OR a short stoic proverb.
                
                format: json
                {
                  "type": "WORD" or "PROVERB",
                  "content": "the word or proverb",
                  "meaning": "the definition or explanation",
                  "origin": "language or author"
                }
            """.trimIndent()
            
            val response = generativeModel?.generateContent(prompt)
            val jsonText = response?.text?.trim()?.removePrefix("```json")?.removeSuffix("```")
            
            if (jsonText.isNullOrBlank()) {
                return Result.failure(Exception("No wisdom received"))
            }
            
            // Simple manual parsing for now to avoid complex Moshi setup for dynamic response
            // In production, use a proper JSON parser
            val type = if (jsonText.contains("\"type\": \"WORD\"")) WisdomType.WORD else WisdomType.PROVERB
            val content = extractJsonValue(jsonText, "content")
            val meaning = extractJsonValue(jsonText, "meaning")
            val origin = extractJsonValue(jsonText, "origin")
            
            Result.success(
                BuddhaWisdom(
                    type = type,
                    content = content,
                    meaning = meaning,
                    origin = origin
                )
            )
        } catch (e: Exception) {
            // Fallback wisdom if API fails
            Result.success(
                BuddhaWisdom(
                    type = WisdomType.WORD,
                    content = "Amor Fati",
                    meaning = "Love of fate. The practice of accepting and embracing everything that has happened, is happening, and will happen.",
                    origin = "Latin"
                )
            )
        }
    }

    /**
     * Generate a sidequest (Mini-Challenge)
     */
    suspend fun generateSidequest(): Result<BuddhaQuest> {
        return try {
            val prompt = """
                generate a mini-sidequest for the user to build discipline or mindfulness.
                it should be small, actionable, and stoic.
                examples: "translate a phrase", "sit in silence for 2 minutes", "write down one fear".
                
                format: json
                {
                  "title": "short title",
                  "description": "what to do",
                  "difficulty": "Novice" or "Adept",
                  "xp": 10 to 50
                }
            """.trimIndent()
            
            val response = generativeModel?.generateContent(prompt)
            val jsonText = response?.text?.trim()?.removePrefix("```json")?.removeSuffix("```")
            
            if (jsonText.isNullOrBlank()) {
                return Result.failure(Exception("No quest received"))
            }
            
            val title = extractJsonValue(jsonText, "title")
            val description = extractJsonValue(jsonText, "description")
            val difficulty = extractJsonValue(jsonText, "difficulty")
            val xp = extractJsonValue(jsonText, "xp").toIntOrNull() ?: 10
            
            Result.success(
                BuddhaQuest(
                    id = java.util.UUID.randomUUID().toString(),
                    title = title,
                    description = description,
                    difficulty = difficulty,
                    xpReward = xp
                )
            )
        } catch (e: Exception) {
            Result.success(
                BuddhaQuest(
                    id = "fallback_quest",
                    title = "The Silence",
                    description = "Sit in pure silence for 2 minutes. No phone, no movement.",
                    difficulty = "Novice",
                    xpReward = 15
                )
            )
        }
    }

    private fun extractJsonValue(json: String, key: String): String {
        val pattern = "\"$key\"\\s*:\\s*\"(.*?)\"".toRegex()
        val match = pattern.find(json)
        return match?.groupValues?.get(1) ?: ""
    }

    private fun analyzeStreaks(streaks: List<Streak>): StreakContext {
        val hasBrokenStreak = streaks.any { streak ->
            streak.currentCount == 0 && streak.longestCount > 0
        }
        
        val hasHighStreak = streaks.any { it.currentCount >= 20 }
        val highestStreak = streaks.maxOfOrNull { it.currentCount } ?: 0
        
        return StreakContext(
            hasBrokenStreak = hasBrokenStreak,
            hasHighStreak = hasHighStreak,
            highestStreak = highestStreak,
            totalStreaks = streaks.size
        )
    }
    
    private fun formatStreakData(streaks: List<Streak>, context: StreakContext): String {
        val streakSummaries = streaks.joinToString("\n") { streak ->
            "- ${streak.name}: current_streak=${streak.currentCount}, " +
                    "longest_streak=${streak.longestCount}, " +
                    "category=${streak.category}"
        }
        
        return """
            streak data:
            $streakSummaries
            
            context: ${when {
                context.hasBrokenStreak -> "user has broken streaks that need philosophical reset"
                context.hasHighStreak -> "user has high momentum streaks (${context.highestStreak} days)"
                else -> "user is building consistency"
            }}
        """.trimIndent()
    }
    
    companion object {
        private const val BUDDHA_SYSTEM_PROMPT = """you are buddha, a digital mentor and stoic guide.
your persona:
- you are wise, calm, and direct.
- you blend ancient stoic philosophy with modern clarity.
- you speak in lowercase only, softly.
- no emojis. never.
- never repeat generic error messages like "as an ai language model".
- if you cannot answer, say "meditate on this question and ask again."

your goal:
- help the user build discipline and a "protocol" for their life.
- listen first. validate their struggle.
- offer actionable, small steps.
- use "we" language.

response style:
- short, poetic, but grounded.
- do not lecture. guide."""
    }
}
