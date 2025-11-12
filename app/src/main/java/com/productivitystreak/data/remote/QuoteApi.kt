package com.productivitystreak.data.remote

import com.productivitystreak.data.remote.model.QuoteResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface QuoteApi {
    @GET("random")
    suspend fun getRandomQuote(
        @Query("tags") tags: String? = null
    ): QuoteResponse
}
