package com.duhapp.dnotes.features.note.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.duhapp.dnotes.foundation.theme.toPalette
import com.duhapp.dnotes.foundation.uicomponents.BaseScreenScaffold
import com.duhapp.dnotes.foundation.uicomponents.CategoryChip
import com.duhapp.dnotes.foundation.uicomponents.ConfirmDialog
import com.duhapp.dnotes.foundation.uicomponents.EmptyStateView
import com.duhapp.dnotes.foundation.uicomponents.LoadingScreen
import com.duhapp.dnotes.foundation.uicomponents.SelectCategorySheet

@Composable
fun NoteEditorScreenRoute(
    noteId: Int?,
    onNavigateBack: () -> Unit,
    viewModel: NoteViewModel = hiltViewModel()
) {
    LaunchedEffect(noteId) {
        viewModel.processIntent(NoteIntent.LoadNote(noteId))
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    MviScreen(
        viewModel = viewModel,
        onEffect = { effect ->
            when (effect) {
                is NoteEffect.NavigateBack -> onNavigateBack()
                is NoteEffect.ShowDeleteConfirmation -> showDeleteDialog = true
                is NoteEffect.ShowToast -> Unit
            }
        }
    ) { state ->
        NoteScreen(
            state = state,
            onIntent = viewModel::processIntent
        )

        if (showDeleteDialog) {
            ConfirmDialog(
                title = "Delete Note",
                message = "Are you sure you want to delete this note?",
                onConfirm = {
                    showDeleteDialog = false
                    viewModel.processIntent(NoteIntent.ConfirmDeleteNote)
                },
                onDismiss = {
                    showDeleteDialog = false
                }
            )
        }
    }
}

@Composable
fun NoteScreen(
    state: NoteState,
    onIntent: (NoteIntent) -> Unit
) {
    val noteColorEnum = state.note?.colorCode ?: NoteColor.RED
    val palette = noteColorEnum.toPalette()

    BaseScreenScaffold(
        showBackButton = true,
        onBackClick = { onIntent(NoteIntent.NavigationBack) },
        title = "Note Editor",
        containerColor = palette.container,
        contentColor = palette.onContainer,
        topBarActions = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Saved",
                tint = palette.onContainer.copy(alpha = 0.6f),
                modifier = Modifier.padding(end = 8.dp)
            )

            if (state.note != null) {
                IconButton(onClick = { onIntent(NoteIntent.DeleteNote) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Note",
                        tint = palette.onContainer
                    )
                }
            }
        }
    ) { paddingValues ->
        when {
            state.isLoading -> LoadingScreen(modifier = Modifier.padding(paddingValues))
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
                    category = state.note.category,
                    textColor = palette.onContainer,
                    categoryAccentColor = palette.accent,
                    categoryAccentContentColor = palette.onAccent,
                    onTitleChange = { onIntent(NoteIntent.UpdateTitle(it)) },
                    onBodyChange = { onIntent(NoteIntent.UpdateBody(it)) },
                    onCategoryClick = { onIntent(NoteIntent.ToggleCategorySheet(true)) },
                    modifier = Modifier.padding(paddingValues)
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
    categoryAccentColor: androidx.compose.ui.graphics.Color,
    categoryAccentContentColor: androidx.compose.ui.graphics.Color,
    category: CategoryUIModel?,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onCategoryClick: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .imePadding()
    ) {
        if (category != null) {
            CategoryChip(
                emoji = category.emoji,
                name = category.name,
                backgroundColor = categoryAccentColor,
                textColor = categoryAccentContentColor,
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 8.dp)
                    .clickable { onCategoryClick() }
            )
        }

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
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 8.dp),
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

        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(onClick = { }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Bold", tint = textColor)
            }
            IconButton(onClick = { }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Italic", tint = textColor)
            }
            IconButton(onClick = { }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = "List", tint = textColor)
            }
        }
    }
}
