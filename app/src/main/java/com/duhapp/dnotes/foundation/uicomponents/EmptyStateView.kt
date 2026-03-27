package com.duhapp.dnotes.foundation.uicomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Standardized Empty State View for DNotes screens.
 * Displayed when there's no data (e.g. no notes, no search results).
 *
 * @param title The primary heading (e.g., "No notes found").
 * @param message An optional subtitle or instruction.
 * @param icon The visual illustration or icon to show. Uses a default info icon if null.
 * @param buttonText Text for the optional action button (e.g. "Create Note").
 * @param onButtonClick Action to perform if buttonText is provided.
 * @param modifier Modifier for layout styling. Defaults to full size and centered.
 */
@Composable
fun EmptyStateView(
    title: String,
    modifier: Modifier = Modifier,
    message: String? = null,
    icon: ImageVector? = Icons.Filled.Info,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = "Empty state illustration",
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        if (!message.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        if (buttonText != null && onButtonClick != null) {
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onButtonClick) {
                Text(text = buttonText)
            }
        }
    }
}
