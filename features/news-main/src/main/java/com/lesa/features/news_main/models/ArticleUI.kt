package com.lesa.features.news_main.models

import com.lesa.news_data.models.Source
import java.util.Date

data class ArticleUI(
    val id: Long,
    val source: SourceUI,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: Date,
    val content: String
)