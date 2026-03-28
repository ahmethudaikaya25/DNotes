package com.duhapp.dnotes.features.search.ui

import androidx.lifecycle.viewModelScope
import com.duhapp.dnotes.features.home.home_screen_category.ui.BaseNoteUIModel
import com.duhapp.dnotes.features.note.data.NoteRepository
import com.duhapp.dnotes.foundation.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : MviViewModel<SearchIntent, SearchState, SearchEffect>(SearchState()) {

    private var searchJob: Job? = null
    private var allNotes: List<BaseNoteUIModel> = emptyList()

    init {
        noteRepository.observeAllNotes()
            .onEach { notes ->
                allNotes = notes
                applySearch(currentState.query)
            }
            .launchIn(viewModelScope)
    }

    override fun processIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.Search -> performSearch(intent.query)
            is SearchIntent.OnNoteClicked -> emitEffect(SearchEffect.NavigateToNote(intent.noteId))
            is SearchIntent.NavigationBack -> emitEffect(SearchEffect.NavigateBack)
        }
    }

    private fun performSearch(query: String) {
        updateState { copy(query = query, isLoading = query.isNotBlank(), errorMessage = null) }
        
        searchJob?.cancel()
        if (query.isBlank()) {
            updateState { copy(results = emptyList(), isLoading = false) }
            return
        }

        searchJob = viewModelScope.launch {
            delay(300) // Debounce
            applySearch(query)
        }
    }

    private fun applySearch(query: String) {
        if (query.isBlank()) {
            updateState { copy(results = emptyList(), isLoading = false, errorMessage = null) }
            return
        }

        val normalizedQuery = query.trim().lowercase()
        val results = allNotes.filter { note ->
            note.title.lowercase().contains(normalizedQuery) ||
                note.body.lowercase().contains(normalizedQuery) ||
                note.category.name.lowercase().contains(normalizedQuery)
        }
        updateState { copy(results = results, isLoading = false, errorMessage = null) }
    }
}
