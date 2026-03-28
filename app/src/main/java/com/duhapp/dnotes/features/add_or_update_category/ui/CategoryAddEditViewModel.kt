package com.duhapp.dnotes.features.add_or_update_category.ui

import androidx.lifecycle.viewModelScope
import com.duhapp.dnotes.NoteColor
import com.duhapp.dnotes.features.add_or_update_category.domain.UpsertCategory
import com.duhapp.dnotes.foundation.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CategoryAddEditViewModel @Inject constructor(
    private val upsertCategory: UpsertCategory
) : MviViewModel<CategoryAddEditIntent, CategoryAddEditState, CategoryAddEditEffect>(CategoryAddEditState()) {

    init {
        val initialColors = NoteColor.values().map { 
            ColorItemUIModel(color = it) 
        }
        updateState { copy(colors = initialColors) }
    }

    override fun processIntent(intent: CategoryAddEditIntent) {
        when (intent) {
            is CategoryAddEditIntent.Init -> {
                val category = intent.category ?: CategoryUIModel(
                    id = -1,
                    color = ColorItemUIModel(color = NoteColor.BLUE)
                )
                updateState { 
                    copy(
                        category = category,
                        showType = intent.showType,
                        hasSelectedEmoji = category.emoji.isNotBlank(),
                        colors = colors.map { it.copy(isSelected = it.color == category.color.color) }
                    )
                }
            }
            is CategoryAddEditIntent.UpdateName -> {
                updateState { copy(category = category.copy(name = intent.name)) }
            }
            is CategoryAddEditIntent.UpdateDescription -> {
                updateState { copy(category = category.copy(description = intent.description)) }
            }
            is CategoryAddEditIntent.UpdateEmoji -> {
                updateState {
                    copy(
                        category = category.copy(emoji = intent.emoji),
                        hasSelectedEmoji = intent.emoji.isNotBlank()
                    )
                }
            }
            is CategoryAddEditIntent.SelectColor -> {
                updateState { 
                    copy(
                        category = category.copy(color = ColorItemUIModel(color = intent.color)),
                        colors = colors.map { it.copy(isSelected = it.color == intent.color) }
                    )
                }
            }
            is CategoryAddEditIntent.SaveCategory -> saveCategory()
            is CategoryAddEditIntent.Dismiss -> emitEffect(CategoryAddEditEffect.Dismiss)
        }
    }

    private fun saveCategory() {
        if (
            currentState.category.name.isBlank() ||
            currentState.category.description.isBlank() ||
            (currentState.showType == CategoryShowType.Add && !currentState.hasSelectedEmoji) ||
            currentState.category.emoji.isBlank()
        ) {
            emitEffect(CategoryAddEditEffect.ShowError("Fill category name, description and emoji"))
            return
        }
        
        updateState { copy(isLoading = true) }
        viewModelScope.launch {
            try {
                upsertCategory.invoke(currentState.category)
                emitEffect(CategoryAddEditEffect.CategorySaved)
                emitEffect(CategoryAddEditEffect.Dismiss)
            } catch (e: Exception) {
                Timber.e(e, "Failed to save category")
                updateState { copy(isLoading = false) }
                emitEffect(CategoryAddEditEffect.ShowError("Failed to save category"))
            }
        }
    }
}
