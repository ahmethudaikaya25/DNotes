package com.duhapp.dnotes.features.home.ui

import com.duhapp.dnotes.features.base.ui.mvi.MviEffect
import com.duhapp.dnotes.features.base.ui.mvi.MviIntent
import com.duhapp.dnotes.features.base.ui.mvi.MviState
import com.duhapp.dnotes.features.home.home_screen_category.ui.HomeCategoryUIModel

data class HomeScreenState(
    val isLoading: Boolean = false,
    val categories: List<HomeCategoryUIModel> = emptyList(),
    val error: String? = null
) : MviState

sealed class HomeScreenIntent : MviIntent {
    data object LoadCategories : HomeScreenIntent()
    data class NoteClicked(val noteId: Int) : HomeScreenIntent()
    data class ViewAllClicked(val categoryId: Int) : HomeScreenIntent()
}

sealed class HomeScreenEffect : MviEffect {
    data class NavigateToNote(val noteId: Int) : HomeScreenEffect()
    data class NavigateToAllNotes(val categoryId: Int) : HomeScreenEffect()
    data class ShowError(val message: String) : HomeScreenEffect()
}
