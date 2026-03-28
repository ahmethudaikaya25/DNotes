package com.duhapp.dnotes.features.add_or_update_category.ui

import com.duhapp.dnotes.NoteColor
import com.duhapp.dnotes.foundation.mvi.UiEffect
import com.duhapp.dnotes.foundation.mvi.UiIntent
import com.duhapp.dnotes.foundation.mvi.UiState

data class CategoryAddEditState(
    val category: CategoryUIModel = CategoryUIModel(),
    val colors: List<ColorItemUIModel> = emptyList(),
    val showType: CategoryShowType = CategoryShowType.Add,
    val hasSelectedEmoji: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : UiState

sealed interface CategoryAddEditIntent : UiIntent {
    data class Init(val category: CategoryUIModel?, val showType: CategoryShowType) : CategoryAddEditIntent
    data class UpdateName(val name: String) : CategoryAddEditIntent
    data class UpdateDescription(val description: String) : CategoryAddEditIntent
    data class UpdateEmoji(val emoji: String) : CategoryAddEditIntent
    data class SelectColor(val color: NoteColor) : CategoryAddEditIntent
    object SaveCategory : CategoryAddEditIntent
    object Dismiss : CategoryAddEditIntent
}

sealed interface CategoryAddEditEffect : UiEffect {
    object CategorySaved : CategoryAddEditEffect
    object Dismiss : CategoryAddEditEffect
    data class ShowError(val message: String) : CategoryAddEditEffect
}
