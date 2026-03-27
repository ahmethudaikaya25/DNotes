package com.duhapp.dnotes.features.settings.ui

import androidx.lifecycle.viewModelScope
import com.duhapp.dnotes.features.base.data.UserPreferencesRepository
import com.duhapp.dnotes.foundation.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : MviViewModel<SettingsIntent, SettingsState, SettingsEffect>(SettingsState()) {

    init {
        userPreferencesRepository.darkModeFlow.onEach { mode ->
            updateState { copy(darkMode = mode) }
        }.launchIn(viewModelScope)
    }

    override fun processIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ChangeDarkMode -> {
                viewModelScope.launch {
                    userPreferencesRepository.updateDarkMode(intent.mode)
                }
            }
            is SettingsIntent.OnExportClick -> emitEffect(SettingsEffect.NavigateToExport)
            is SettingsIntent.OnImportClick -> emitEffect(SettingsEffect.NavigateToImport)
            is SettingsIntent.NavigationBack -> emitEffect(SettingsEffect.NavigateBack)
        }
    }
}
