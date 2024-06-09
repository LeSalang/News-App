package com.lesa.data

import com.lesa.data.models.Article
import com.lesa.data.models.Source
import com.lesa.database.models.ArticleDBO
import com.lesa.database.models.SourceDBO
import com.lesa.newsapi.models.ArticleDTO
import com.lesa.newsapi.models.SourceDTO
import java.util.UUID

internal fun ArticleDTO.toArticleDBO(): ArticleDBO {
    return ArticleDBO(
        id = UUID.randomUUID().mostSignificantBits,
        sourceDBO = sourceDTO?.toSourceDBO(),
        author = author,
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content
    )
}

internal fun ArticleDBO.toArticle(): Article {
    return Article(
        id = id,
        source = sourceDBO?.toSource(),
        author = author,
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content
    )
}

internal fun ArticleDTO.toArticle(): Article {
    return Article(
        id = null,
        source = sourceDTO?.toSource(),
        author = author,
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content
    )
}

internal fun SourceDTO.toSource(): Source {
    return Source(
        id = id ?: name,
        name = name
    )
}

internal fun SourceDTO.toSourceDBO(): SourceDBO {
    return SourceDBO(
        id = id ?: name,
        name = name
    )
}

internal fun SourceDBO.toSource(): Source {
    return Source(
        id = id,
        name = name
    )
}
