package com.duhapp.dnotes.features.note.domain

import com.duhapp.dnotes.app.database.CategoryDao
import com.duhapp.dnotes.app.database.NoteDao
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.home.home_screen_category.ui.BasicNoteUIModel
import com.duhapp.dnotes.features.home.home_screen_category.ui.ImageNoteUIModel
import com.duhapp.dnotes.features.home.home_screen_category.ui.BaseNoteUIModel
import com.duhapp.dnotes.features.home.home_screen_category.ui.DEFAULT_NOTE_MODEL
import com.duhapp.dnotes.features.add_or_update_category.ui.ColorItemUIModel
import com.duhapp.dnotes.NoteColor
import javax.inject.Inject

class GetNoteById @Inject constructor(
    private val noteDao: NoteDao,
    private val categoryDao: CategoryDao
) {
    suspend operator fun invoke(id: Int): BaseNoteUIModel? {
        val noteEntity = noteDao.getNoteById(id) ?: return null
        val categoryEntity = categoryDao.getById(noteEntity.categoryId)

        val categoryUIModel = if (categoryEntity != null) {
            CategoryUIModel(
                id = categoryEntity.id,
                name = categoryEntity.name,
                emoji = categoryEntity.emoji,
                description = categoryEntity.message,
                color = ColorItemUIModel(color = NoteColor.fromOrdinal(categoryEntity.colorId))
            )
        } else {
            DEFAULT_NOTE_MODEL.category
        }

        return BasicNoteUIModel(
            id = noteEntity.id,
            isPinned = false, // Not in NoteEntity yet (Sprint 6)
            isCompleted = false,
            isCompletable = false,
            category = categoryUIModel,
            title = noteEntity.title,
            body = noteEntity.details,
            colorCode = categoryUIModel.color.color
        )
    }
}
