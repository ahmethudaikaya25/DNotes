package com.duhapp.dnotes.features.home

import androidx.lifecycle.viewModelScope
import com.duhapp.dnotes.features.add_or_update_category.domain.FetchHomeData
import com.duhapp.dnotes.features.base.data.UserPreferencesRepository
import com.duhapp.dnotes.foundation.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val fetchHomeData: FetchHomeData,
    private val userPreferencesRepository: UserPreferencesRepository
) : MviViewModel<HomeIntent, HomeState, HomeEffect>(HomeState()) {

    init {
        // Observe DataStore preferences
        userPreferencesRepository.sortByFlow.onEach { sortBy ->
            updateState { copy(sortBy = sortBy) }
        }.launchIn(viewModelScope)

        userPreferencesRepository.groupByFlow.onEach { groupBy ->
            updateState { copy(groupBy = groupBy) }
        }.launchIn(viewModelScope)

        // Automatically load content on init for now
        processIntent(HomeIntent.LoadContent)
    }

    override fun processIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadContent -> loadCategories()
            is HomeIntent.OnAddNoteClicked -> emitEffect(HomeEffect.NavigateToNote(null))
            is HomeIntent.OnNoteClicked -> emitEffect(HomeEffect.NavigateToNote(intent.noteId))
            is HomeIntent.OnCategoryViewAllClicked -> emitEffect(HomeEffect.NavigateToAllNotes(intent.categoryId))
            is HomeIntent.OnSortByChanged -> {
                launch { userPreferencesRepository.updateSortBy(intent.sortBy) }
            }
            is HomeIntent.OnGroupByChanged -> {
                launch { userPreferencesRepository.updateGroupBy(intent.groupBy) }
            }
        }
    }

    private fun loadCategories() {
        updateState { copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val categories = fetchHomeData.invoke()
                if (categories.isEmpty()) {
                    updateState {
                        copy(
                            isLoading = false,
                            categories = emptyList(),
                            errorMessage = "No notes found. Create one!"
                        )
                    }
                } else {
                    updateState {
                        copy(
                            isLoading = false,
                            categories = categories,
                            errorMessage = null
                        )
                    }
                }
            } catch (e: Exception) {
                updateState {
                    copy(
                        isLoading = false,
                        errorMessage = e.message ?: "An error occurred"
                    )
                }
            }
        }
    }
}
