package com.productivitystreak.data.repository

import com.productivitystreak.data.local.dao.StreakDao
import com.productivitystreak.data.local.entity.StreakEntity
import com.productivitystreak.data.model.Streak
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class StreakRepositoryTest {

    @Mock
    private lateinit var streakDao: StreakDao

    private lateinit var streakRepository: StreakRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        streakRepository = StreakRepository(streakDao)
    }

    @Test
    fun `observeStreaks should map entities to streaks`() = runBlocking {
        // Given
        val streakEntities = listOf(
            StreakEntity(
                id = "test-id",
                name = "Test Streak",
                currentCount = 5,
                longestCount = 10,
                goalPerDay = 1,
                unit = "times",
                category = "test",
                history = emptyList(),
                color = "#FF0000",
                icon = "test"
            )
        )
        `when`(streakDao.getAllStreaks()).thenReturn(flowOf(streakEntities))

        // When
        val result = streakRepository.observeStreaks()

        // Then
        result.collect { streaks ->
            assertEquals(1, streaks.size)
            assertEquals("test-id", streaks[0].id)
            assertEquals("Test Streak", streaks[0].name)
            assertEquals(5, streaks[0].currentCount)
        }
    }

    @Test
    fun `createStreak should insert new streak entity`() = runBlocking {
        // Given
        val name = "New Streak"
        val goalPerDay = 2
        val unit = "pages"
        val category = "Reading"
        val color = "#00FF00"
        val icon = "book"

        // When
        val result = streakRepository.createStreak(name, goalPerDay, unit, category, color, icon)

        // Then
        assertTrue(result is RepositoryResult.Success)
        verify(streakDao).insertStreak(any<StreakEntity>())
    }
}