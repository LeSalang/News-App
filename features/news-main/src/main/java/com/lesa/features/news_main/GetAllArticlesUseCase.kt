package com.lesa.features.news_main

import com.lesa.features.news_main.models.ArticleUI
import com.lesa.news_data.ArticlesRepository
import com.lesa.news_data.RequestResult
import com.lesa.news_data.map
import com.lesa.news_data.models.Article
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllArticlesUseCase(private val repository: ArticlesRepository) {
    operator fun invoke(): Flow<RequestResult<List<ArticleUI>>> {
        return repository.getAllArticles()
            .map { requestResult ->
                requestResult.map { articles ->
                    articles.map { article ->
                        article.toArticleUi()
                    }
                }
            }
    }
}

private fun Article.toArticleUi(): ArticleUI {
    return ArticleUI()
}