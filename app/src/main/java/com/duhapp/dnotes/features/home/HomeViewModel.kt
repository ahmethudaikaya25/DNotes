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

    private var rawCategories = emptyList<com.duhapp.dnotes.features.home.home_screen_category.ui.HomeCategoryUIModel>()

    init {
        // Observe DataStore preferences
        userPreferencesRepository.sortByFlow.onEach { sortBy ->
            updateState { copy(sortBy = sortBy) }
            applyPresentation()
        }.launchIn(viewModelScope)

        userPreferencesRepository.groupByFlow.onEach { groupBy ->
            updateState { copy(groupBy = groupBy) }
            applyPresentation()
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
                    rawCategories = emptyList()
                    updateState {
                        copy(
                            isLoading = false,
                            notes = emptyList(),
                            categories = emptyList(),
                            errorMessage = "No notes found. Create one!"
                        )
                    }
                } else {
                    rawCategories = categories
                    applyPresentation(isLoading = false)
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

    private fun applyPresentation(isLoading: Boolean = false) {
        val sortBy = currentState.sortBy
        val groupBy = currentState.groupBy

        val sortedCategories = rawCategories
            .map { category ->
                category.copy(noteList = sortNotes(category.noteList, sortBy))
            }
            .sortedBy { it.title.lowercase() }

        val flatSortedNotes = sortNotes(
            rawCategories.flatMap { it.noteList },
            sortBy
        )

        updateState {
            copy(
                isLoading = isLoading,
                notes = flatSortedNotes,
                categories = if (groupBy == GroupBy.CATEGORY) sortedCategories else emptyList(),
                errorMessage = null
            )
        }
    }

    private fun sortNotes(
        notes: List<com.duhapp.dnotes.features.home.home_screen_category.ui.BaseNoteUIModel>,
        sortBy: SortBy
    ): List<com.duhapp.dnotes.features.home.home_screen_category.ui.BaseNoteUIModel> {
        return when (sortBy) {
            SortBy.DATE_ADDED -> notes.sortedByDescending { it.id }
            SortBy.DATE_MODIFIED -> notes.sortedByDescending { it.id }
            SortBy.TITLE -> notes.sortedBy { it.title.lowercase() }
            SortBy.COLOR -> notes.sortedBy { it.colorCode.ordinal }
        }
    }
}
