package com.duhapp.dnotes.features.settings.data

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

/** A backup file as it was read from storage. */
data class BackupFileContent(
    val name: String?,
    val body: String
)

/**
 * Reads and writes backup files through the documents the user picks with the system
 * file picker, so the feature needs no storage permission.
 */
class BackupFileDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun read(uriString: String): BackupFileContent = withContext(Dispatchers.IO) {
        val uri = Uri.parse(uriString)
        val body = context.contentResolver.openInputStream(uri)?.use { input ->
            input.readBytes().toString(Charsets.UTF_8)
        } ?: throw IOException("Selected file could not be opened for reading")
        BackupFileContent(name = displayName(uri), body = body)
    }

    suspend fun write(uriString: String, body: String) = withContext(Dispatchers.IO) {
        val uri = Uri.parse(uriString)
        // "wt" truncates: picking an existing file must not leave a tail of the old backup.
        val stream = context.contentResolver.openOutputStream(uri, "wt")
            ?: throw IOException("Selected file could not be opened for writing")
        stream.use { output ->
            output.write(body.toByteArray(Charsets.UTF_8))
            output.flush()
        }
    }

    private fun displayName(uri: Uri): String? = runCatching {
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getString(0) else null
            }
    }.getOrNull() ?: uri.lastPathSegment?.substringAfterLast('/')
}
