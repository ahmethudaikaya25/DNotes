package com.duhapp.dnotes.features.manage_category.domain

import com.duhapp.dnotes.features.add_or_update_category.data.CategoryRepository
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel

class CreateDefaultCategory(
    private val categoryRepository: CategoryRepository,
    private val defaultCategoryModel: CategoryUIModel
) {
    suspend fun invoke() {
        if (categoryRepository.getDefaultCategory() != null) return

        // no category carries the default flag: either the database is empty and needs the
        // seed category, or an existing category has to take the role over
        val categories = categoryRepository.getCategories()
        if (categories.isEmpty()) {
            categoryRepository.insert(defaultCategoryModel)
        } else {
            categoryRepository.setDefaultCategory(categories.first().id)
        }
    }
}
