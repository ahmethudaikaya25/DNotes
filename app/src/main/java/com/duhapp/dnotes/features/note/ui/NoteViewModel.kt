package com.duhapp.dnotes.features.note.ui

import androidx.lifecycle.viewModelScope
import com.duhapp.dnotes.app.database.CategoryDao
import com.duhapp.dnotes.NoteColor
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.add_or_update_category.ui.ColorItemUIModel
import com.duhapp.dnotes.features.add_or_update_category.ui.toUIModel
import com.duhapp.dnotes.features.home.home_screen_category.ui.DEFAULT_NOTE_MODEL
import com.duhapp.dnotes.features.note.domain.GetDefaultCategory
import com.duhapp.dnotes.features.note.domain.GetNoteById
import com.duhapp.dnotes.features.note.domain.UpsertNote
import com.duhapp.dnotes.app.database.NoteDao
import com.duhapp.dnotes.foundation.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val upsertNote: UpsertNote,
    private val getDefaultCategory: GetDefaultCategory,
    private val getNoteById: GetNoteById,
    private val categoryDao: CategoryDao,
    private val noteDao: NoteDao
) : MviViewModel<NoteIntent, NoteState, NoteEffect>(NoteState()) {
    private var autoSaveJob: Job? = null

    override fun processIntent(intent: NoteIntent) {
        when (intent) {
            is NoteIntent.LoadNote -> loadNoteAndCategories(intent.noteId)
            is NoteIntent.UpdateTitle -> updateTitle(intent.title)
            is NoteIntent.UpdateBody -> updateBody(intent.body)
            is NoteIntent.ChangeCategory -> changeCategory(intent.category)
            is NoteIntent.ToggleCategorySheet -> updateState { copy(isCategorySheetVisible = intent.isVisible) }
            is NoteIntent.SaveNote -> saveNote(goBack = true)
            is NoteIntent.NavigationBack -> {
                autoSaveJob?.cancel()
                saveNote(goBack = true)
            }
            is NoteIntent.DeleteNote -> {
                emitEffect(NoteEffect.ShowDeleteConfirmation)
            }
            is NoteIntent.ConfirmDeleteNote -> deleteNote()
        }
    }

    private fun loadNoteAndCategories(noteId: Int?) {
        updateState { copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                // Fetch categories
                val availableCategories = categoryDao.getCategories().map { it.toUIModel() }

                if (noteId == null || noteId == -1) {
                    // Create new note
                    val defaultCategory = getDefaultCategory.invoke()
                    val newNote = DEFAULT_NOTE_MODEL.newCopy().apply {
                        category = defaultCategory
                        colorCode = defaultCategory.color.color
                    }
                    updateState {
                        copy(
                            isLoading = false,
                            note = newNote,
                            isEditable = true,
                            availableCategories = availableCategories
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
                                isEditable = true,
                                availableCategories = availableCategories
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
        scheduleAutoSave()
    }

    private fun updateBody(body: String) {
        val currentNote = currentState.note ?: return
        updateState {
            copy(note = currentNote.newCopy().apply { this.body = body })
        }
        scheduleAutoSave()
    }

    private fun changeCategory(category: CategoryUIModel) {
        val currentNote = currentState.note ?: return
        updateState {
            copy(
                note = currentNote.newCopy().apply { 
                    this.category = category 
                    this.colorCode = category.color.color
                },
                isCategorySheetVisible = false
            )
        }
        scheduleAutoSave()
    }

    private fun scheduleAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(600)
            saveNote(goBack = false)
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

    private fun deleteNote() {
        val currentNote = currentState.note ?: return
        if (currentNote.id != -1) {
            viewModelScope.launch {
                try {
                    noteDao.deleteNotes(listOf(currentNote.id))
                    emitEffect(NoteEffect.NavigateBack)
                } catch (e: Exception) {
                    Timber.e(e)
                    emitEffect(NoteEffect.ShowToast("Failed to delete note"))
                }
            }
        } else {
            // Note was not saved yet
            emitEffect(NoteEffect.NavigateBack)
        }
    }
}
