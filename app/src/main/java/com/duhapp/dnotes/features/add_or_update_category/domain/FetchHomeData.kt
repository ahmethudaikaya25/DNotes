package com.duhapp.dnotes.features.add_or_update_category.domain

import com.duhapp.dnotes.app.database.CategoryDao
import com.duhapp.dnotes.app.database.NoteDao
import com.duhapp.dnotes.features.add_or_update_category.ui.toUIModel
import com.duhapp.dnotes.features.home.home_screen_category.ui.HomeCategoryUIModel
import com.duhapp.dnotes.features.home.home_screen_category.ui.toUIModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class FetchHomeData(
    private val noteDao: NoteDao,
    private val categoryDao: CategoryDao
) {
    operator fun invoke(): Flow<List<HomeCategoryUIModel>> {
        return combine(categoryDao.getAll(), noteDao.getAll()) { categoryEntities, noteEntities ->
            val categoriesById = categoryEntities.associateBy { it.id }

            noteEntities
                .groupBy { it.categoryId }
                .mapNotNull { (categoryId, notes) ->
                    val categoryEntity = categoriesById[categoryId] ?: return@mapNotNull null
                    val category = categoryEntity.toUIModel()
                    HomeCategoryUIModel(
                        id = category.id,
                        title = category.name,
                        noteList = notes.map { it.toUIModel(category) }
                    )
                }
        }
    }
}
