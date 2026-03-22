package com.duhapp.dnotes.features.note.ui

import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.base.ui.mvi.MviEffect
import com.duhapp.dnotes.features.base.ui.mvi.MviIntent
import com.duhapp.dnotes.features.base.ui.mvi.MviState

data class NoteScreenState(
    val isLoading: Boolean = false,
    val noteId: Int? = null,
    val title: String = "",
    val body: String = "",
    val category: CategoryUIModel = CategoryUIModel(),
    val isEditable: Boolean = true,
    val showCategoryBottomSheet: Boolean = false,
    val error: String? = null
) : MviState

sealed class NoteScreenIntent : MviIntent {
    data class LoadNote(val noteId: Int?) : NoteScreenIntent()
    data class UpdateTitle(val title: String) : NoteScreenIntent()
    data class UpdateBody(val body: String) : NoteScreenIntent()
    data class SelectCategory(val category: CategoryUIModel) : NoteScreenIntent()
    data object SaveNote : NoteScreenIntent()
    data object ShowCategoryBottomSheet : NoteScreenIntent()
    data object HideCategoryBottomSheet : NoteScreenIntent()
    data object NavigateBack : NoteScreenIntent()
}

sealed class NoteScreenEffect : MviEffect {
    data object NavigateBack : NoteScreenEffect()
    data class ShowError(val message: String) : NoteScreenEffect()
    data object NoteSaved : NoteScreenEffect()
}
