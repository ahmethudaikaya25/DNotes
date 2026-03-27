package com.duhapp.dnotes.features.note.ui

import androidx.lifecycle.viewModelScope
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.home.home_screen_category.ui.DEFAULT_NOTE_MODEL
import com.duhapp.dnotes.features.note.domain.GetDefaultCategory
import com.duhapp.dnotes.features.note.domain.GetNoteById
import com.duhapp.dnotes.features.note.domain.UpsertNote
import com.duhapp.dnotes.foundation.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val upsertNote: UpsertNote,
    private val getDefaultCategory: GetDefaultCategory,
    private val getNoteById: GetNoteById
) : MviViewModel<NoteIntent, NoteState, NoteEffect>(NoteState()) {

    override fun processIntent(intent: NoteIntent) {
        when (intent) {
            is NoteIntent.LoadNote -> loadNote(intent.noteId)
            is NoteIntent.UpdateTitle -> updateTitle(intent.title)
            is NoteIntent.UpdateBody -> updateBody(intent.body)
            is NoteIntent.ChangeCategory -> changeCategory(intent.category)
            is NoteIntent.SaveNote -> saveNote(goBack = false)
            is NoteIntent.NavigationBack -> saveNote(goBack = true)
            is NoteIntent.DeleteNote -> {
                // To be implemented in Story 3.3
                emitEffect(NoteEffect.ShowDeleteConfirmation)
            }
        }
    }

    private fun loadNote(noteId: Int?) {
        updateState { copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                if (noteId == null || noteId == -1) {
                    // Create new note
                    val defaultCategory = getDefaultCategory.invoke()
                    val newNote = DEFAULT_NOTE_MODEL.newCopy().apply {
                        category = defaultCategory
                    }
                    updateState {
                        copy(
                            isLoading = false,
                            note = newNote,
                            isEditable = true
                        )
                    }
                } else {
                    // Edit existing note
                    val note = getNoteById.invoke(noteId)
                    if (note != null) {
                        updateState {
                            copy(
                                isLoading = false,
                                note = note,
                                isEditable = true
                            )
                        }
                    } else {
                        updateState {
                            copy(
                                isLoading = false,
                                errorMessage = "Note not found."
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
                updateState {
                    copy(
                        isLoading = false,
                        errorMessage = "Failed to load note"
                    )
                }
            }
        }
    }

    private fun updateTitle(title: String) {
        val currentNote = currentState.note ?: return
        updateState {
            copy(note = currentNote.newCopy().apply { this.title = title })
        }
    }

    private fun updateBody(body: String) {
        val currentNote = currentState.note ?: return
        updateState {
            copy(note = currentNote.newCopy().apply { this.body = body })
        }
    }

    private fun changeCategory(category: CategoryUIModel) {
        val currentNote = currentState.note ?: return
        updateState {
            copy(note = currentNote.newCopy().apply { this.category = category; this.color = category.color.color.ordinal })
        }
    }

    private fun saveNote(goBack: Boolean) {
        val currentNote = currentState.note ?: return
        // Do not save if empty
        if (currentNote.title.isBlank() && currentNote.body.isBlank()) {
            if (goBack) emitEffect(NoteEffect.NavigateBack)
            return
        }

        viewModelScope.launch {
            try {
                val updatedNote = upsertNote.invoke(currentNote)
                updateState {
                    copy(note = updatedNote)
                }
                if (goBack) {
                    emitEffect(NoteEffect.NavigateBack)
                }
            } catch (e: Exception) {
                Timber.e(e)
                emitEffect(NoteEffect.ShowToast("Could not save note"))
            }
        }
    }
}