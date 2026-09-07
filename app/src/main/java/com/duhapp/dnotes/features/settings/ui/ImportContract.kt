package com.duhapp.dnotes.features.settings.ui

import com.duhapp.dnotes.foundation.mvi.UiEffect
import com.duhapp.dnotes.foundation.mvi.UiIntent
import com.duhapp.dnotes.foundation.mvi.UiState

data class ImportState(
    val fileName: String? = null,
    /** Set once the picked file turns out to be encrypted, which is when we ask for a password. */
    val requiresPassword: Boolean = false,
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
) : UiState

sealed interface ImportIntent : UiIntent {
    data class UpdatePassword(val password: String) : ImportIntent
    object TogglePasswordVisibility : ImportIntent
    object StartImport : ImportIntent
    object ConfirmImport : ImportIntent
    object CancelPasswordEntry : ImportIntent
    object NavigationBack : ImportIntent
    data class OnImportFileSelected(val uri: String) : ImportIntent
}

sealed interface ImportEffect : UiEffect {
    object NavigateBack : ImportEffect
    object TriggerFilePicker : ImportEffect
}
