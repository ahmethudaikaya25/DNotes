package com.duhapp.dnotes.foundation.uicomponents

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A shared Scaffold wrapper for DNotes screens.
 * Provides a standardized top app bar and ensures consistent spacing
 * and floating action button integration across the app.
 *
 * @param title The screen title to display in the top app bar. If empty, no top bar is shown.
 * @param showBackButton Whether a left-aligned back button should be shown.
 * @param onBackClick Callback triggered when the back button is pressed.
 * @param topBarActions Optional slot for trailing action icons in the top bar.
 * @param floatingActionButton Optional slot for a FAB.
 * @param modifier Modifier for styling or layout.
 * @param content The main screen content, receiving inner padding values from the Scaffold.
 */
@Composable
fun BaseScreenScaffold(
    title: String = "",
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    topBarActions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            if (title.isNotEmpty() || showBackButton) {
                DNotesTopBar(
                    title = title,
                    showBackButton = showBackButton,
                    onBackClick = onBackClick,
                    actions = topBarActions
                )
            }
        },
        floatingActionButton = floatingActionButton,
        content = content
    )
}
