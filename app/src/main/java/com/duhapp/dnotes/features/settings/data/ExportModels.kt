package com.duhapp.dnotes.features.settings.data

import kotlinx.serialization.Serializable

/** Marker written into every backup file so foreign json files can be rejected. */
const val BACKUP_FORMAT = "dnotes-backup"

/** Highest backup layout this build is able to read. */
const val BACKUP_VERSION = 1

/**
 * Self describing wrapper around [ExportData].
 *
 * A password free backup keeps the notes in [data] so the file stays readable, an
 * encrypted one keeps the encrypted [ExportData] json in [payload]. [encrypted] lets the
 * importer decide whether it has to ask for a password before it opens the file.
 *
 * [format] deliberately has no default: without it kotlinx would happily deserialize any
 * json object into an empty envelope instead of failing.
 */
@Serializable
data class BackupEnvelope(
    val format: String,
    val version: Int = BACKUP_VERSION,
    val encrypted: Boolean = false,
    val kdfIterations: Int? = null,
    val data: ExportData? = null,
    val payload: String? = null
)

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
