package com.duhapp.dnotes.features.manage_category.ui

import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryShowType
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.base.ui.mvi.MviEffect
import com.duhapp.dnotes.features.base.ui.mvi.MviIntent
import com.duhapp.dnotes.features.base.ui.mvi.MviState

data class ManageCategoryScreenState(
    val isLoading: Boolean = false,
    val categories: List<CategoryUIModel> = emptyList(),
    val error: String? = null
) : MviState

sealed class ManageCategoryScreenIntent : MviIntent {
    data object LoadCategories : ManageCategoryScreenIntent()
    data class CategoryClicked(val category: CategoryUIModel) : ManageCategoryScreenIntent()
    data object AddCategoryClicked : ManageCategoryScreenIntent()
    data class DeleteCategory(val category: CategoryUIModel) : ManageCategoryScreenIntent()
}

sealed class ManageCategoryScreenEffect : MviEffect {
    data class NavigateToCategoryBottomSheet(
        val category: CategoryUIModel,
        val showType: CategoryShowType
    ) : ManageCategoryScreenEffect()
    data class ShowError(val message: String) : ManageCategoryScreenEffect()
    data class ShowDeleteConfirmation(val category: CategoryUIModel) : ManageCategoryScreenEffect()
}
