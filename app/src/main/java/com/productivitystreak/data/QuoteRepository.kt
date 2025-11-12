package com.productivitystreak.data

import com.productivitystreak.data.model.Quote
import com.productivitystreak.data.remote.QuoteService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

class QuoteRepository {
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

    suspend fun getDailyQuote(tags: String? = null): Quote = withContext(Dispatchers.IO) {
        try {
            val response = QuoteService.api.getRandomQuote(tags)
            Quote(text = response.content, author = response.author)
        } catch (exception: Exception) {
            fallbackQuotes.random(Random(System.currentTimeMillis()))
        }
    }
}
