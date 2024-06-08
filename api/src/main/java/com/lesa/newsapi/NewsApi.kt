package com.lesa.newsapi

import androidx.annotation.IntRange
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.lesa.newsapi.models.ArticleDTO
import com.lesa.newsapi.models.Language
import com.lesa.newsapi.models.ResponseDTO
import com.lesa.newsapi.models.SortBy
import com.lesa.newsapi.utils.NewsApiKeyInterceptor
import com.skydoves.retrofit.adapters.result.ResultCallAdapterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Date

// API documentation: https://newsapi.org/docs/get-started
interface NewsApi {

    // API details: https://newsapi.org/docs/endpoints/everything)
    @GET(EVERYTHING)
    suspend fun getAllArticles(
        @Query(Q) query: String? = null,
        @Query(FROM) from: Date? = null,
        @Query(TO) to: Date? = null,
        @Query(LANGUAGE) language: List<@JvmSuppressWildcards Language>? = null,
        @Query(SORT_BY) sortBy: SortBy? = null,
        @Query(PAGE_SIZE) @IntRange(from = 1, to = 100) pageSize: Int = 100,
        @Query(PAGE) @IntRange(from = 1) page: Int = 1,
        ): Result<ResponseDTO<ArticleDTO>>
}

fun NewsApi(
    apiKey: String,
    baseUrl: String,
    json: Json = Json,
    okHttpClient: OkHttpClient? = null,
) : NewsApi {
    return retrofit(
        apiKey = apiKey,
        baseUrl = baseUrl,
        json = json,
        okHttpClient = okHttpClient,
    ).create()
}

private fun retrofit(
    apiKey: String,
    baseUrl: String,
    json: Json = Json,
    okHttpClient: OkHttpClient?,
): Retrofit {
    val jsonConverterFactory = json.asConverterFactory("application/json".toMediaType())

    val modifiedOokHttpClient = (okHttpClient?.newBuilder() ?: OkHttpClient.Builder())
        .addInterceptor(NewsApiKeyInterceptor(apiKey))
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(jsonConverterFactory)
        .addCallAdapterFactory(ResultCallAdapterFactory.create())
        .client(modifiedOokHttpClient)
        .build()

    return retrofit
}

private const val EVERYTHING = "everything"
private const val Q = "q"
private const val FROM = "from"
private const val TO = "to"
private const val LANGUAGE = "language"
private const val SORT_BY = "sortBy"
private const val PAGE_SIZE = "pageSize"
private const val PAGE = "page"