package com.lesa.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.lesa.database.models.ArticleDBO
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticlesDao {
    @Query("SELECT * FROM articles")
    fun observeAllArticles(): Flow<List<ArticleDBO>>

    @Query("SELECT * FROM articles")
    suspend fun getAllArticles(): List<ArticleDBO>

    @Insert
    suspend fun insertArticles(articles: List<ArticleDBO>)

    @Delete
    suspend fun deleteSelectedArticles(articles: List<ArticleDBO>)

    @Query("DELETE FROM articles")
    suspend fun deleteAllArticles()
}