package com.duhapp.dnotes.features.settings.ui

import com.duhapp.dnotes.foundation.mvi.UiEffect
import com.duhapp.dnotes.foundation.mvi.UiIntent
import com.duhapp.dnotes.foundation.mvi.UiState

data class ExportState(
    val isEncryptionEnabled: Boolean = true,
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
) : UiState {
    /** A password free export needs no input at all, an encrypted one needs a usable password. */
    val canExport: Boolean
        get() = !isEncryptionEnabled || password.length >= MIN_PASSWORD_LENGTH

    companion object {
        const val MIN_PASSWORD_LENGTH = 4
    }
}

sealed interface ExportIntent : UiIntent {
    data class UpdatePassword(val password: String) : ExportIntent
    data class ToggleEncryption(val enabled: Boolean) : ExportIntent
    object TogglePasswordVisibility : ExportIntent
    object StartExport : ExportIntent
    object NavigationBack : ExportIntent
    data class OnExportFileSelected(val uri: String) : ExportIntent
}

sealed interface ExportEffect : UiEffect {
    object NavigateBack : ExportEffect
    data class TriggerFilePicker(val suggestedFileName: String) : ExportEffect
}
