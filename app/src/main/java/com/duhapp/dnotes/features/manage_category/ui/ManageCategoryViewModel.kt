package com.duhapp.dnotes.features.manage_category.ui

import androidx.lifecycle.viewModelScope
import com.duhapp.dnotes.features.add_or_update_category.domain.DeleteCategory
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.manage_category.domain.GetCategories
import com.duhapp.dnotes.features.manage_category.domain.UndoCategory
import com.duhapp.dnotes.foundation.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ManageCategoryViewModel @Inject constructor(
    private val getCategories: GetCategories,
    private val deleteCategory: DeleteCategory,
    private val undoCategory: UndoCategory,
) : MviViewModel<ManageCategoryIntent, ManageCategoryState, ManageCategoryEffect>(ManageCategoryState()) {

    init {
        processIntent(ManageCategoryIntent.LoadCategories)
    }

    override fun processIntent(intent: ManageCategoryIntent) {
        when (intent) {
            is ManageCategoryIntent.LoadCategories -> loadCategories()
            is ManageCategoryIntent.OnCategoryClick -> {
                emitEffect(ManageCategoryEffect.ShowAddEditCategorySheet(intent.category))
            }
            is ManageCategoryIntent.OnDeleteCategory -> handleDeleteCategory(intent.category)
            is ManageCategoryIntent.OnCategoryDeleted -> {
                emitEffect(ManageCategoryEffect.ShowDeleteSuccess(intent.categoryName))
                loadCategories()
            }
            is ManageCategoryIntent.OnUndoDelete -> handleUndoDelete()
            is ManageCategoryIntent.OnAddCategoryClick -> {
                emitEffect(ManageCategoryEffect.ShowAddEditCategorySheet(null))
            }
            is ManageCategoryIntent.NavigationBack -> {
                emitEffect(ManageCategoryEffect.NavigateBack)
            }
        }
    }

    private fun loadCategories() {
        updateState { copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val list = getCategories.invoke()
                updateState { copy(isLoading = false, categories = list) }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load categories")
                updateState { copy(isLoading = false, errorMessage = "Failed to load categories") }
            }
        }
    }

    private fun handleDeleteCategory(category: CategoryUIModel) {
        viewModelScope.launch {
            try {
                deleteCategory.invoke(category)
                emitEffect(ManageCategoryEffect.ShowDeleteSuccess(category.name))
                loadCategories()
            } catch (e: Exception) {
                Timber.e(e, "Failed to delete category")
                emitEffect(ManageCategoryEffect.ShowError("Failed to delete category"))
            }
        }
    }

    private fun handleUndoDelete() {
        viewModelScope.launch {
            try {
                undoCategory.invoke()
                loadCategories()
            } catch (e: Exception) {
                Timber.e(e, "Failed to undo category deletion")
                emitEffect(ManageCategoryEffect.ShowError("Undo failed"))
            }
        }
    }
}
