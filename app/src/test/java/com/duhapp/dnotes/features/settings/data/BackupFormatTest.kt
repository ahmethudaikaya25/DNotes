package com.duhapp.dnotes.features.settings.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupFormatTest {

    private val exportData = ExportData(
        categories = listOf(
            CategoryExportModel(
                name = "Work",
                message = "Work notes",
                emoji = "💼",
                colorId = 3,
                isDefault = true,
                sortOrder = 0
            )
        ),
        notes = listOf(
            NoteExportModel(
                title = "Standup",
                details = "Talk about the export bug",
                categoryName = "Work",
                isPinned = true,
                createdAt = 1_700_000_000_000,
                updatedAt = 1_700_000_100_000
            )
        ),
        exportedAt = 1_700_000_200_000
    )

    @Test
    fun `plain backup survives a write and read round trip`() {
        val envelope = BackupFormat.read(
            BackupFormat.write(
                BackupEnvelope(format = BACKUP_FORMAT, encrypted = false, data = exportData)
            )
        )

        assertFalse(envelope.encrypted)
        assertNull(envelope.payload)
        assertEquals(exportData, envelope.data)
    }

    @Test
    fun `encrypted backup is recognised without touching the payload`() {
        val envelope = BackupFormat.read(
            BackupFormat.write(
                BackupEnvelope(
                    format = BACKUP_FORMAT,
                    encrypted = true,
                    kdfIterations = 120_000,
                    payload = "not-real-ciphertext"
                )
            )
        )

        assertTrue(envelope.encrypted)
        assertEquals(120_000, envelope.kdfIterations)
        assertEquals("not-real-ciphertext", envelope.payload)
    }

    @Test
    fun `a bare export body is read as an unencrypted backup`() {
        val envelope = BackupFormat.read(BackupFormat.encode(exportData))

        assertFalse(envelope.encrypted)
        assertEquals(exportData, envelope.data)
    }

    @Test
    fun `unknown keys do not break an otherwise valid backup`() {
        val body = """
            {
              "format": "$BACKUP_FORMAT",
              "version": 1,
              "encrypted": false,
              "somethingFromTheFuture": true,
              "data": { "categories": [], "notes": [] }
            }
        """.trimIndent()

        val envelope = BackupFormat.read(body)

        assertEquals(emptyList<NoteExportModel>(), envelope.data?.notes)
    }

    @Test(expected = InvalidBackupFileException::class)
    fun `an empty file is rejected`() {
        BackupFormat.read("   \n ")
    }

    @Test(expected = InvalidBackupFileException::class)
    fun `a foreign json file is rejected`() {
        BackupFormat.read("""{ "some": "other file" }""")
    }

    @Test(expected = InvalidBackupFileException::class)
    fun `a file with a foreign format marker is rejected`() {
        BackupFormat.read(
            """{ "format": "some-other-app", "version": 1, "encrypted": false }"""
        )
    }

    @Test(expected = InvalidBackupFileException::class)
    fun `a backup from a newer version is rejected`() {
        BackupFormat.read(
            """{ "format": "$BACKUP_FORMAT", "version": ${BACKUP_VERSION + 1}, "encrypted": false }"""
        )
    }
}
