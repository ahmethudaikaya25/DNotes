package com.duhapp.dnotes.features.settings.ui

import androidx.lifecycle.viewModelScope
import com.duhapp.dnotes.features.settings.data.BackupFileDataSource
import com.duhapp.dnotes.features.settings.data.ImportSummary
import com.duhapp.dnotes.features.settings.data.InvalidBackupFileException
import com.duhapp.dnotes.features.settings.data.InvalidBackupPasswordException
import com.duhapp.dnotes.features.settings.data.NoteExporter
import com.duhapp.dnotes.foundation.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ImportViewModel @Inject constructor(
    private val noteExporter: NoteExporter,
    private val backupFileDataSource: BackupFileDataSource
) : MviViewModel<ImportIntent, ImportState, ImportEffect>(ImportState()) {

    /** Body of the picked file, kept while the user types the password for it. */
    private var pendingBackup: String? = null

    override fun processIntent(intent: ImportIntent) {
        when (intent) {
            is ImportIntent.UpdatePassword -> updateState {
                copy(password = intent.password, errorMessage = null)
            }

            is ImportIntent.TogglePasswordVisibility -> updateState {
                copy(isPasswordVisible = !isPasswordVisible)
            }

            is ImportIntent.StartImport -> {
                updateState { copy(errorMessage = null, successMessage = null) }
                emitEffect(ImportEffect.TriggerFilePicker)
            }

            is ImportIntent.OnImportFileSelected -> loadBackup(intent.uri)
            is ImportIntent.ConfirmImport -> confirmImport()
            is ImportIntent.CancelPasswordEntry -> {
                pendingBackup = null
                updateState {
                    copy(
                        fileName = null,
                        requiresPassword = false,
                        password = "",
                        isPasswordVisible = false,
                        errorMessage = null
                    )
                }
            }

            is ImportIntent.NavigationBack -> emitEffect(ImportEffect.NavigateBack)
        }
    }

    private fun loadBackup(uri: String) {
        updateState {
            copy(
                isLoading = true,
                requiresPassword = false,
                password = "",
                isPasswordVisible = false,
                errorMessage = null,
                successMessage = null
            )
        }
        viewModelScope.launch {
            val file = try {
                backupFileDataSource.read(uri)
            } catch (e: Exception) {
                Timber.e(e, "Reading the backup file failed")
                updateState {
                    copy(
                        isLoading = false,
                        errorMessage = "The selected file could not be read."
                    )
                }
                return@launch
            }

            val needsPassword = try {
                noteExporter.requiresPassword(file.body)
            } catch (e: InvalidBackupFileException) {
                updateState { copy(isLoading = false, fileName = file.name, errorMessage = e.message) }
                return@launch
            }

            if (needsPassword) {
                // Encrypted backups stop here until the user supplies the password.
                pendingBackup = file.body
                updateState {
                    copy(isLoading = false, fileName = file.name, requiresPassword = true)
                }
            } else {
                updateState { copy(fileName = file.name) }
                restore(file.body, password = null)
            }
        }
    }

    private fun confirmImport() {
        val backup = pendingBackup
        if (backup == null) {
            updateState { copy(errorMessage = "Please select a backup file first.") }
            return
        }
        if (currentState.password.isEmpty()) {
            updateState { copy(errorMessage = "Please enter the password of this backup.") }
            return
        }
        viewModelScope.launch {
            restore(backup, currentState.password.toCharArray())
        }
    }

    private suspend fun restore(content: String, password: CharArray?) {
        updateState { copy(isLoading = true, errorMessage = null, successMessage = null) }
        try {
            val summary = noteExporter.restoreBackup(content, password)
            pendingBackup = null
            updateState {
                copy(
                    isLoading = false,
                    requiresPassword = false,
                    password = "",
                    isPasswordVisible = false,
                    successMessage = summaryMessage(summary)
                )
            }
        } catch (e: InvalidBackupPasswordException) {
            updateState {
                copy(isLoading = false, errorMessage = "Wrong password, or the file is damaged.")
            }
        } catch (e: InvalidBackupFileException) {
            updateState { copy(isLoading = false, errorMessage = e.message) }
        } catch (e: Exception) {
            Timber.e(e, "Import failed")
            updateState { copy(isLoading = false, errorMessage = "Import failed. Please try again.") }
        } finally {
            password?.fill(' ')
        }
    }

    private fun summaryMessage(summary: ImportSummary): String = buildString {
        append("Imported ${summary.newNotes} notes")
        if (summary.newCategories > 0) {
            append(" and ${summary.newCategories} categories")
        }
        append(".")
        if (summary.skippedNotes > 0) {
            append(" ${summary.skippedNotes} notes were already here and were skipped.")
        }
    }
}
