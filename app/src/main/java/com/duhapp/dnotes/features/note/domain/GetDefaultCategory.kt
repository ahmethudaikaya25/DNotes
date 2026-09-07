package com.duhapp.dnotes.features.note.domain

import com.duhapp.dnotes.features.add_or_update_category.data.CategoryRepository
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel

class GetDefaultCategory(
    private val categoryRepository: CategoryRepository,
    private val fallbackCategory: CategoryUIModel
) {
    suspend fun invoke(): CategoryUIModel {
        // the default category is whichever row carries the flag; it is not a fixed id
        // anymore, since the original default can be deleted and replaced by another one
        return categoryRepository.getDefaultCategory()
            ?: categoryRepository.getCategories().firstOrNull()
            ?: fallbackCategory
    }
}
