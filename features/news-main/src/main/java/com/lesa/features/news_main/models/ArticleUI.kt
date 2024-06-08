package com.lesa.features.news_main.models

import com.lesa.news_data.models.Source
import java.util.Date

internal data class ArticleUI(
    val id: Long?,
    val title: String,
    val description: String,
    val urlToImage: String?,
    val url: String?,
)