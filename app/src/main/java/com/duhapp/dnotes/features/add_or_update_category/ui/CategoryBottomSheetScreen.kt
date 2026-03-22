package com.duhapp.dnotes.features.add_or_update_category.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duhapp.dnotes.NoteColor
import com.duhapp.dnotes.features.note.ui.getDarkColorFromOrdinal
import com.duhapp.dnotes.features.note.ui.getLightColorFromOrdinal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryBottomSheetScreen(
    categoryUIModel: CategoryUIModel,
    categoryShowType: CategoryShowType,
    onSave: (CategoryUIModel) -> Unit,
    onDismiss: () -> Unit,
    viewModel: CategoryBottomSheetViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val successState = (uiState as? CategoryBottomSheetUIState.Success)
    val colors = successState?.colors ?: NoteColor.entries.map {
        ColorItemUIModel(isSelected = it == categoryUIModel.color.color, color = it)
    }
    val category = successState?.categoryUIModel ?: categoryUIModel

    var nameText by remember(categoryUIModel.id) { mutableStateOf(category.name) }
    var descriptionText by remember(categoryUIModel.id) { mutableStateOf(category.description) }

    LaunchedEffect(Unit) {
        viewModel.setViewWithBundle(categoryUIModel.copy(), categoryShowType)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is CategoryUIEvent.Upserted -> {
                    val updatedCategory = category.copy(name = nameText, description = descriptionText)
                    onSave(updatedCategory)
                }
                else -> {}
            }
        }
    }

    val backgroundColor = getLightColorFromOrdinal(category.color.color.ordinal)
    val buttonColor = getDarkColorFromOrdinal(category.color.color.ordinal)
    val textColor = Color.White

    ModalBottomSheet(
        onDismissRequest = {
            viewModel.onDismissed()
            onDismiss()
        },
        sheetState = bottomSheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Fill the category info and change color and emoji",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.3f))
                        .clickable { viewModel.onEmojiClicked() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category.emoji.ifEmpty { "😀" },
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                OutlinedTextField(
                    value = nameText,
                    onValueChange = {
                        nameText = it
                        viewModel.onNameChanged(it)
                    },
                    label = { Text("Category Name") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.Gray
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = descriptionText,
                onValueChange = {
                    descriptionText = it
                    viewModel.onDescriptionChanged(it)
                },
                label = { Text("Category Description") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.Gray.copy(alpha = 0.5f))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Choose a color",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(colors) { colorItem ->
                    ColorCircle(
                        colorOrdinal = colorItem.color.ordinal,
                        isSelected = colorItem.isSelected,
                        onClick = {
                            viewModel.onColorSelected(colorItem)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.onSaveButtonClick()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (categoryShowType == CategoryShowType.Add) "Add Category" else "Edit Category",
                        color = textColor
                    )
                }

                Button(
                    onClick = {
                        viewModel.onCancelButtonClick()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Cancel",
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun ColorCircle(
    colorOrdinal: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = getDarkColorFromOrdinal(colorOrdinal)

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .then(
                if (isSelected) {
                    Modifier.border(3.dp, Color.Blue, CircleShape)
                } else Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
