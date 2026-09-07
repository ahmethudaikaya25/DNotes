package com.duhapp.dnotes.features.manage_category.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel

/**
 * Asks which of the remaining categories should take over the default role before the current
 * default category is deleted. Confirming is blocked until a replacement is picked.
 *
 * @param category The default category about to be deleted.
 * @param candidates The categories that can become the new default (never empty).
 * @param onConfirm Receives the id of the category chosen as the new default.
 * @param onDismiss Called when the dialog is cancelled.
 */
@Composable
fun DeleteDefaultCategoryDialog(
    category: CategoryUIModel,
    candidates: List<CategoryUIModel>,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedCategoryId by remember(category.id) { mutableStateOf<Int?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Delete '${category.name}'",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column {
                Text(
                    text = "This is your default category. Pick the category that takes over " +
                        "as default — its notes will be moved there as well.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.padding(top = 8.dp))
                LazyColumn(
                    modifier = Modifier.heightIn(max = 260.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(items = candidates, key = { it.id }) { candidate ->
                        CandidateRow(
                            candidate = candidate,
                            isSelected = candidate.id == selectedCategoryId,
                            onSelect = { selectedCategoryId = candidate.id }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { selectedCategoryId?.let(onConfirm) },
                enabled = selectedCategoryId != null
            ) {
                Text(text = "Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun CandidateRow(
    candidate: CategoryUIModel,
    isSelected: Boolean,
    onSelect: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = isSelected, onClick = onSelect)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            RadioButton(selected = isSelected, onClick = onSelect)
            Text(text = candidate.emoji, fontSize = 18.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = candidate.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
