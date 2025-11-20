package com.productivitystreak.data

import com.productivitystreak.data.model.Quote
import com.productivitystreak.data.model.UserContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

class QuoteRepository {
    private val personalizedEngine = PersonalizedQuoteEngine()
    
    private val fallbackQuotes = listOf(
        Quote(
            text = "Discipline is the bridge between goals and accomplishment.",
            author = "Jim Rohn"
        ),
        Quote(
            text = "The best time to plant a tree was 20 years ago. The second best time is now.",
            author = "Chinese Proverb"
        ),
        Quote(
            text = "Great things are done by a series of small things brought together.",
            author = "Vincent van Gogh"
        ),
        Quote(
            text = "Success is the sum of small efforts repeated day in and day out.",
            author = "Robert Collier"
        )
    )

    suspend fun getPersonalizedQuote(userContext: UserContext): Quote = withContext(Dispatchers.IO) {
        try {
            personalizedEngine.generateQuote(userContext)
        } catch (exception: Exception) {
            fallbackQuotes.random(Random(System.currentTimeMillis()))
        }
    }

    // Legacy method for backward compatibility
    @Deprecated("Use getPersonalizedQuote instead", ReplaceWith("getPersonalizedQuote(userContext)"))
    suspend fun getDailyQuote(tags: String? = null): Quote = withContext(Dispatchers.IO) {
        fallbackQuotes.random(Random(System.currentTimeMillis()))
    }
}
