package com.duhapp.dnotes.features.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.duhapp.dnotes.foundation.mvi.MviScreen
import com.duhapp.dnotes.foundation.uicomponents.BaseScreenScaffold

@Composable
fun SettingsScreenRoute(
    onNavigateBack: () -> Unit,
    onNavigateToExport: () -> Unit,
    onNavigateToImport: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    MviScreen(
        viewModel = viewModel,
        onEffect = { effect ->
            when (effect) {
                is SettingsEffect.NavigateBack -> onNavigateBack()
                is SettingsEffect.NavigateToExport -> onNavigateToExport()
                is SettingsEffect.NavigateToImport -> onNavigateToImport()
            }
        }
    ) { state ->
        SettingsScreen(
            state = state,
            onIntent = viewModel::processIntent
        )
    }
}

@Composable
fun SettingsScreen(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit
) {
    BaseScreenScaffold(
        title = "Settings",
        showBackButton = true,
        onBackClick = { onIntent(SettingsIntent.NavigationBack) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            DarkModeItem("Auto", "AUTO", state.darkMode) { onIntent(SettingsIntent.ChangeDarkMode("AUTO")) }
            DarkModeItem("Light", "LIGHT", state.darkMode) { onIntent(SettingsIntent.ChangeDarkMode("LIGHT")) }
            DarkModeItem("Dark", "DARK", state.darkMode) { onIntent(SettingsIntent.ChangeDarkMode("DARK")) }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text(
                text = "Data Sync",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            SyncItem(
                title = "Export Portfolio",
                description = "Export your notes to an encrypted file.",
                icon = Icons.Default.Download,
                onClick = { onIntent(SettingsIntent.OnExportClick) }
            )
            SyncItem(
                title = "Import Portfolio",
                description = "Import your notes from an encrypted file.",
                icon = Icons.Default.Upload,
                onClick = { onIntent(SettingsIntent.OnImportClick) }
            )
        }
    }
}

@Composable
private fun DarkModeItem(label: String, mode: String, currentMode: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        RadioButton(selected = currentMode == mode, onClick = onClick)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun SyncItem(title: String, description: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
