package com.duhapp.dnotes.features.add_or_update_category.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.duhapp.dnotes.foundation.mvi.MviScreen
import com.duhapp.dnotes.foundation.theme.toComposeColors
import com.duhapp.dnotes.foundation.uicomponents.BaseModalSheet
import com.duhapp.dnotes.foundation.uicomponents.ColorSelectorRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCategorySheet(
    category: CategoryUIModel?,
    showType: CategoryShowType,
    onDismissRequest: () -> Unit,
    onSaved: () -> Unit,
    viewModel: CategoryAddEditViewModel = hiltViewModel()
) {
    LaunchedEffect(category, showType) {
        viewModel.processIntent(CategoryAddEditIntent.Init(category, showType))
    }

    MviScreen(
        viewModel = viewModel,
        onEffect = { effect ->
            when (effect) {
                is CategoryAddEditEffect.CategorySaved -> onSaved()
                is CategoryAddEditEffect.Dismiss -> onDismissRequest()
                is CategoryAddEditEffect.ShowError -> { /* Handle error toast */ }
            }
        }
    ) { state ->
        BaseModalSheet(onDismissRequest = { viewModel.processIntent(CategoryAddEditIntent.Dismiss) }) {
            AddEditCategoryContent(
                state = state,
                onIntent = viewModel::processIntent
            )
        }
    }
}

@Composable
private fun AddEditCategoryContent(
    state: CategoryAddEditState,
    onIntent: (CategoryAddEditIntent) -> Unit
) {
    val title = if (state.showType == CategoryShowType.Add) "Add Category" else "Edit Category"
    val (colorDark) = state.category.color.color.toComposeColors()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Emoji selection box (Clickable to show picker, for now placeholder)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .clickable { /* Emoji Picker Dialog */ }
        ) {
            Surface(
                shape = CircleShape,
                color = colorDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)) {
                    Text(text = state.category.emoji.ifBlank { "❓" }, fontSize = 32.sp)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.category.name,
            onValueChange = { onIntent(CategoryAddEditIntent.UpdateName(it)) },
            label = { Text("Category Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.category.description,
            onValueChange = { onIntent(CategoryAddEditIntent.UpdateDescription(it)) },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Select Color",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.align(Alignment.Start)
        )

        ColorSelectorRow(
            colors = state.colors.map { it.color.toComposeColors().first },
            selectedColor = state.category.color.color.toComposeColors().first,
            onColorSelected = { color ->
                val selectedNoteColor = state.colors.find { it.color.toComposeColors().first == color }?.color
                if (selectedNoteColor != null) {
                    onIntent(CategoryAddEditIntent.SelectColor(selectedNoteColor))
                }
            },
            modifier = Modifier.padding(vertical = 12.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onIntent(CategoryAddEditIntent.SaveCategory) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            Text(if (state.showType == CategoryShowType.Add) "Create" else "Update")
        }
    }
}

@Composable
private fun Surface(shape: androidx.compose.ui.graphics.Shape, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    androidx.compose.material3.Surface(shape = shape, color = color, modifier = modifier) { content() }
}
