package com.lesa.features.news_main

import com.lesa.features.news_main.models.ArticleUI
import com.lesa.features.news_main.models.SourceUI
import com.lesa.news_data.ArticlesRepository
import com.lesa.news_data.RequestResult
import com.lesa.news_data.map
import com.lesa.news_data.models.Article
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ViewModelScoped
internal class GetAllArticlesUseCase @Inject constructor(
    private val repository: ArticlesRepository,
) {
    operator fun invoke(query: String): Flow<RequestResult<List<ArticleUI>>> {
        return repository.getAllArticles(query = query)
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
    return ArticleUI(
        id = id,
        title = title ?: NO_TITLE,
        description = description ?: NO_DESCRIPTION,
        url = url,
        urlToImage = urlToImage,
    )
}

private const val NO_TITLE = "No title"
private const val NO_DESCRIPTION = "No description"