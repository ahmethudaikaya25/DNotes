package com.duhapp.dnotes.features.settings.data

import androidx.room.withTransaction
import com.duhapp.dnotes.app.database.AppDatabase
import com.duhapp.dnotes.app.database.CategoryDao
import com.duhapp.dnotes.app.database.CategoryEntity
import com.duhapp.dnotes.app.database.NoteDao
import com.duhapp.dnotes.app.database.NoteEntity
import javax.inject.Inject

/** What a single [NoteExporter.restoreBackup] call actually changed. */
data class ImportSummary(
    val newCategories: Int,
    val newNotes: Int,
    val skippedNotes: Int
)

class NoteExporter @Inject constructor(
    private val database: AppDatabase,
    private val categoryDao: CategoryDao,
    private val noteDao: NoteDao
) {
    /**
     * Serializes every category and note into the body of a backup file.
     * A null or empty [password] produces a readable, unencrypted backup.
     */
    suspend fun createBackup(password: CharArray?): String {
        val exportData = collectExportData()
        val envelope = if (password == null || password.isEmpty()) {
            BackupEnvelope(format = BACKUP_FORMAT, encrypted = false, data = exportData)
        } else {
            BackupEnvelope(
                format = BACKUP_FORMAT,
                encrypted = true,
                kdfIterations = ExportEncryption.ITERATIONS,
                payload = ExportEncryption.encrypt(BackupFormat.encode(exportData), password)
            )
        }
        return BackupFormat.write(envelope)
    }

    /**
     * True when [content] can only be read with a password. Throws
     * [InvalidBackupFileException] when the file is not a DNotes backup at all.
     */
    fun requiresPassword(content: String): Boolean = BackupFormat.read(content).encrypted

    /**
     * Merges the backup in [content] into the database. Categories are matched by name and
     * notes that are already present are skipped, so importing the same file twice does
     * not duplicate anything.
     */
    suspend fun restoreBackup(content: String, password: CharArray?): ImportSummary {
        val envelope = BackupFormat.read(content)
        val importData = if (envelope.encrypted) {
            val payload = envelope.payload
                ?: throw InvalidBackupFileException("Encrypted backup carries no data")
            if (password == null || password.isEmpty()) throw InvalidBackupPasswordException()
            val decrypted = ExportEncryption.decrypt(
                encryptedBase64 = payload,
                password = password,
                iterations = envelope.kdfIterations ?: ExportEncryption.ITERATIONS
            )
            BackupFormat.readExportData(decrypted)
        } else {
            envelope.data ?: throw InvalidBackupFileException("Backup carries no data")
        }
        return applyImport(importData)
    }

    private suspend fun collectExportData(): ExportData {
        val categories = categoryDao.getCategories().sortedBy { it.sortOrder }
        val notes = categories.flatMap { category ->
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
        return ExportData(
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
            notes = notes
        )
    }

    private suspend fun applyImport(importData: ExportData): ImportSummary =
        database.withTransaction {
            val existingCategories = categoryDao.getCategories()
            val categoryIdsByName = existingCategories
                .associateTo(mutableMapOf()) { it.name to it.id }
            var hasDefaultCategory = existingCategories.any { it.isDefault }
            var newCategories = 0

            importData.categories.forEach { category ->
                if (categoryIdsByName.containsKey(category.name)) return@forEach
                val claimsDefault = category.isDefault && !hasDefaultCategory
                val id = categoryDao.insert(
                    CategoryEntity(
                        name = category.name,
                        message = category.message,
                        emoji = category.emoji,
                        colorId = category.colorId,
                        // Only one category may be the default one and the local setup wins.
                        isDefault = claimsDefault,
                        sortOrder = category.sortOrder
                    )
                ).toInt()
                if (claimsDefault) hasDefaultCategory = true
                categoryIdsByName[category.name] = id
                newCategories++
            }

            // Notes of a category the backup does not describe land in the default one
            // instead of being thrown away.
            val fallbackCategoryId = existingCategories.firstOrNull { it.isDefault }?.id
                ?: categoryIdsByName.values.firstOrNull()

            val knownNotes = categoryIdsByName.values
                .flatMap { categoryId -> noteDao.getNoteByCategoryId(categoryId) }
                .mapTo(mutableSetOf()) {
                    NoteKey(it.categoryId, it.title, it.details, it.createdAt)
                }

            var newNotes = 0
            var skippedNotes = 0
            importData.notes.forEach { note ->
                val categoryId = categoryIdsByName[note.categoryName] ?: fallbackCategoryId
                if (categoryId == null) {
                    skippedNotes++
                    return@forEach
                }
                val key = NoteKey(categoryId, note.title, note.details, note.createdAt)
                if (!knownNotes.add(key)) {
                    // Already in the database, importing the same backup twice is a no-op.
                    skippedNotes++
                    return@forEach
                }
                noteDao.insert(
                    NoteEntity(
                        title = note.title,
                        details = note.details,
                        categoryId = categoryId,
                        isPinned = note.isPinned,
                        createdAt = note.createdAt,
                        updatedAt = note.updatedAt
                    )
                )
                newNotes++
            }

            ImportSummary(
                newCategories = newCategories,
                newNotes = newNotes,
                skippedNotes = skippedNotes
            )
        }

    private data class NoteKey(
        val categoryId: Int,
        val title: String,
        val details: String,
        val createdAt: Long
    )
}
