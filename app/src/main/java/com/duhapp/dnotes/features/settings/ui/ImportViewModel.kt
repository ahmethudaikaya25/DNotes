package com.duhapp.dnotes.features.settings.ui

import androidx.lifecycle.viewModelScope
import com.duhapp.dnotes.features.settings.data.ExportEncryption
import com.duhapp.dnotes.features.settings.data.NoteExporter
import com.duhapp.dnotes.foundation.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ImportViewModel @Inject constructor(
    private val noteExporter: NoteExporter
) : MviViewModel<ImportIntent, ImportState, ImportEffect>(ImportState()) {

    override fun processIntent(intent: ImportIntent) {
        when (intent) {
            is ImportIntent.UpdatePassword -> updateState { copy(password = intent.password) }
            is ImportIntent.TogglePasswordVisibility -> updateState { copy(isPasswordVisible = !isPasswordVisible) }
            is ImportIntent.StartImport -> emitEffect(ImportEffect.TriggerFilePicker)
            is ImportIntent.OnImportFileSelected -> importNotes(intent.uri)
            is ImportIntent.NavigationBack -> emitEffect(ImportEffect.NavigateBack)
        }
    }

    private fun importNotes(uri: String) {
        updateState { copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                // Read from URI placeholder: In real app, we use contentResolver.openInputStream()
                val encryptedData = "placeholder_from_uri" 
                
                val decryptedJson = ExportEncryption.decrypt(encryptedData, currentState.password.toCharArray())
                noteExporter.importFromJson(decryptedJson)
                
                emitEffect(ImportEffect.ShowSuccess("Portfolio imported successfully"))
                updateState { copy(isLoading = false, successMessage = "Portfolio restored!") }
            } catch (e: Exception) {
                Timber.e(e, "Import failed")
                updateState { copy(isLoading = false, errorMessage = "Import failed. Check password or file.") }
            }
        }
    }
}
