package com.duhapp.dnotes.features.manage_category.ui

import androidx.lifecycle.viewModelScope
import com.duhapp.dnotes.R
import com.duhapp.dnotes.features.add_or_update_category.domain.DeleteCategory
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryShowType
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.base.ui.mvi.MviViewModel
import com.duhapp.dnotes.features.manage_category.domain.GetCategories
import com.duhapp.dnotes.features.manage_category.domain.UndoCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ManageCategoryViewModel @Inject constructor(
    private val getCategories: GetCategories,
    private val deleteCategory: DeleteCategory,
    private val undoCategory: UndoCategory,
) : MviViewModel<ManageCategoryScreenIntent, ManageCategoryScreenState, ManageCategoryScreenEffect>(
    initialState = ManageCategoryScreenState()
) {

    init {
        processIntent(ManageCategoryScreenIntent.LoadCategories)
    }

    override fun handleIntent(intent: ManageCategoryScreenIntent) {
        when (intent) {
            is ManageCategoryScreenIntent.LoadCategories -> loadCategories()
            is ManageCategoryScreenIntent.CategoryClicked -> handleCategoryClicked(intent.category)
            is ManageCategoryScreenIntent.AddCategoryClicked -> handleAddCategoryClicked()
            is ManageCategoryScreenIntent.DeleteCategory -> handleDeleteCategory(intent.category)
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            try {
                val categories = getCategories.invoke()
                setState { copy(isLoading = false, categories = categories) }
            } catch (e: Exception) {
                Timber.e(e)
                setState { copy(isLoading = false, error = e.message ?: "Failed to load categories") }
            }
        }
    }

    private fun handleCategoryClicked(category: CategoryUIModel) {
        setEffect(
            ManageCategoryScreenEffect.NavigateToCategoryBottomSheet(
                category = category,
                showType = CategoryShowType.Edit
            )
        )
    }

    private fun handleAddCategoryClicked() {
        setEffect(
            ManageCategoryScreenEffect.NavigateToCategoryBottomSheet(
                category = CategoryUIModel(),
                showType = CategoryShowType.Add
            )
        )
    }

    private fun handleDeleteCategory(category: CategoryUIModel) {
        viewModelScope.launch {
            try {
                deleteCategory.invoke(category)
                setEffect(ManageCategoryScreenEffect.ShowDeleteConfirmation(category))
                loadCategories()
            } catch (e: Exception) {
                Timber.e(e)
                setState { copy(error = e.message ?: "Failed to delete category") }
            }
        }
    }

    fun onUndoDelete() {
        viewModelScope.launch {
            try {
                undoCategory.invoke()
                loadCategories()
            } catch (e: Exception) {
                Timber.e(e)
                setState { copy(error = e.message ?: "Failed to undo delete") }
            }
        }
    }

    fun onCategoryUpserted() {
        loadCategories()
    }
}
