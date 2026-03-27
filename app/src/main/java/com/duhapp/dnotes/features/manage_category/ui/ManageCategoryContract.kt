package com.duhapp.dnotes.features.manage_category.ui

import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.foundation.mvi.UiEffect
import com.duhapp.dnotes.foundation.mvi.UiIntent
import com.duhapp.dnotes.foundation.mvi.UiState

data class ManageCategoryState(
    val isLoading: Boolean = false,
    val categories: List<CategoryUIModel> = emptyList(),
    val errorMessage: String? = null
) : UiState

sealed interface ManageCategoryIntent : UiIntent {
    object LoadCategories : ManageCategoryIntent
    data class OnCategoryClick(val category: CategoryUIModel) : ManageCategoryIntent
    data class OnDeleteCategory(val category: CategoryUIModel) : ManageCategoryIntent
    object OnAddCategoryClick : ManageCategoryIntent
    object NavigationBack : ManageCategoryIntent
}

sealed interface ManageCategoryEffect : UiEffect {
    object NavigateBack : ManageCategoryEffect
    data class ShowAddEditCategorySheet(val category: CategoryUIModel? = null) : ManageCategoryEffect
    object ShowDeleteSuccess : ManageCategoryEffect
    data class ShowError(val message: String) : ManageCategoryEffect
}
