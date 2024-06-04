package com.lesa.news_data

import com.lesa.database.NewsDatabase
import com.lesa.database.models.ArticleDBO
import com.lesa.news_data.models.Article
import com.lesa.newsapi.NewsApi
import com.lesa.newsapi.models.ArticleDTO
import com.lesa.newsapi.models.ResponseDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach

class ArticlesRepository(
    private val database: NewsDatabase,
    private val api: NewsApi,
) {
    fun getAllArticles(
        requestResponseStrategy: MergeStrategy<RequestResult<List<Article>>> = DefaultMergeStrategy()
    ): Flow<RequestResult<List<Article>>> {
        val cachedAllArticles = getCachedArticles()
            .map { requestResult ->
                requestResult.map { articleDBOList ->
                    articleDBOList.map { articleDBO ->
                        articleDBO.toArticle()
                    }
                }
            }
        val remoteArticles = getRemoteArticles()
            .map { requestResult ->
                requestResult.map { responseDTO ->
                    responseDTO.articles.map { articleDTO ->
                        articleDTO.toArticle()
                    }
                }
            }
        return cachedAllArticles.combine(remoteArticles, requestResponseStrategy::merge)
            .flatMapLatest { requestResult ->
                if (requestResult is RequestResult.Success) {
                    database.articlesDao.observeAllArticles()
                        .map { dbos -> dbos.map { it.toArticle() } }
                        .map { RequestResult.Success(it) }
                } else {
                    flowOf(requestResult)
                }
            }
    }

    private fun getCachedArticles(): Flow<RequestResult<List<ArticleDBO>>> {
        val dbResponse = database.articlesDao::getAllArticles
            .asFlow()
            .map { RequestResult.Success(it) }
        val start = flowOf<RequestResult<List<ArticleDBO>>>(RequestResult.InProgress())
        return merge(dbResponse, start)
    }

    private fun getRemoteArticles(): Flow<RequestResult<ResponseDTO<ArticleDTO>>> {
        val apiResponse = flow { emit(api.getAllArticles()) }
            .onEach { requestResult ->
                if (requestResult.isSuccess) {
                    saveNetResponseToCache(checkNotNull(requestResult.getOrNull()).articles)
                }
            }
            .map { it.toRequestResult() }
        val start = flowOf<RequestResult<ResponseDTO<ArticleDTO>>>(RequestResult.InProgress())
        return merge(apiResponse, start)
    }

    private suspend fun saveNetResponseToCache(data: List<ArticleDTO>) {
        val articleDBOList = data.map { articleDTO -> articleDTO.toArticleDBO() }
        database.articlesDao.insertArticles(articleDBOList)
    }
}