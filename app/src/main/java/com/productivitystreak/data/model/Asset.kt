package com.productivitystreak.data.model

enum class AssetCategory {
    PSYCHOLOGY_TRICKS,
    MEMORY_TECHNIQUES,
    NEGOTIATION_SCRIPTS,
    MARKETING_MENTAL_MODELS,
    BOOK_SUMMARIES
}

data class AssetTestQuestion(
    val id: String,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int
)

data class AssetTest(
    val questions: List<AssetTestQuestion>,
    val passingScore: Int
)

data class Asset(
    val id: String,
    val title: String,
    val category: AssetCategory,
    val content: String,
    val test: AssetTest? = null,
    val xpValue: Int = 10,
    val certified: Boolean = false
)
