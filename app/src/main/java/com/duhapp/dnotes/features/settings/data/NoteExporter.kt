package com.duhapp.dnotes.features.settings.data

import com.duhapp.dnotes.app.database.CategoryDao
import com.duhapp.dnotes.app.database.NoteDao
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class NoteExporter @Inject constructor(
    private val categoryDao: CategoryDao,
    private val noteDao: NoteDao
) {
    suspend fun exportToJson(): String {
        val categories = categoryDao.getCategories()
        val allNotes = categories.flatMap { category ->
            noteDao.getNoteByCategoryId(category.id).map { note ->
                NoteExportModel(
                    title = note.title,
                    details = note.details,
                    categoryName = category.name,
                    isPinned = note.isPinned,
                    createdAt = note.createdAt,
                    updatedAt = note.updatedAt
                )
            }
        }

        val exportData = ExportData(
            categories = categories.map {
                CategoryExportModel(
                    name = it.name,
                    message = it.message,
                    emoji = it.emoji,
                    colorId = it.colorId,
                    isDefault = it.isDefault,
                    sortOrder = it.sortOrder
                )
            },
            notes = allNotes
        )

        return Json.encodeToString(exportData)
    }

    suspend fun importFromJson(jsonString: String) {
        val importData = Json.decodeFromString<ExportData>(jsonString)
        
        // 1. Import or map categories
        val existingCategories = categoryDao.getCategories().associateBy { it.name }
        val categoryMap = mutableMapOf<String, Int>()

        importData.categories.forEach { cat ->
            val existing = existingCategories[cat.name]
            if (existing == null) {
                val newId = categoryDao.insert(
                    com.duhapp.dnotes.app.database.CategoryEntity(
                        name = cat.name,
                        message = cat.message,
                        emoji = cat.emoji,
                        colorId = cat.colorId,
                        isDefault = cat.isDefault,
                        sortOrder = cat.sortOrder
                    )
                ).toInt()
                categoryMap[cat.name] = newId
            } else {
                categoryMap[cat.name] = existing.id
            }
        }

        // 2. Import notes
        importData.notes.forEach { note ->
            val categoryId = categoryMap[note.categoryName] ?: -1
            if (categoryId != -1) {
                noteDao.insert(
                    com.duhapp.dnotes.app.database.NoteEntity(
                        title = note.title,
                        details = note.details,
                        categoryId = categoryId,
                        isPinned = note.isPinned,
                        createdAt = note.createdAt,
                        updatedAt = note.updatedAt
                    )
                )
            }
        }
    }
}
