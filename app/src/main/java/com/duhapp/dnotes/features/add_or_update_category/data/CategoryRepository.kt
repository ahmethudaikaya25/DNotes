package com.duhapp.dnotes.features.add_or_update_category.data

import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel

interface CategoryRepository {
    suspend fun insert(categoryUIModel: CategoryUIModel): Int

    /**
     * Moves every note of [categoryUIModel] to [targetCategory] and removes the category.
     * When the deleted category is the default one, [targetCategory] becomes the new default.
     */
    suspend fun deleteCategory(categoryUIModel: CategoryUIModel, targetCategory: CategoryUIModel)
    suspend fun updateCategory(categoryUIModel: CategoryUIModel)
    suspend fun getCategories(): List<CategoryUIModel>
    suspend fun getById(id: Int): CategoryUIModel?
    suspend fun getDefaultCategory(): CategoryUIModel?
    suspend fun setDefaultCategory(id: Int)
    suspend fun undo(): Boolean
}
