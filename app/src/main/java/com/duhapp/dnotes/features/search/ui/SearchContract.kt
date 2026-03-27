package com.duhapp.dnotes.features.search.ui

import com.duhapp.dnotes.features.home.home_screen_category.ui.BaseNoteUIModel
import com.duhapp.dnotes.foundation.mvi.UiEffect
import com.duhapp.dnotes.foundation.mvi.UiIntent
import com.duhapp.dnotes.foundation.mvi.UiState

data class SearchState(
    val query: String = "",
    val results: List<BaseNoteUIModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : UiState

sealed interface SearchIntent : UiIntent {
    data class Search(val query: String) : SearchIntent
    data class OnNoteClicked(val noteId: Int) : SearchIntent
    object NavigationBack : SearchIntent
}

sealed interface SearchEffect : UiEffect {
    data class NavigateToNote(val noteId: Int) : SearchEffect
    object NavigateBack : SearchEffect
}
