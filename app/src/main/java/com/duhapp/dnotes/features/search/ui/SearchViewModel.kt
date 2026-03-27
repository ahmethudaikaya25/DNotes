package com.duhapp.dnotes.features.search.ui

import androidx.lifecycle.viewModelScope
import com.duhapp.dnotes.features.search.domain.SearchNotes
import com.duhapp.dnotes.foundation.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchNotes: SearchNotes
) : MviViewModel<SearchIntent, SearchState, SearchEffect>(SearchState()) {

    private var searchJob: Job? = null

    override fun processIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.Search -> performSearch(intent.query)
            is SearchIntent.OnNoteClicked -> emitEffect(SearchEffect.NavigateToNote(intent.noteId))
            is SearchIntent.NavigationBack -> emitEffect(SearchEffect.NavigateBack)
        }
    }

    private fun performSearch(query: String) {
        updateState { copy(query = query, isLoading = query.isNotBlank()) }
        
        searchJob?.cancel()
        if (query.isBlank()) {
            updateState { copy(results = emptyList(), isLoading = false) }
            return
        }

        searchJob = viewModelScope.launch {
            delay(300) // Debounce
            try {
                val results = searchNotes.invoke(query)
                updateState { copy(results = results, isLoading = false) }
            } catch (e: Exception) {
                Timber.e(e, "Search failed")
                updateState { copy(isLoading = false, errorMessage = "Search failed. Try again.") }
            }
        }
    }
}
