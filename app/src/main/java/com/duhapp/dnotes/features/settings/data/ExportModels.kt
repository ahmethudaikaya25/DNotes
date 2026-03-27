package com.duhapp.dnotes.features.settings.data

import kotlinx.serialization.Serializable

@Serializable
data class ExportData(
    val categories: List<CategoryExportModel>,
    val notes: List<NoteExportModel>,
    val version: Int = 1,
    val exportedAt: Long = System.currentTimeMillis()
)

@Serializable
data class CategoryExportModel(
    val name: String,
    val message: String,
    val emoji: String,
    val colorId: Int,
    val isDefault: Boolean,
    val sortOrder: Int
)

@Serializable
data class NoteExportModel(
    val title: String,
    val details: String,
    val categoryName: String, // We use name for mapping back on import
    val isPinned: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)
