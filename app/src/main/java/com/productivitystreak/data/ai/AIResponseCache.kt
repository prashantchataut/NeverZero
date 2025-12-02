package com.productivitystreak.data.ai

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * In-memory cache for AI responses with TTL (Time To Live)
 * Reduces redundant API calls and improves performance
 */
class AIResponseCache {
    
    private data class CacheEntry<T>(
        val value: T,
        val timestamp: Long,
        val ttlMillis: Long
    ) {
        fun isExpired(): Boolean = System.currentTimeMillis() - timestamp > ttlMillis
    }
    
    private val cache = ConcurrentHashMap<String, CacheEntry<*>>()
    
    /**
     * Get cached value if exists and not expired
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String): T? {
        val entry = cache[key] as? CacheEntry<T> ?: return null
        
        return if (entry.isExpired()) {
            cache.remove(key)
            null
        } else {
            entry.value
        }
    }
    
    /**
     * Store value in cache with TTL
     */
    fun <T> put(key: String, value: T, ttl: Long, unit: TimeUnit) {
        val ttlMillis = unit.toMillis(ttl)
        cache[key] = CacheEntry(value, System.currentTimeMillis(), ttlMillis)
    }
    
    /**
     * Clear specific cache entry
     */
    fun invalidate(key: String) {
        cache.remove(key)
    }
    
    /**
     * Clear all cache entries
     */
    fun clear() {
        cache.clear()
    }
    
    /**
     * Remove all expired entries
     */
    fun cleanup() {
        val expiredKeys = cache.entries
            .filter { (it.value as? CacheEntry<*>)?.isExpired() == true }
            .map { it.key }
        
        expiredKeys.forEach { cache.remove(it) }
    }
    
    /**
     * Get cache statistics
     */
    fun getStats(): CacheStats {
        cleanup() // Clean before counting
        return CacheStats(
            totalEntries = cache.size,
            hitRate = 0.0 // TODO: Track hits/misses for accurate rate
        )
    }
    
    data class CacheStats(
        val totalEntries: Int,
        val hitRate: Double
    )
    
    companion object {
        // Default TTLs for different AI features
        val WORD_OF_DAY_TTL = TimeUnit.HOURS.toMillis(24) // 24 hours
        val BUDDHA_INSIGHT_TTL = TimeUnit.HOURS.toMillis(6) // 6 hours
        val TEACHING_LESSON_TTL = TimeUnit.DAYS.toMillis(7) // 7 days
        val DAILY_BRIEFING_TTL = TimeUnit.HOURS.toMillis(12) // 12 hours
        val HABIT_SUGGESTIONS_TTL = TimeUnit.DAYS.toMillis(1) // 1 day
        
        @Volatile
        private var instance: AIResponseCache? = null
        
        fun getInstance(): AIResponseCache = instance ?: synchronized(this) {
            instance ?: AIResponseCache().also { instance = it }
        }
    }
}
