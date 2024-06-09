package com.lesa.features.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lesa.data.RequestResult
import com.lesa.features.main.models.ArticleUI
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

    val state: StateFlow<State> = getAllArticlesUseCase.get().invoke(query = "movie")
        .map { it.toState() }
        .stateIn(viewModelScope, SharingStarted.Lazily, State.None)
}

private fun RequestResult<List<ArticleUI>>.toState(): State {
    return when (this) {
        is RequestResult.Error -> State.Error(data)
        is RequestResult.InProgress -> State.Loading(data)
        is RequestResult.Success -> State.Success(data)
    }
}

internal sealed class State(val articleUIList: List<ArticleUI>?) {
    data object None : State(articleUIList = null)
    class Loading(articleUIList: List<ArticleUI>? = null) : State(articleUIList)
    class Error(articleUIList: List<ArticleUI>? = null) : State(articleUIList)
    class Success(articleUIList: List<ArticleUI>) : State(articleUIList)
}
