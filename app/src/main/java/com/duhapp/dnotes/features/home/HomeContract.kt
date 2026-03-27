package com.duhapp.dnotes.features.home

import com.duhapp.dnotes.features.home.home_screen_category.ui.BaseNoteUIModel
import com.duhapp.dnotes.features.home.home_screen_category.ui.HomeCategoryUIModel
import com.duhapp.dnotes.foundation.mvi.UiEffect
import com.duhapp.dnotes.foundation.mvi.UiIntent
import com.duhapp.dnotes.foundation.mvi.UiState

data class HomeState(
    val isLoading: Boolean = false,
    val categories: List<HomeCategoryUIModel> = emptyList(),
    val errorMessage: String? = null,
    // Note: sorting/grouping properties will be added in a subsequent commit
) : UiState

sealed interface HomeIntent : UiIntent {
    object LoadContent : HomeIntent
    data class OnNoteClicked(val noteId: Int) : HomeIntent
    data class OnCategoryViewAllClicked(val categoryId: Int) : HomeIntent
    object OnAddNoteClicked : HomeIntent
}

sealed interface HomeEffect : UiEffect {
    data class NavigateToNote(val noteId: Int? = null) : HomeEffect // null means create new
    data class NavigateToAllNotes(val categoryId: Int) : HomeEffect
    data class ShowToast(val message: String) : HomeEffect
}
