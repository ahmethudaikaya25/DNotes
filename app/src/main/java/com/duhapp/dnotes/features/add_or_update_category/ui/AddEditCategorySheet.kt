package com.duhapp.dnotes.features.add_or_update_category.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.duhapp.dnotes.foundation.mvi.MviScreen
import com.duhapp.dnotes.foundation.theme.toPalette
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
    var showEmojiDialog by remember { mutableStateOf(false) }

    LaunchedEffect(category, showType) {
        viewModel.processIntent(CategoryAddEditIntent.Init(category, showType))
    }

    MviScreen(
        viewModel = viewModel,
        onEffect = { effect ->
            when (effect) {
                is CategoryAddEditEffect.CategorySaved -> onSaved()
                is CategoryAddEditEffect.Dismiss -> onDismissRequest()
                is CategoryAddEditEffect.ShowError -> Unit
            }
        }
    ) { state ->
        BaseModalSheet(onDismissRequest = { viewModel.processIntent(CategoryAddEditIntent.Dismiss) }) {
            AddEditCategoryContent(
                state = state,
                onIntent = viewModel::processIntent,
                onEmojiClick = { showEmojiDialog = true }
            )
        }

        if (showEmojiDialog) {
            EmojiKeyboardDialog(
                selectedEmoji = state.category.emoji,
                onDismiss = { showEmojiDialog = false },
                onEmojiSelected = { emoji ->
                    viewModel.processIntent(CategoryAddEditIntent.UpdateEmoji(emoji))
                    showEmojiDialog = false
                }
            )
        }
    }
}

@Composable
private fun AddEditCategoryContent(
    state: CategoryAddEditState,
    onIntent: (CategoryAddEditIntent) -> Unit,
    onEmojiClick: () -> Unit
) {
    val title = if (state.showType == CategoryShowType.Add) "Add Category" else "Edit Category"
    val palette = state.category.color.color.toPalette()
    val isSaveEnabled = state.category.name.isNotBlank() &&
        state.category.description.isNotBlank() &&
        state.category.emoji.isNotBlank() &&
        !state.isLoading

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(84.dp)
                .clip(CircleShape)
                .background(palette.accent)
                .clickable(onClick = onEmojiClick)
        ) {
            Text(text = state.category.emoji.ifBlank { "🙂" }, fontSize = 34.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

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
            minLines = 2,
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Select Color",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(12.dp))

        ColorSelectorRow(
            colors = state.colors.map { it.color },
            selectedColor = state.category.color.color,
            onColorSelected = { onIntent(CategoryAddEditIntent.SelectColor(it)) }
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = { onIntent(CategoryAddEditIntent.SaveCategory) },
            enabled = isSaveEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.accent,
                contentColor = palette.onAccent,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = if (state.showType == CategoryShowType.Add) "Create" else "Update",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

private data class EmojiGroup(
    val title: String,
    val emojis: List<String>
)

private val emojiGroups = listOf(
    EmojiGroup("Smileys", listOf("😀", "😊", "😍", "🥳", "🤩", "😎", "🫶", "🤓", "😴", "🤯", "🥹", "😇")),
    EmojiGroup("Nature", listOf("🌿", "🌸", "🌻", "🍀", "🌵", "🌊", "☀️", "🌙", "🔥", "🌈", "⭐", "❄️")),
    EmojiGroup("Places", listOf("🏠", "🏡", "🏢", "🏫", "🏥", "🏖️", "🏕️", "🗽", "🗼", "⛩️", "🏛️", "🛣️")),
    EmojiGroup("Objects", listOf("📚", "💼", "🧠", "💡", "🛠️", "🧾", "📌", "🖥️", "📷", "🎧", "📦", "🧸")),
    EmojiGroup("Food", listOf("☕", "🍔", "🍕", "🍜", "🍎", "🍋", "🍩", "🍪", "🍷", "🍰", "🍓", "🥑")),
    EmojiGroup("Travel", listOf("🚗", "✈️", "🚆", "🚌", "🚲", "🧭", "🗺️", "🏝️", "⛰️", "⛺", "🚤", "🎡"))
)

@Composable
private fun EmojiKeyboardDialog(
    selectedEmoji: String,
    onDismiss: () -> Unit,
    onEmojiSelected: (String) -> Unit
) {
    var selectedGroupIndex by remember { mutableIntStateOf(0) }
    val selectedGroup = emojiGroups[selectedGroupIndex]

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {
                Text(
                    text = "Select Emoji",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(emojiGroups.size) { index ->
                        FilterChip(
                            selected = index == selectedGroupIndex,
                            onClick = { selectedGroupIndex = index },
                            label = { Text(emojiGroups[index].title) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier.height(280.dp)
                ) {
                    items(selectedGroup.emojis) { emoji ->
                        val isSelected = emoji == selectedEmoji
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .size(52.dp)
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { onEmojiSelected(emoji) }
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 26.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Cancel",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(onClick = onDismiss)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }
}
