package com.duhapp.dnotes.features.settings.data

import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Turns backup file bodies into a [BackupEnvelope] and back. Kept apart from
 * [NoteExporter] so the format itself can be exercised without a database.
 */
internal object BackupFormat {

    private val json = Json {
        prettyPrint = true
        // A password free backup is a plain text file the user may edit by hand, so be
        // forgiving about extra keys instead of failing the whole import.
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun write(envelope: BackupEnvelope): String = json.encodeToString(envelope)

    fun encode(exportData: ExportData): String = json.encodeToString(exportData)

    fun read(content: String): BackupEnvelope {
        val trimmed = content.trim()
        if (trimmed.isEmpty()) throw InvalidBackupFileException("The selected file is empty")

        val envelope = try {
            json.decodeFromString<BackupEnvelope>(trimmed)
        } catch (e: SerializationException) {
            // BackupEnvelope.format has no default, so a foreign json object always lands
            // here. Retry as a bare ExportData body, which is what a hand written or
            // externally generated plain backup looks like.
            BackupEnvelope(
                format = BACKUP_FORMAT,
                encrypted = false,
                data = readExportData(trimmed)
            )
        }

        if (envelope.format != BACKUP_FORMAT) {
            throw InvalidBackupFileException("The selected file is not a DNotes backup")
        }
        if (envelope.version > BACKUP_VERSION) {
            throw InvalidBackupFileException("This backup was created by a newer version of DNotes")
        }
        return envelope
    }

    fun readExportData(raw: String): ExportData = try {
        json.decodeFromString<ExportData>(raw)
    } catch (e: SerializationException) {
        throw InvalidBackupFileException("The selected file is not a DNotes backup", e)
    }
}
