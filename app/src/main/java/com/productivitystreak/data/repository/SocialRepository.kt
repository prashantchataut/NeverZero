package com.productivitystreak.data.repository

import com.productivitystreak.ui.state.stats.LeaderboardEntry
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocialRepository @Inject constructor() {

    fun getGlobalLeaderboard(): Flow<List<LeaderboardEntry>> = flow {
        // Simulate network delay
        delay(800)
        
        val mockData = listOf(
            LeaderboardEntry(1, "Alex Chen", 142),
            LeaderboardEntry(2, "Sarah J.", 128),
            LeaderboardEntry(3, "Mike Ross", 115),
            LeaderboardEntry(4, "Emma Watson", 98),
            LeaderboardEntry(5, "You", 45), // Placeholder for current user
            LeaderboardEntry(6, "David K.", 42),
            LeaderboardEntry(7, "Lisa M.", 38),
            LeaderboardEntry(8, "Tom H.", 35),
            LeaderboardEntry(9, "Jenny P.", 30),
            LeaderboardEntry(10, "Robert D.", 28)
        )
        
        emit(mockData)
    }
}
