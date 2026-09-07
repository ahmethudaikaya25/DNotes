package com.duhapp.dnotes.features.add_or_update_category.ui

import androidx.lifecycle.viewModelScope
import com.duhapp.dnotes.NoteColor
import com.duhapp.dnotes.features.add_or_update_category.domain.DeleteCategory
import com.duhapp.dnotes.features.add_or_update_category.domain.UpsertCategory
import com.duhapp.dnotes.foundation.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CategoryAddEditViewModel @Inject constructor(
    private val upsertCategory: UpsertCategory,
    private val deleteCategory: DeleteCategory,
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
                        initialCategory = category,
                        showType = intent.showType,
                        hasSelectedEmoji = category.emoji.isNotBlank(),
                        isLoading = false,
                        errorMessage = null,
                        colors = colors.map { it.copy(isSelected = it.color == category.color.color) }
                    )
                }
            }
            is CategoryAddEditIntent.UpdateName -> {
                updateState { copy(category = category.copy(name = intent.name), errorMessage = null) }
            }
            is CategoryAddEditIntent.UpdateDescription -> {
                updateState {
                    copy(category = category.copy(description = intent.description), errorMessage = null)
                }
            }
            is CategoryAddEditIntent.UpdateEmoji -> {
                updateState {
                    copy(
                        category = category.copy(emoji = intent.emoji),
                        hasSelectedEmoji = intent.emoji.isNotBlank(),
                        errorMessage = null
                    )
                }
            }
            is CategoryAddEditIntent.SelectColor -> {
                updateState { 
                    copy(
                        category = category.copy(color = ColorItemUIModel(color = intent.color)),
                        colors = colors.map { it.copy(isSelected = it.color == intent.color) },
                        errorMessage = null
                    )
                }
            }
            is CategoryAddEditIntent.SaveCategory -> saveCategory()
            is CategoryAddEditIntent.DeleteCategory -> handleDeleteCategory()
            is CategoryAddEditIntent.Dismiss -> emitEffect(CategoryAddEditEffect.Dismiss)
        }
    }

    private fun handleDeleteCategory() {
        val category = currentState.category
        if (category.isDefault) {
            // deleting the default needs a replacement to be picked, which only the
            // category list offers
            showError("To delete the default category, use the delete button in the category list")
            return
        }
        if (currentState.showType != CategoryShowType.Edit || category.id <= 0) {
            showError("This category cannot be deleted")
            return
        }

        updateState { copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                deleteCategory.invoke(category)
                emitEffect(CategoryAddEditEffect.CategoryDeleted(category.name))
                emitEffect(CategoryAddEditEffect.Dismiss)
            } catch (e: Exception) {
                Timber.e(e, "Failed to delete category")
                updateState { copy(isLoading = false) }
                showError("Failed to delete category")
            }
        }
    }

    private fun saveCategory() {
        if (!currentState.isFilled) {
            showError("Fill category name, description and emoji")
            return
        }
        if (!currentState.hasChanges) {
            showError("Nothing to update, change a field first")
            return
        }

        updateState { copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val saved = currentState.category
                upsertCategory.invoke(saved)
                // the saved model becomes the new baseline so a reopened sheet starts clean
                updateState { copy(initialCategory = saved, category = saved, isLoading = false) }
                emitEffect(CategoryAddEditEffect.CategorySaved)
                emitEffect(CategoryAddEditEffect.Dismiss)
            } catch (e: Exception) {
                Timber.e(e, "Failed to save category")
                updateState { copy(isLoading = false) }
                showError("Failed to save category")
            }
        }
    }

    private fun showError(message: String) {
        updateState { copy(errorMessage = message) }
        emitEffect(CategoryAddEditEffect.ShowError(message))
    }
}
