package com.duhapp.dnotes.features.notifications.ui

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import com.duhapp.dnotes.foundation.uicomponents.BaseScreenScaffold
import com.duhapp.dnotes.foundation.uicomponents.EmptyStateView

@Composable
fun NotificationsScreenRoute() {
    BaseScreenScaffold(
        title = "Notifications"
    ) { paddingValues ->
        EmptyStateView(
            title = "Coming Soon",
            message = "Notifications feature is under development.",
            modifier = Modifier.padding(paddingValues)
        )
    }
}
