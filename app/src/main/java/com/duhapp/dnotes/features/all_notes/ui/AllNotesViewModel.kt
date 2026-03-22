package com.duhapp.dnotes.features.all_notes.ui

import androidx.lifecycle.viewModelScope
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.all_notes.domain.DeleteNote
import com.duhapp.dnotes.features.all_notes.domain.GetNotesByCategoryId
import com.duhapp.dnotes.features.all_notes.domain.UpdateNotes
import com.duhapp.dnotes.features.base.ui.mvi.MviViewModel
import com.duhapp.dnotes.features.home.home_screen_category.ui.BaseNoteUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AllNotesViewModel @Inject constructor(
    private val getNotesByCategoryId: GetNotesByCategoryId,
    private val defaultCategoryModel: CategoryUIModel,
    private val deleteNote: DeleteNote,
    private val updateNotes: UpdateNotes,
) : MviViewModel<AllNotesScreenIntent, AllNotesScreenState, AllNotesScreenEffect>(
    initialState = AllNotesScreenState()
) {

    override fun handleIntent(intent: AllNotesScreenIntent) {
        when (intent) {
            is AllNotesScreenIntent.LoadNotes -> loadNotes(intent.categoryId)
            is AllNotesScreenIntent.NoteClicked -> handleNoteClicked(intent.note)
            is AllNotesScreenIntent.NoteLongClicked -> handleNoteLongClicked(intent.note)
            is AllNotesScreenIntent.ToggleNoteSelection -> toggleNoteSelection(intent.note)
            is AllNotesScreenIntent.DeleteSelectedNotes -> deleteSelectedNotes()
            is AllNotesScreenIntent.DeleteNote -> deleteNote(intent.note)
            is AllNotesScreenIntent.MoveNote -> moveNote(intent.note)
            is AllNotesScreenIntent.NavigateBack -> navigateBack()
        }
    }

    private fun loadNotes(categoryId: Int) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            try {
                val notes = getNotesByCategoryId.invoke(categoryId)
                if (notes.isEmpty()) {
                    setState {
                        copy(
                            isLoading = false,
                            category = defaultCategoryModel,
                            notes = emptyList()
                        )
                    }
                } else {
                    setState {
                        copy(
                            isLoading = false,
                            category = notes.first().category,
                            notes = notes
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
                setState { copy(isLoading = false, error = e.message ?: "Failed to load notes") }
            }
        }
    }

    private fun handleNoteClicked(note: BaseNoteUIModel) {
        if (currentState.isSelectable) {
            toggleNoteSelection(note)
        } else {
            setEffect(AllNotesScreenEffect.NavigateToNote(note))
        }
    }

    private fun handleNoteLongClicked(note: BaseNoteUIModel) {
        if (!currentState.isSelectable) {
            val updatedNotes = currentState.notes.map { n ->
                n.newCopy().apply {
                    isSelectable = n.id == note.id
                    isSelected = n.id == note.id
                }
            }
            setState { copy(isSelectable = true, notes = updatedNotes) }
        }
    }

    private fun toggleNoteSelection(note: BaseNoteUIModel) {
        val newSelectedIds = currentState.selectedNoteIds.toMutableSet()
        if (newSelectedIds.contains(note.id)) {
            newSelectedIds.remove(note.id)
        } else {
            newSelectedIds.add(note.id)
        }
        
        val updatedNotes = currentState.notes.map { n ->
            n.newCopy().apply {
                isSelectable = currentState.isSelectable
                isSelected = newSelectedIds.contains(n.id)
            }
        }
        
        setState {
            copy(
                selectedNoteIds = newSelectedIds,
                notes = updatedNotes
            )
        }
    }

    private fun deleteSelectedNotes() {
        viewModelScope.launch {
            try {
                val selectedNotes = currentState.notes.filter { currentState.selectedNoteIds.contains(it.id) }
                deleteNote.invoke(selectedNotes)
                setEffect(AllNotesScreenEffect.NotesDeleted)
                loadNotes(currentState.category.id)
                clearSelection()
            } catch (e: Exception) {
                Timber.e(e)
                setEffect(AllNotesScreenEffect.ShowError(e.message ?: "Failed to delete notes"))
            }
        }
    }

    private fun deleteNote(note: BaseNoteUIModel) {
        viewModelScope.launch {
            try {
                deleteNote.invoke(listOf(note))
                loadNotes(currentState.category.id)
            } catch (e: Exception) {
                Timber.e(e)
                setEffect(AllNotesScreenEffect.ShowError(e.message ?: "Failed to delete note"))
            }
        }
    }

    private fun moveNote(note: BaseNoteUIModel) {
        setEffect(AllNotesScreenEffect.ShowMoveDialog(note))
    }

    private fun navigateBack() {
        setEffect(AllNotesScreenEffect.NavigateBack)
    }

    private fun clearSelection() {
        val updatedNotes = currentState.notes.map { n ->
            n.newCopy().apply {
                isSelectable = false
                isSelected = false
            }
        }
        setState {
            copy(
                isSelectable = false,
                selectedNoteIds = emptySet(),
                notes = updatedNotes
            )
        }
    }

    fun moveNotesToCategory(notes: List<BaseNoteUIModel>, category: CategoryUIModel) {
        viewModelScope.launch {
            try {
                val notesToMove = notes.map { it.newCopy().apply { this.category = category } }
                updateNotes.invoke(notesToMove)
                loadNotes(currentState.category.id)
            } catch (e: Exception) {
                Timber.e(e)
                setEffect(AllNotesScreenEffect.ShowError(e.message ?: "Failed to move notes"))
            }
        }
    }
}
