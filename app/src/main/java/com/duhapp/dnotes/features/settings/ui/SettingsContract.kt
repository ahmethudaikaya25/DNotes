package com.duhapp.dnotes.features.settings.ui

import com.duhapp.dnotes.foundation.mvi.UiEffect
import com.duhapp.dnotes.foundation.mvi.UiIntent
import com.duhapp.dnotes.foundation.mvi.UiState

data class SettingsState(
    val darkMode: String = "AUTO", // AUTO, LIGHT, DARK
    val isLoading: Boolean = false
) : UiState

sealed interface SettingsIntent : UiIntent {
    data class ChangeDarkMode(val mode: String) : SettingsIntent
    object OnExportClick : SettingsIntent
    object OnImportClick : SettingsIntent
    object NavigationBack : SettingsIntent
}

sealed interface SettingsEffect : UiEffect {
    object NavigateBack : SettingsEffect
    object NavigateToExport : SettingsEffect
    object NavigateToImport : SettingsEffect
}
