package com.productivitystreak.data.ai

import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

/**
 * Rate limiter for AI API calls
 * Prevents excessive API usage and manages quotas
 */
class RateLimiter(
    private val maxRequestsPerMinute: Int = 10,
    private val maxRequestsPerHour: Int = 100
) {
    private val requestTimestamps = ConcurrentLinkedQueue<Long>()
    private val requestCount = AtomicInteger(0)
    
    /**
     * Check if request is allowed and wait if necessary
     * @return true if allowed, false if quota exceeded
     */
    suspend fun acquirePermit(): Boolean {
        val now = System.currentTimeMillis()
        
        // Clean up old timestamps (older than 1 hour)
        val oneHourAgo = now - HOUR_IN_MILLIS
        while (requestTimestamps.peek()?.let { it < oneHourAgo } == true) {
            requestTimestamps.poll()
        }
        
        // Check hourly limit
        if (requestTimestamps.size >= maxRequestsPerHour) {
            return false // Quota exceeded
        }
        
        // Check per-minute limit
        val oneMinuteAgo = now - MINUTE_IN_MILLIS
        val recentRequests = requestTimestamps.count { it > oneMinuteAgo }
        
        if (recentRequests >= maxRequestsPerMinute) {
            // Wait until we can make another request
            val oldestRecentRequest = requestTimestamps.first { it > oneMinuteAgo }
            val waitTime = MINUTE_IN_MILLIS - (now - oldestRecentRequest)
            
            if (waitTime > 0) {
                delay(waitTime)
            }
        }
        
        // Add current request
        requestTimestamps.offer(now)
        requestCount.incrementAndGet()
        
        return true
    }
    
    /**
     * Get current usage statistics
     */
    fun getUsageStats(): UsageStats {
        val now = System.currentTimeMillis()
        val oneMinuteAgo = now - MINUTE_IN_MILLIS
        val oneHourAgo = now - HOUR_IN_MILLIS
        
        val requestsLastMinute = requestTimestamps.count { it > oneMinuteAgo }
        val requestsLastHour = requestTimestamps.count { it > oneHourAgo }
        
        return UsageStats(
            requestsLastMinute = requestsLastMinute,
            requestsLastHour = requestsLastHour,
            totalRequests = requestCount.get(),
            minuteQuotaRemaining = maxRequestsPerMinute - requestsLastMinute,
            hourQuotaRemaining = maxRequestsPerHour - requestsLastHour
        )
    }
    
    /**
     * Reset all counters
     */
    fun reset() {
        requestTimestamps.clear()
        requestCount.set(0)
    }
    
    data class UsageStats(
        val requestsLastMinute: Int,
        val requestsLastHour: Int,
        val totalRequests: Int,
        val minuteQuotaRemaining: Int,
        val hourQuotaRemaining: Int
    )
    
    companion object {
        private const val MINUTE_IN_MILLIS = 60_000L
        private const val HOUR_IN_MILLIS = 3_600_000L
        
        @Volatile
        private var instance: RateLimiter? = null
        
        fun getInstance(): RateLimiter = instance ?: synchronized(this) {
            instance ?: RateLimiter().also { instance = it }
        }
    }
}
