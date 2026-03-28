package com.duhapp.dnotes.features.all_notes.ui

import androidx.lifecycle.viewModelScope
import com.duhapp.dnotes.app.database.CategoryDao
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.all_notes.domain.DeleteNote
import com.duhapp.dnotes.features.all_notes.domain.UpdateNotes
import com.duhapp.dnotes.features.home.home_screen_category.ui.BaseNoteUIModel
import com.duhapp.dnotes.features.note.data.NoteRepository
import com.duhapp.dnotes.foundation.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import com.duhapp.dnotes.features.add_or_update_category.ui.toUIModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AllNotesViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val deleteNote: DeleteNote,
    private val updateNotes: UpdateNotes,
    private val categoryDao: CategoryDao,
    private val defaultCategoryModel: CategoryUIModel
) : MviViewModel<AllNotesIntent, AllNotesState, AllNotesEffect>(AllNotesState()) {
    private var observedCategoryId: Int = -1
    private var notesObserverJob: Job? = null

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

    init {
        categoryDao.getAll()
            .onEach { categories ->
                updateState {
                    copy(availableCategories = categories.map { it.toUIModel() })
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadNotes(categoryId: Int) {
        observedCategoryId = categoryId
        updateState { copy(isLoading = true, errorMessage = null) }

        notesObserverJob?.cancel()
        notesObserverJob = noteRepository.observeAllNotes()
            .onEach { allNotes ->
                val filteredNotes = allNotes.filter { it.category.id == observedCategoryId }
                val selectedIds = currentState.selectedNoteIds
                val category = filteredNotes.firstOrNull()?.category
                    ?: currentState.availableCategories.firstOrNull { it.id == observedCategoryId }
                    ?: if (observedCategoryId == defaultCategoryModel.id) defaultCategoryModel else currentState.category

                updateState {
                    copy(
                        isLoading = false,
                        category = category,
                        notes = filteredNotes.map { note ->
                            note.newCopy().apply { isSelected = selectedIds.contains(note.id) }
                        },
                        isSelectionMode = selectedIds.isNotEmpty(),
                        errorMessage = null
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun currentSelectedIds(): Set<Int> {
        return currentState.selectedNoteIds.ifEmpty {
            currentState.notes.filter { it.isSelected }.map { it.id }.toSet()
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
        val selectedIds = currentSelectedIds().toMutableSet().apply {
            if (contains(toggledNote.id)) remove(toggledNote.id) else add(toggledNote.id)
        }
        val updatedNotes = currentState.notes.map { note ->
            if (note.id == toggledNote.id) {
                note.newCopy().apply { isSelected = selectedIds.contains(note.id) }
            } else {
                note.newCopy().apply { isSelected = selectedIds.contains(note.id) }
            }
        }

        updateState {
            copy(
                notes = updatedNotes,
                selectedNoteIds = selectedIds,
                isSelectionMode = selectedIds.isNotEmpty()
            )
        }
    }

    private fun cancelSelectionMode() {
        updateState {
            copy(
                notes = notes.map { it.newCopy().apply { isSelected = false } },
                selectedNoteIds = emptySet(),
                isSelectionMode = false
            )
        }
    }

    private fun deleteSelectedNotes() {
        viewModelScope.launch {
            try {
                val selectedNotes = currentState.notes.filter { currentSelectedIds().contains(it.id) }
                if (selectedNotes.isEmpty()) return@launch

                deleteNote.invoke(selectedNotes)
                cancelSelectionMode()
            } catch (e: Exception) {
                Timber.e(e)
                emitEffect(AllNotesEffect.ShowToast("Could not delete notes"))
            }
        }
    }

    private fun changeCategoryForSelectedNotes(category: CategoryUIModel) {
        viewModelScope.launch {
            try {
                val selectedNotes = currentState.notes.filter { currentSelectedIds().contains(it.id) }
                if (selectedNotes.isEmpty()) return@launch

                selectedNotes.forEach {
                    it.category = category
                    it.colorCode = category.color.color
                }
                
                updateNotes.invoke(selectedNotes)

                updateState {
                    copy(
                        isMoveSheetVisible = false,
                        isSelectionMode = false,
                        selectedNoteIds = emptySet()
                    )
                }
            } catch (e: Exception) {
                Timber.e(e)
                emitEffect(AllNotesEffect.ShowToast("Could not move notes"))
            }
        }
    }
}
