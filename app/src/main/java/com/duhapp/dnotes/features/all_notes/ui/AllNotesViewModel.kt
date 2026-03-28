package com.duhapp.dnotes.features.all_notes.ui

import androidx.lifecycle.viewModelScope
import com.duhapp.dnotes.app.database.CategoryDao
import com.duhapp.dnotes.NoteColor
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.add_or_update_category.ui.ColorItemUIModel
import com.duhapp.dnotes.features.all_notes.domain.DeleteNote
import com.duhapp.dnotes.features.all_notes.domain.GetNotesByCategoryId
import com.duhapp.dnotes.features.all_notes.domain.UpdateNotes
import com.duhapp.dnotes.features.home.home_screen_category.ui.BaseNoteUIModel
import com.duhapp.dnotes.foundation.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import com.duhapp.dnotes.features.add_or_update_category.ui.toUIModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AllNotesViewModel @Inject constructor(
    private val getNotesByCategoryId: GetNotesByCategoryId,
    private val deleteNote: DeleteNote,
    private val updateNotes: UpdateNotes,
    private val categoryDao: CategoryDao,
    private val defaultCategoryModel: CategoryUIModel
) : MviViewModel<AllNotesIntent, AllNotesState, AllNotesEffect>(AllNotesState()) {

    override fun processIntent(intent: AllNotesIntent) {
        when (intent) {
            is AllNotesIntent.LoadNotes -> loadNotes(intent.categoryId)
            is AllNotesIntent.OnNoteClick -> handleNoteClick(intent.note)
            is AllNotesIntent.OnNoteLongClick -> handleNoteLongClick(intent.note)
            is AllNotesIntent.CancelSelectionMode -> cancelSelectionMode()
            is AllNotesIntent.DeleteSelectedNotes -> deleteSelectedNotes()
            is AllNotesIntent.MoveSelectedNotes -> updateState { copy(isMoveSheetVisible = true) }
            is AllNotesIntent.OnCategorySelected -> changeCategoryForSelectedNotes(intent.category)
            is AllNotesIntent.ToggleMoveSheet -> updateState { copy(isMoveSheetVisible = intent.isVisible) }
            is AllNotesIntent.GoBack -> emitEffect(AllNotesEffect.NavigateBack)
        }
    }

    private fun loadNotes(categoryId: Int) {
        updateState { copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                // Fetch categories
                val availableCategories = categoryDao.getCategories().map { it.toUIModel() }

                val notesById = getNotesByCategoryId.invoke(categoryId)
                val category = notesById.firstOrNull()?.category ?: defaultCategoryModel

                if (notesById.isEmpty()) {
                    updateState {
                        copy(
                            isLoading = false,
                            category = category,
                            notes = emptyList(),
                            errorMessage = null,
                            availableCategories = availableCategories
                        )
                    }
                    emitEffect(AllNotesEffect.NavigateBack)
                    return@launch
                }
                
                updateState {
                    copy(
                        isLoading = false,
                        category = category,
                        notes = notesById,
                        isSelectionMode = false,
                        availableCategories = availableCategories
                    )
                }
            } catch (e: Exception) {
                Timber.e(e)
                updateState {
                    copy(
                        isLoading = false,
                        errorMessage = "Could not load notes"
                    )
                }
            }
        }
    }

    private fun handleNoteClick(note: BaseNoteUIModel) {
        if (currentState.isSelectionMode) {
            toggleNoteSelection(note)
        } else {
            emitEffect(AllNotesEffect.NavigateToNoteEditor(note.id))
        }
    }

    private fun handleNoteLongClick(note: BaseNoteUIModel) {
        if (!currentState.isSelectionMode) {
            updateState { copy(isSelectionMode = true) }
        }
        toggleNoteSelection(note)
    }

    private fun toggleNoteSelection(toggledNote: BaseNoteUIModel) {
        val updatedNotes = currentState.notes.map {
            if (it.id == toggledNote.id) {
                it.newCopy().apply { isSelected = !toggledNote.isSelected }
            } else {
                it
            }
        }
        
        // If no notes are selected, exit selection mode
        val anySelected = updatedNotes.any { it.isSelected }
        updateState {
            copy(
                notes = updatedNotes,
                isSelectionMode = anySelected
            )
        }
    }

    private fun cancelSelectionMode() {
        val mappedNotes = currentState.notes.map {
            it.newCopy().apply { isSelected = false }
        }
        updateState {
            copy(
                notes = mappedNotes,
                isSelectionMode = false
            )
        }
    }

    private fun deleteSelectedNotes() {
        viewModelScope.launch {
            try {
                val selectedNotes = currentState.notes.filter { it.isSelected }
                if (selectedNotes.isEmpty()) return@launch

                deleteNote.invoke(selectedNotes)
                
                // Reload notes from the current category
                currentState.category?.let { loadNotes(it.id) }
            } catch (e: Exception) {
                Timber.e(e)
                emitEffect(AllNotesEffect.ShowToast("Could not delete notes"))
            }
        }
    }

    private fun changeCategoryForSelectedNotes(category: CategoryUIModel) {
        viewModelScope.launch {
            try {
                val selectedNotes = currentState.notes.filter { it.isSelected }
                if (selectedNotes.isEmpty()) return@launch

                selectedNotes.forEach {
                    it.category = category
                    it.colorCode = category.color.color
                }
                
                updateNotes.invoke(selectedNotes)
                
                // Reset select mode and reload
                updateState { copy(isMoveSheetVisible = false, isSelectionMode = false) }
                loadNotes(currentState.category?.id ?: -1)
            } catch (e: Exception) {
                Timber.e(e)
                emitEffect(AllNotesEffect.ShowToast("Could not move notes"))
            }
        }
    }
}
