package com.lesa.features.news_main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.lesa.features.news_main.models.ArticleUI
import com.lesa.uikit.NewsTheme

@Composable
fun NewsMainScreen() {
    NewsMainScreen(viewModel = viewModel())
}

@Composable
internal fun NewsMainScreen(
    viewModel: NewsMainViewModel
) {
    val state = viewModel.state.collectAsState()
    val currentState = state.value
    val articleUIList = currentState.articleUIList
    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {
        if (currentState is State.Error) ErrorView()
        if (currentState is State.Loading) CircularProgressIndicator()
        if (articleUIList != null) Articles(articleUIList = articleUIList) else NewsEmpty()
    }
}

@Composable
internal fun ErrorView()  {
    Text(
        text = "Error",
        color = MaterialTheme.colorScheme.onError,
        modifier = Modifier
            .background(NewsTheme.colorScheme.error)
    )
}

@Composable
internal fun NewsEmpty() {
    Text(text = "Empty")
}

@Composable
private fun Articles(
    @PreviewParameter(
        provider = ArticleListPreviewProvider::class,
        limit = 1
    ) articleUIList: List<ArticleUI>
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(articleUIList) { articleUI ->
            key(articleUI.id) {
                Article(articleUI)
            }
        }
    }
}

@Preview
@Composable
private fun Article(
    @PreviewParameter(
        provider = ArticlePreviewProvider::class,
        limit = 3
    ) article: ArticleUI
) {
    Row {
        article.urlToImage.let { urlToImage ->
            var isImageVisible by remember { mutableStateOf(true) }
            if (isImageVisible) {
                AsyncImage(
                    model = urlToImage,
                    onState = {
                        if (it is AsyncImagePainter.State.Error) {
                            isImageVisible = false
                        }
                    },
                    contentDescription = article.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(128.dp)
                )
                Spacer(modifier = Modifier.size(4.dp))
            }

        }
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = article.title,
                style = NewsTheme.typography.titleMedium,
                maxLines = 1
            )
            Spacer(
                modifier = Modifier.size(4.dp)
            )
            Text(
                text = article.description,
                style = NewsTheme.typography.bodyMedium,
                maxLines = 3
            )
        }
    }

}

private class ArticlePreviewProvider : PreviewParameterProvider<ArticleUI> {
    override val values: Sequence<ArticleUI> = sequenceOf(
        ArticleUI(
            id = 3448,
            title = "Title",
            description = "Description",
            urlToImage = "https://search.yahoo.com/search?p=mei",
            url = "https://duckduckgo.com/?q=metus",
        ),
        ArticleUI(
            id = 6227,
            title = "faucibus",
            description = "fugit",
            urlToImage = null,
            url = "https://search.yahoo.com/search?p=enim"
        ),
        ArticleUI(
            id = 8242,
            title = "laudem",
            description = "cursus",
            urlToImage = null,
            url = "https://search.yahoo.com/search?p=fuisset"
        ),
        ArticleUI(
            id = 8941,
            title = "debet",
            description = "mei",
            urlToImage = null,
            url = "https://www.google.com/#q=ludus"
        ),
    )
}

private class ArticleListPreviewProvider : PreviewParameterProvider<List<ArticleUI>> {
    override val values: Sequence<List<ArticleUI>> = sequenceOf(
        ArticlePreviewProvider().values.toList()
    )
}
