package com.duhapp.dnotes.features.settings.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.duhapp.dnotes.foundation.mvi.MviScreen
import com.duhapp.dnotes.foundation.uicomponents.BaseScreenScaffold
import com.duhapp.dnotes.foundation.uicomponents.LoadingScreen

@Composable
fun ExportScreenRoute(
    onNavigateBack: () -> Unit,
    viewModel: ExportViewModel = hiltViewModel()
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream"),
        onResult = { uri ->
            uri?.let { viewModel.processIntent(ExportIntent.OnExportFileSelected(it.toString())) }
        }
    )

    MviScreen(
        viewModel = viewModel,
        onEffect = { effect ->
            when (effect) {
                is ExportEffect.NavigateBack -> onNavigateBack()
                is ExportEffect.TriggerFilePicker -> launcher.launch("dnotes_backup.dn")
                is ExportEffect.ShowSuccess -> { /* Snackbar success */ }
                is ExportEffect.ShowError -> { /* Snackbar error */ }
            }
        }
    ) { state ->
        ExportScreen(
            state = state,
            onIntent = viewModel::processIntent
        )
    }
}

@Composable
fun ExportScreen(
    state: ExportState,
    onIntent: (ExportIntent) -> Unit
) {
    BaseScreenScaffold(
        title = "Export Portfolio",
        showBackButton = true,
        onBackClick = { onIntent(ExportIntent.NavigationBack) }
    ) { paddingValues ->
        if (state.isLoading) {
            LoadingScreen(modifier = Modifier.padding(paddingValues))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp)
            ) {
                Text(
                    text = "Security Password",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "This password will be used to encrypt your portfolio. You MUST remember it to import your data later.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = state.password,
                    onValueChange = { onIntent(ExportIntent.UpdatePassword(it)) },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { onIntent(ExportIntent.TogglePasswordVisibility) }) {
                            Icon(
                                imageVector = if (state.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle Visibility"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { onIntent(ExportIntent.StartExport) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.password.length >= 4
                ) {
                    Text(text = "Choose Path & Export")
                }

                if (state.errorMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = state.errorMessage, color = MaterialTheme.colorScheme.error)
                }

                if (state.successMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = state.successMessage, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
