package com.duhapp.dnotes.features.note.ui

import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.home.home_screen_category.ui.BaseNoteUIModel
import com.duhapp.dnotes.foundation.mvi.UiEffect
import com.duhapp.dnotes.foundation.mvi.UiIntent
import com.duhapp.dnotes.foundation.mvi.UiState

data class NoteState(
    val isLoading: Boolean = false,
    val note: BaseNoteUIModel? = null,
    val isEditable: Boolean = true,
    val errorMessage: String? = null
) : UiState

sealed interface NoteIntent : UiIntent {
    data class LoadNote(val noteId: Int?) : NoteIntent
    data class UpdateTitle(val title: String) : NoteIntent
    data class UpdateBody(val body: String) : NoteIntent
    data class ChangeCategory(val category: CategoryUIModel) : NoteIntent
    object SaveNote : NoteIntent
    object DeleteNote : NoteIntent
    object NavigationBack : NoteIntent
}

sealed interface NoteEffect : UiEffect {
    object NavigateBack : NoteEffect
    data class ShowToast(val message: String) : NoteEffect
    object ShowDeleteConfirmation : NoteEffect
}
