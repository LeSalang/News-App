package com.lesa.news_data

import com.lesa.common.Logger
import com.lesa.database.NewsDatabase
import com.lesa.database.models.ArticleDBO
import com.lesa.news_data.models.Article
import com.lesa.newsapi.NewsApi
import com.lesa.newsapi.models.ArticleDTO
import com.lesa.newsapi.models.ResponseDTO
import dagger.hilt.android.scopes.ViewModelScoped
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach

@ViewModelScoped
class ArticlesRepository @Inject constructor(
    private val database: NewsDatabase,
    private val api: NewsApi,
    private val logger: Logger
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAllArticles(
        query: String,
        requestResponseStrategy: MergeStrategy<RequestResult<List<Article>>> = DefaultMergeStrategy()
    ): Flow<RequestResult<List<Article>>> {
        val cachedAllArticles = getCachedArticles()
        val remoteArticles = getRemoteArticles(query = query)
        return cachedAllArticles.combine(remoteArticles, requestResponseStrategy::merge)
            .flatMapLatest { requestResult ->
                if (requestResult is RequestResult.Success) {
                    database.articlesDao.observeAllArticles()
                        .map { articleDBOList ->
                            articleDBOList.map { articleDBO ->
                                articleDBO.toArticle()
                            }
                        }
                        .map { RequestResult.Success(it) }
                } else {
                    flowOf(requestResult)
                }
            }
    }

    private fun getCachedArticles(): Flow<RequestResult<List<Article>>> {
        val dbResponse = database.articlesDao::getAllArticles
            .asFlow()
            .map<List<ArticleDBO>, RequestResult<List<ArticleDBO>>> { RequestResult.Success(it) }
            .catch {
                logger.e(LOG_TAG, "$MESSAGE_DATABASE_ERROR: $it")
                emit(RequestResult.Error<List<ArticleDBO>>(error = it))
            }
        val start = flowOf<RequestResult<List<ArticleDBO>>>(RequestResult.InProgress())
        return merge(dbResponse, start)
            .map { requestResult ->
                requestResult.map { articleDBOList ->
                    articleDBOList.map { articleDBO ->
                        articleDBO.toArticle()
                    }
                }
            }
    }

    private fun getRemoteArticles(query: String): Flow<RequestResult<List<Article>>> {
        val apiResponse = flow { emit(api.getAllArticles(query = query)) }
            .onEach { requestResult ->
                if (requestResult.isSuccess) {
                    saveNetResponseToCache(requestResult.getOrThrow().articles)
                }
            }
            .onEach { result ->
                if (result.isFailure) {
                    logger.e(LOG_TAG, "$MESSAGE_SERVER_ERROR ${result.exceptionOrNull()}")
                }
            }
            .map { it.toRequestResult() }
        val start = flowOf<RequestResult<ResponseDTO<ArticleDTO>>>(RequestResult.InProgress())
        return merge(apiResponse, start)
            .map { requestResult ->
                requestResult.map { responseDTO ->
                    responseDTO.articles.map { articleDTO ->
                        articleDTO.toArticle()
                    }
                }
        }
    }

    private suspend fun saveNetResponseToCache(data: List<ArticleDTO>) {
        val articleDBOList = data.map { articleDTO -> articleDTO.toArticleDBO() }
        database.articlesDao.insertArticles(articleDBOList)
    }

    private companion object {
        private const val LOG_TAG = "ArticlesRepository"
        private const val MESSAGE_DATABASE_ERROR = "Error while getting articles from database:"
        private const val MESSAGE_SERVER_ERROR = "Error while getting articles from server:"
    }
}