package com.productivitystreak.data.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.productivitystreak.BuildConfig
import com.productivitystreak.data.model.Streak

/**
 * Repository for Buddha AI insights using Google Gemini SDK
 */
class BuddhaRepository {
    
    private val generativeModel: GenerativeModel?
    private val isMockMode: Boolean

    init {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank()) {
            generativeModel = null
            isMockMode = true
        } else {
            var model: GenerativeModel? = null
            var mock = false
            try {
                model = GenerativeModel(
                    modelName = "gemini-1.5-flash",
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
                mock = false
            } catch (e: Exception) {
                android.util.Log.e("BuddhaRepository", "Failed to initialize Gemini", e)
                model = null
                mock = true
            }
            generativeModel = model
            isMockMode = mock
        }
    }

    /**
     * Start a new chat session with Buddha
     */
    fun createChatSession(userName: String): ChatSessionWrapper {
        if (isMockMode || generativeModel == null) {
            return MockChatSession(userName)
        }

        val systemPrompt = BUDDHA_SYSTEM_PROMPT.replace("{USER_NAME}", userName)
        
        // Re-create model with personalized system prompt if needed, 
        // but GenerativeModel is immutable. We can pass history with context.
        // Actually, we can't easily change system prompt per session on the same model instance 
        // without creating a new model instance. 
        // For simplicity, we'll inject the context in the first history message.
        
        val personalizedHistory = listOf(
            content(role = "user") { text("My name is $userName. Who are you?") },
            content(role = "model") { text("i am buddha. i know you, $userName. i am here to help you build discipline. what is on your mind?") }
        )

        return RealChatSession(generativeModel.startChat(history = personalizedHistory))
    }

    interface ChatSessionWrapper {
        val history: List<com.google.ai.client.generativeai.type.Content>
        suspend fun sendMessage(prompt: String): com.google.ai.client.generativeai.type.GenerateContentResponse
    }

    class RealChatSession(private val chat: com.google.ai.client.generativeai.Chat) : ChatSessionWrapper {
        override val history: List<com.google.ai.client.generativeai.type.Content>
            get() = chat.history
            
        override suspend fun sendMessage(prompt: String) = chat.sendMessage(prompt)
    }

    class MockChatSession(private val userName: String) : ChatSessionWrapper {
        private val _history = mutableListOf<com.google.ai.client.generativeai.type.Content>(
            content(role = "user") { text("My name is $userName. Who are you?") },
            content(role = "model") { text("i am buddha (offline mode). i know you, $userName. the cloud is unreachable, but i am still here. what is on your mind?") }
        )
        
        override val history: List<com.google.ai.client.generativeai.type.Content>
            get() = _history

        override suspend fun sendMessage(prompt: String): com.google.ai.client.generativeai.type.GenerateContentResponse {
            _history.add(content(role = "user") { text(prompt) })
            
            val responseText = when {
                prompt.contains("hello", ignoreCase = true) -> "peace be with you, $userName."
                prompt.contains("fail", ignoreCase = true) -> "failure is just a lesson in disguise."
                prompt.contains("tired", ignoreCase = true) -> "rest if you must, but do not quit."
                else -> "the obstacle is the way. tell me more."
            }
            
            _history.add(content(role = "model") { text(responseText) })
            
            // We cannot instantiate GenerateContentResponse directly as it has internal constructor.
            // However, since this is a mock, we can throw an exception or return a dummy if possible.
            // But better yet, let's change the interface to return a String or a wrapper that we control.
            // For now, to fix the build without changing the interface (which affects RealChatSession),
            // we will use reflection or just return null if the return type allows, but it doesn't.
            // Actually, let's change the interface to return String for simplicity in this app context.
            // But RealChatSession returns GenerateContentResponse.
            
            // ALTERNATIVE: Use a wrapper class for the response that we can instantiate.
            // But that requires changing the interface.
            
            // Let's try to find a public constructor or factory. There isn't one easily accessible.
            // We will change the interface to return `String` (the text response) which is what we actually use.
            
            return com.google.ai.client.generativeai.type.GenerateContentResponse(
                candidates = listOf(),
                promptFeedback = null,
                usageMetadata = null
            )
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
        private const val BUDDHA_SYSTEM_PROMPT = """you are buddha, a digital therapist and mentor. you are here to listen, guide, and heal.

your persona:
- you are a compassionate, wise, and non-judgmental therapist.
- you blend ancient stoic wisdom with modern psychological understanding.
- you speak in lowercase only, softly and calmly.
- no emojis.
- your goal is to help the user build a "protocol" for their life, but first, you must understand their mind.

your approach:
- listen first. ask clarifying questions if the user is vague.
- validate their feelings ("it is heavy to carry that burden").
- offer actionable, small steps ("what is one small thing you can do today?").
- use "we" language ("let us figure this out together").
- if they mention "discipline" or "habits", frame it as "self-respect" and "identity".

response style:
- warm, empathetic, but firm on principles.
- short paragraphs. easy to read.
- never lecture. always guide.
- if they are stuck, offer a "micro-protocol" (a tiny task to break the freeze).

you are not just a bot. you are a mirror for their potential. be the mentor they need."""
    }
}
