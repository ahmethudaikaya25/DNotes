package com.duhapp.dnotes.features.settings.ui

import com.duhapp.dnotes.foundation.mvi.UiEffect
import com.duhapp.dnotes.foundation.mvi.UiIntent
import com.duhapp.dnotes.foundation.mvi.UiState

data class ExportState(
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
) : UiState

sealed interface ExportIntent : UiIntent {
    data class UpdatePassword(val password: String) : ExportIntent
    object TogglePasswordVisibility : ExportIntent
    object StartExport : ExportIntent
    object NavigationBack : ExportIntent
    data class OnExportFileSelected(val uri: String) : ExportIntent
}

sealed interface ExportEffect : UiEffect {
    object NavigateBack : ExportEffect
    object TriggerFilePicker : ExportEffect
    data class ShowError(val message: String) : ExportEffect
    data class ShowSuccess(val message: String) : ExportEffect
}
