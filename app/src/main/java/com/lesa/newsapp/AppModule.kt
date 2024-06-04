package com.lesa.newsapp

import android.content.Context
import com.lesa.database.NewsDatabase
import com.lesa.database.newsDatabase
import com.lesa.news_data.ArticlesRepository
import com.lesa.newsapi.NewsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNewsApi(): NewsApi {
        return NewsApi(
            baseUrl = BuildConfig.NEWS_API_BASE_URL,
            apiKey = BuildConfig.NEWS_API_KEY
        )
    }

    @Provides
    @Singleton
    fun provideNewsDatabase(@ApplicationContext context: Context): NewsDatabase {
        return newsDatabase(context)
    }

    /*@Provides
    fun provideNewsRepository(): ArticlesRepository {
        return ArticlesRepository()
    }*/
}