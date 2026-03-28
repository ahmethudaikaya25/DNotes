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
fun ImportScreenRoute(
    onNavigateBack: () -> Unit,
    viewModel: ImportViewModel = hiltViewModel()
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let { viewModel.processIntent(ImportIntent.OnImportFileSelected(it.toString())) }
        }
    )

    MviScreen(
        viewModel = viewModel,
        onEffect = { effect ->
            when (effect) {
                is ImportEffect.NavigateBack -> onNavigateBack()
                is ImportEffect.TriggerFilePicker -> launcher.launch(arrayOf("application/octet-stream"))
                is ImportEffect.ShowError -> { /* Handle Error Toast */ }
                is ImportEffect.ShowSuccess -> { /* Handle Success Toast */ }
            }
        }
    ) { state ->
        ImportScreen(
            state = state,
            onIntent = viewModel::processIntent
        )
    }
}

@Composable
fun ImportScreen(
    state: ImportState,
    onIntent: (ImportIntent) -> Unit
) {
    BaseScreenScaffold(
        title = "Import Portfolio",
        showBackButton = true,
        onBackClick = { onIntent(ImportIntent.NavigationBack) }
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
                    text = "Decrypt Backup",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Please enter the password used at export to decrypt your backup file.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = state.password,
                    onValueChange = { onIntent(ImportIntent.UpdatePassword(it)) },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { onIntent(ImportIntent.TogglePasswordVisibility) }) {
                            Icon(
                                imageVector = if (state.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle Visibility"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { onIntent(ImportIntent.StartImport) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.password.length >= 4
                ) {
                    Text(text = "Select File & Import")
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
