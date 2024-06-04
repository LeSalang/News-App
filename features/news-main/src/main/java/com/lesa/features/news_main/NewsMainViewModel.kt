package com.lesa.features.news_main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lesa.features.news_main.models.ArticleUI
import com.lesa.news_data.ArticlesRepository
import com.lesa.news_data.RequestResult
import com.lesa.news_data.map
import com.lesa.news_data.models.Article
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class NewsMainViewModel(
    private val getAllArticlesUseCase: GetAllArticlesUseCase
) : ViewModel() {

    val state: StateFlow<State> = getAllArticlesUseCase()
        .map { it.toState() }
        .stateIn(viewModelScope, SharingStarted.Lazily, State.None)
}


private fun RequestResult<List<ArticleUI>>.toState(): State {
    return when (this) {
        is RequestResult.Error -> State.Error()
        is RequestResult.InProgress -> State.Loading(data)
        is RequestResult.Success -> State.Success(checkNotNull(data))
    }
}

sealed class State {
    data object None : State()
    data class Loading(val articleUIS: List<ArticleUI>?) : State()
    data class Error(val message: String? = null) : State()
    data class Success(val articleUIS: List<ArticleUI>) : State()
}