package com.duhapp.dnotes.features.note.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.duhapp.dnotes.NoteColor
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.foundation.mvi.MviScreen
import com.duhapp.dnotes.foundation.theme.toComposeColors
import com.duhapp.dnotes.foundation.uicomponents.BaseScreenScaffold
import com.duhapp.dnotes.foundation.uicomponents.CategoryChip
import com.duhapp.dnotes.foundation.uicomponents.EmptyStateView
import com.duhapp.dnotes.foundation.uicomponents.LoadingScreen

@Composable
fun NoteEditorScreenRoute(
    noteId: Int?,
    onNavigateBack: () -> Unit,
    viewModel: NoteViewModel = hiltViewModel()
) {
    LaunchedEffect(noteId) {
        viewModel.processIntent(NoteIntent.LoadNote(noteId))
    }

    MviScreen(
        viewModel = viewModel,
        onEffect = { effect ->
            when (effect) {
                is NoteEffect.NavigateBack -> onNavigateBack()
                is NoteEffect.ShowDeleteConfirmation -> {
                    // Handled in Story 3.3
                }
                is NoteEffect.ShowToast -> {
                    // Typically show a real toast or snackbar
                }
            }
        }
    ) { state ->
        NoteScreen(
            state = state,
            onIntent = viewModel::processIntent
        )
    }
}

@Composable
fun NoteScreen(
    state: NoteState,
    onIntent: (NoteIntent) -> Unit
) {
    // We derive dynamic colors from the selected category inside the note payload
    val noteColorEnum = state.note?.color?.let { NoteColor.fromOrdinal(it) } ?: NoteColor.RED
    val (colorDark, colorLight, textColor) = noteColorEnum.toComposeColors()

    BaseScreenScaffold(
        showBackButton = true,
        onBackClick = { onIntent(NoteIntent.NavigationBack) },
        modifier = Modifier.background(colorLight)
    ) { paddingValues ->
        when {
            state.isLoading -> {
                LoadingScreen(modifier = Modifier.padding(paddingValues))
            }
            state.errorMessage != null -> {
                EmptyStateView(
                    title = "Error",
                    message = state.errorMessage,
                    icon = Icons.Default.Warning,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            state.note != null -> {
                NoteEditorContent(
                    title = state.note.title,
                    body = state.note.body,
                    textColor = textColor,
                    category = state.note.category,
                    onTitleChange = { onIntent(NoteIntent.UpdateTitle(it)) },
                    onBodyChange = { onIntent(NoteIntent.UpdateBody(it)) },
                    onCategoryClick = { onIntent(NoteIntent.ToggleCategorySheet(true)) },
                    paddingValues = paddingValues
                )
            }
        }
        
        SelectCategorySheet(
            isVisible = state.isCategorySheetVisible,
            categories = state.availableCategories,
            onCategorySelected = { onIntent(NoteIntent.ChangeCategory(it)) },
            onDismissRequest = { onIntent(NoteIntent.ToggleCategorySheet(false)) }
        )
    }
}

@Composable
private fun NoteEditorContent(
    title: String,
    body: String,
    textColor: androidx.compose.ui.graphics.Color,
    category: CategoryUIModel?,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onCategoryClick: () -> Unit,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp)
            .imePadding() // Adjust for keyboard
    ) {
        // Category Chip (opens category sheet)
        if (category != null) {
            CategoryChip(
                icon = category.emoji,
                label = category.name,
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 8.dp)
                    .clickable { onCategoryClick() }
            )
        }

        // Title Input
        BasicTextField(
            value = title,
            onValueChange = onTitleChange,
            textStyle = TextStyle(
                color = textColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = MaterialTheme.typography.titleLarge.fontFamily
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            cursorBrush = SolidColor(textColor),
            decorationBox = { innerTextField ->
                Box {
                    if (title.isEmpty()) {
                        Text(
                            text = "Title",
                            style = TextStyle(
                                color = textColor.copy(alpha = 0.5f),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    innerTextField()
                }
            }
        )

        // Body Input
        BasicTextField(
            value = body,
            onValueChange = onBodyChange,
            textStyle = TextStyle(
                color = textColor,
                fontSize = 18.sp,
                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                lineHeight = 28.sp
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp, bottom = 16.dp),
            cursorBrush = SolidColor(textColor),
            decorationBox = { innerTextField ->
                Box {
                    if (body.isEmpty()) {
                        Text(
                            text = "Type something...",
                            style = TextStyle(
                                color = textColor.copy(alpha = 0.5f),
                                fontSize = 18.sp
                            )
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}
