package com.duhapp.dnotes.features.home.ui

import androidx.lifecycle.viewModelScope
import com.duhapp.dnotes.R
import com.duhapp.dnotes.features.add_or_update_category.domain.FetchHomeData
import com.duhapp.dnotes.features.base.ui.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val fetchHomeData: FetchHomeData
) : MviViewModel<HomeScreenIntent, HomeScreenState, HomeScreenEffect>(
    initialState = HomeScreenState()
) {

    init {
        processIntent(HomeScreenIntent.LoadCategories)
    }

    override fun handleIntent(intent: HomeScreenIntent) {
        when (intent) {
            is HomeScreenIntent.LoadCategories -> loadCategories()
            is HomeScreenIntent.NoteClicked -> handleNoteClicked(intent.noteId)
            is HomeScreenIntent.ViewAllClicked -> handleViewAllClicked(intent.categoryId)
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            try {
                val categories = fetchHomeData.invoke()
                setState { copy(isLoading = false, categories = categories) }
            } catch (e: Exception) {
                Timber.e(e)
                setState { copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    private fun handleNoteClicked(noteId: Int) {
        setEffect(HomeScreenEffect.NavigateToNote(noteId))
    }

    private fun handleViewAllClicked(categoryId: Int) {
        setEffect(HomeScreenEffect.NavigateToAllNotes(categoryId))
    }
}
