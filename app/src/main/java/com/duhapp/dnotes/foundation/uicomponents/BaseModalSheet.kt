package com.duhapp.dnotes.foundation.uicomponents

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A standard Modal Bottom Sheet wrapper for DNotes.
 * Used for actions like selecting a category, adding a category, etc.
 *
 * @param onDismissRequest Action to perform when the bottom sheet is dismissed by the user.
 * @param modifier Modifier for styling or layout.
 * @param content The composable content to display inside the bottom sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseModalSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    ) {
        content()
    }
}
