package com.duhapp.dnotes.features.all_notes.ui

import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.home.home_screen_category.ui.BaseNoteUIModel
import com.duhapp.dnotes.foundation.mvi.UiEffect
import com.duhapp.dnotes.foundation.mvi.UiIntent
import com.duhapp.dnotes.foundation.mvi.UiState

data class AllNotesState(
    val isLoading: Boolean = false,
    val category: CategoryUIModel? = null,
    val notes: List<BaseNoteUIModel> = emptyList(),
    val isSelectionMode: Boolean = false,
    val isMoveSheetVisible: Boolean = false,
    val availableCategories: List<CategoryUIModel> = emptyList(),
    val errorMessage: String? = null
) : UiState

sealed interface AllNotesIntent : UiIntent {
    data class LoadNotes(val categoryId: Int) : AllNotesIntent
    data class OnNoteClick(val note: BaseNoteUIModel) : AllNotesIntent
    data class OnNoteLongClick(val note: BaseNoteUIModel) : AllNotesIntent
    object CancelSelectionMode : AllNotesIntent
    object DeleteSelectedNotes : AllNotesIntent
    object MoveSelectedNotes : AllNotesIntent
    data class OnCategorySelected(val category: CategoryUIModel) : AllNotesIntent
    data class ToggleMoveSheet(val isVisible: Boolean) : AllNotesIntent
    object GoBack : AllNotesIntent
}

sealed interface AllNotesEffect : UiEffect {
    object NavigateBack : AllNotesEffect
    data class NavigateToNoteEditor(val noteId: Int) : AllNotesEffect
    data class ShowToast(val message: String) : AllNotesEffect
    // Will pass noteIds or notes to the bottom sheet to move
    data class ShowMoveCategorySheet(val selectedNoteIds: List<Int>) : AllNotesEffect
}
