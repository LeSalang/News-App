package com.lesa.features.news_main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lesa.features.news_main.models.ArticleUI
import com.lesa.news_data.RequestResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
internal class NewsMainViewModel @Inject constructor(
    getAllArticlesUseCase: Provider<GetAllArticlesUseCase>,
) : ViewModel() {

    val state: StateFlow<State> = getAllArticlesUseCase.get().invoke()
        .map { it.toState() }
        .stateIn(viewModelScope, SharingStarted.Lazily, State.None)
}


private fun RequestResult<List<ArticleUI>>.toState(): State {
    return when (this) {
        is RequestResult.Error -> State.Error()
        is RequestResult.InProgress -> State.Loading(data)
        is RequestResult.Success -> State.Success(data)
    }
}

sealed class State {
    data object None : State()
    data class Loading(val articleUIS: List<ArticleUI>? = null) : State()
    data class Error(val articleUIS: List<ArticleUI>? = null) : State()
    data class Success(val articleUIS: List<ArticleUI>) : State()
}