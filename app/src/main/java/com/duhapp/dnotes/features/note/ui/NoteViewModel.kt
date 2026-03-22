package com.duhapp.dnotes.features.note.ui

import androidx.lifecycle.viewModelScope
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.base.ui.mvi.MviViewModel
import com.duhapp.dnotes.features.home.home_screen_category.ui.DEFAULT_NOTE_MODEL
import com.duhapp.dnotes.features.note.domain.GetDefaultCategory
import com.duhapp.dnotes.features.note.domain.GetNoteById
import com.duhapp.dnotes.features.note.domain.UpsertNote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val upsertNote: UpsertNote,
    private val getDefaultCategory: GetDefaultCategory,
    private val getNoteById: GetNoteById,
) : MviViewModel<NoteScreenIntent, NoteScreenState, NoteScreenEffect>(
    initialState = NoteScreenState()
) {

    init {
        processIntent(NoteScreenIntent.LoadNote(null))
    }

    override fun handleIntent(intent: NoteScreenIntent) {
        when (intent) {
            is NoteScreenIntent.LoadNote -> loadNote(intent.noteId)
            is NoteScreenIntent.UpdateTitle -> updateTitle(intent.title)
            is NoteScreenIntent.UpdateBody -> updateBody(intent.body)
            is NoteScreenIntent.SelectCategory -> selectCategory(intent.category)
            is NoteScreenIntent.SaveNote -> saveNote()
            is NoteScreenIntent.ShowCategoryBottomSheet -> showCategoryBottomSheet()
            is NoteScreenIntent.HideCategoryBottomSheet -> hideCategoryBottomSheet()
            is NoteScreenIntent.NavigateBack -> navigateBack()
        }
    }

    private fun loadNote(noteId: Int?) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            try {
                if (noteId == null || noteId == 0) {
                    val defaultCategory = getDefaultCategory.invoke()
                    setState {
                        copy(
                            isLoading = false,
                            noteId = null,
                            title = "",
                            body = "",
                            category = defaultCategory,
                            isEditable = true
                        )
                    }
                } else {
                    val note = getNoteById.invoke(noteId)
                    if (note != null) {
                        setState {
                            copy(
                                isLoading = false,
                                noteId = note.id,
                                title = note.title,
                                body = note.body,
                                category = note.category,
                                isEditable = true
                            )
                        }
                    } else {
                        val defaultCategory = getDefaultCategory.invoke()
                        setState {
                            copy(
                                isLoading = false,
                                noteId = null,
                                title = "",
                                body = "",
                                category = defaultCategory,
                                isEditable = true
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
                setState { copy(isLoading = false, error = e.message ?: "Failed to load note") }
            }
        }
    }

    private fun updateTitle(title: String) {
        setState { copy(title = title) }
    }

    private fun updateBody(body: String) {
        setState { copy(body = body) }
    }

    private fun selectCategory(category: CategoryUIModel) {
        setState { copy(category = category, showCategoryBottomSheet = false) }
    }

    private fun saveNote() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            try {
                val note = DEFAULT_NOTE_MODEL.newCopy().apply {
                    this.title = currentState.title
                    this.body = currentState.body
                    this.category = currentState.category
                    currentState.noteId?.let { this.id = it }
                }
                upsertNote.invoke(note)
                setState { copy(isLoading = false) }
                setEffect(NoteScreenEffect.NoteSaved)
                setEffect(NoteScreenEffect.NavigateBack)
            } catch (e: Exception) {
                Timber.e(e)
                setState { copy(isLoading = false, error = e.message ?: "Failed to save note") }
                setEffect(NoteScreenEffect.ShowError(e.message ?: "Failed to save note"))
            }
        }
    }

    private fun showCategoryBottomSheet() {
        setState { copy(showCategoryBottomSheet = true) }
    }

    private fun hideCategoryBottomSheet() {
        setState { copy(showCategoryBottomSheet = false) }
    }

    private fun navigateBack() {
        setEffect(NoteScreenEffect.NavigateBack)
    }
}
