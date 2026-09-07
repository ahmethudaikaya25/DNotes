package com.duhapp.dnotes.features.add_or_update_category.ui

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.duhapp.dnotes.NoteColor
import com.duhapp.dnotes.features.add_or_update_category.domain.DeleteCategory
import com.duhapp.dnotes.features.add_or_update_category.domain.UpsertCategory
import com.duhapp.dnotes.features.base.domain.CustomException
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
                        errorMessageRes = null,
                        colors = colors.map { it.copy(isSelected = it.color == category.color.color) }
                    )
                }
            }
            is CategoryAddEditIntent.UpdateName -> {
                updateState {
                    copy(
                        category = category.copy(name = intent.name),
                        errorMessage = null,
                        errorMessageRes = null
                    )
                }
            }
            is CategoryAddEditIntent.UpdateDescription -> {
                updateState {
                    copy(
                        category = category.copy(description = intent.description),
                        errorMessage = null,
                        errorMessageRes = null
                    )
                }
            }
            is CategoryAddEditIntent.UpdateEmoji -> {
                updateState {
                    copy(
                        category = category.copy(emoji = intent.emoji),
                        hasSelectedEmoji = intent.emoji.isNotBlank(),
                        errorMessage = null,
                        errorMessageRes = null
                    )
                }
            }
            is CategoryAddEditIntent.SelectColor -> {
                updateState { 
                    copy(
                        category = category.copy(color = ColorItemUIModel(color = intent.color)),
                        colors = colors.map { it.copy(isSelected = it.color == intent.color) },
                        errorMessage = null,
                        errorMessageRes = null
                    )
                }
            }
            is CategoryAddEditIntent.SaveCategory -> saveCategory()
            is CategoryAddEditIntent.DeleteCategory ->
                handleDeleteCategory(intent.newDefaultCategoryId)
            is CategoryAddEditIntent.Dismiss -> emitEffect(CategoryAddEditEffect.Dismiss)
        }
    }

    /**
     * Deletes the edited category, the default one included. Deleting the default requires
     * [newDefaultCategoryId] to name the category that takes the role over; the sheet asks
     * for it before sending this intent.
     */
    private fun handleDeleteCategory(newDefaultCategoryId: Int?) {
        val category = currentState.category
        if (currentState.showType != CategoryShowType.Edit || category.id <= 0) {
            showError("This category cannot be deleted")
            return
        }

        updateState { copy(isLoading = true, errorMessage = null, errorMessageRes = null) }
        viewModelScope.launch {
            try {
                deleteCategory.invoke(category, newDefaultCategoryId)
                emitEffect(CategoryAddEditEffect.CategoryDeleted(category.name))
                emitEffect(CategoryAddEditEffect.Dismiss)
            } catch (e: CustomException) {
                Timber.e(e, "Failed to delete category")
                updateState { copy(isLoading = false) }
                showError(e.data.message)
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

        updateState { copy(isLoading = true, errorMessage = null, errorMessageRes = null) }
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
        updateState { copy(errorMessage = message, errorMessageRes = null) }
        emitEffect(CategoryAddEditEffect.ShowError(message))
    }

    /** Surfaces a failure the domain layer reported as a string resource. */
    private fun showError(@StringRes messageRes: Int) {
        updateState { copy(errorMessage = null, errorMessageRes = messageRes) }
    }
}
