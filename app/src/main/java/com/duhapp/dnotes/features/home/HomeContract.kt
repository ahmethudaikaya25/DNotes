package com.duhapp.dnotes.features.home

import com.duhapp.dnotes.features.home.home_screen_category.ui.BaseNoteUIModel
import com.duhapp.dnotes.features.home.home_screen_category.ui.HomeCategoryUIModel
import com.duhapp.dnotes.foundation.mvi.UiEffect
import com.duhapp.dnotes.foundation.mvi.UiIntent
import com.duhapp.dnotes.foundation.mvi.UiState

enum class SortBy {
    DATE_ADDED,
    DATE_MODIFIED,
    TITLE,
    COLOR
}

enum class GroupBy {
    NONE,     // Shows a flat list of all notes
    CATEGORY  // Groups notes by their assigned categories
}

data class HomeState(
    val isLoading: Boolean = false,
    val notes: List<BaseNoteUIModel> = emptyList(), // Flat list of all notes
    val categories: List<HomeCategoryUIModel> = emptyList(), // Grouped by category
    val errorMessage: String? = null,
    val sortBy: SortBy = SortBy.DATE_ADDED,
    val groupBy: GroupBy = GroupBy.NONE
) : UiState

sealed interface HomeIntent : UiIntent {
    object LoadContent : HomeIntent
    data class OnNoteClicked(val noteId: Int) : HomeIntent
    data class OnCategoryViewAllClicked(val categoryId: Int) : HomeIntent
    object OnAddNoteClicked : HomeIntent
    data class OnSortByChanged(val sortBy: SortBy) : HomeIntent
    data class OnGroupByChanged(val groupBy: GroupBy) : HomeIntent
}

sealed interface HomeEffect : UiEffect {
    data class NavigateToNote(val noteId: Int? = null) : HomeEffect // null means create new
    data class NavigateToAllNotes(val categoryId: Int) : HomeEffect
    data class ShowToast(val message: String) : HomeEffect
}
