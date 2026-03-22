package com.duhapp.dnotes.features.all_notes.ui

import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.base.ui.mvi.MviEffect
import com.duhapp.dnotes.features.base.ui.mvi.MviIntent
import com.duhapp.dnotes.features.base.ui.mvi.MviState
import com.duhapp.dnotes.features.home.home_screen_category.ui.BaseNoteUIModel

data class AllNotesScreenState(
    val isLoading: Boolean = false,
    val category: CategoryUIModel = CategoryUIModel(),
    val notes: List<BaseNoteUIModel> = emptyList(),
    val isSelectable: Boolean = false,
    val selectedNoteIds: Set<Int> = emptySet(),
    val error: String? = null
) : MviState

sealed class AllNotesScreenIntent : MviIntent {
    data class LoadNotes(val categoryId: Int) : AllNotesScreenIntent()
    data class NoteClicked(val note: BaseNoteUIModel) : AllNotesScreenIntent()
    data class NoteLongClicked(val note: BaseNoteUIModel) : AllNotesScreenIntent()
    data class ToggleNoteSelection(val note: BaseNoteUIModel) : AllNotesScreenIntent()
    data object DeleteSelectedNotes : AllNotesScreenIntent()
    data class DeleteNote(val note: BaseNoteUIModel) : AllNotesScreenIntent()
    data class MoveNote(val note: BaseNoteUIModel) : AllNotesScreenIntent()
    data object NavigateBack : AllNotesScreenIntent()
}

sealed class AllNotesScreenEffect : MviEffect {
    data class NavigateToNote(val note: BaseNoteUIModel) : AllNotesScreenEffect()
    data object NavigateBack : AllNotesScreenEffect()
    data class ShowError(val message: String) : AllNotesScreenEffect()
    data class ShowMoveDialog(val note: BaseNoteUIModel) : AllNotesScreenEffect()
    data object NotesDeleted : AllNotesScreenEffect()
}
