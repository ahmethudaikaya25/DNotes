package com.duhapp.dnotes.features.all_notes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.duhapp.dnotes.NoteColor
import com.duhapp.dnotes.foundation.mvi.MviScreen
import com.duhapp.dnotes.foundation.theme.toComposeColors
import com.duhapp.dnotes.foundation.uicomponents.BaseScreenScaffold
import com.duhapp.dnotes.foundation.uicomponents.CategoryChip
import com.duhapp.dnotes.foundation.uicomponents.EmptyStateView
import com.duhapp.dnotes.foundation.uicomponents.LoadingScreen
import com.duhapp.dnotes.foundation.uicomponents.NoteCard
import com.duhapp.dnotes.foundation.uicomponents.SelectCategorySheet

@Composable
fun AllNotesScreenRoute(
    categoryId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToNote: (Int) -> Unit,
    viewModel: AllNotesViewModel = hiltViewModel()
) {
    LaunchedEffect(categoryId) {
        viewModel.processIntent(AllNotesIntent.LoadNotes(categoryId))
    }

    MviScreen(
        viewModel = viewModel,
        onEffect = { effect ->
            when (effect) {
                is AllNotesEffect.NavigateBack -> onNavigateBack()
                is AllNotesEffect.NavigateToNoteEditor -> onNavigateToNote(effect.noteId)
                is AllNotesEffect.ShowToast -> { /* Handle Toast */ }
                is AllNotesEffect.ShowMoveCategorySheet -> {
                    // This intent is now handled internally in ViewModel 
                    // by toggling state for the sheet directly.
                }
            }
        }
    ) { state ->
        AllNotesScreen(
            state = state,
            onIntent = viewModel::processIntent
        )
    }
}

@Composable
fun AllNotesScreen(
    state: AllNotesState,
    onIntent: (AllNotesIntent) -> Unit
) {
    val categoryName = state.category?.name ?: "All Notes"

    // If selection mode is active, override standard Title with count
    val title = if (state.isSelectionMode) {
        val count = state.notes.count { it.isSelected }
        "$count selected"
    } else {
        categoryName
    }

    BaseScreenScaffold(
        title = title,
        showBackButton = !state.isSelectionMode,
        onBackClick = {
            if (state.isSelectionMode) {
                onIntent(AllNotesIntent.CancelSelectionMode)
            } else {
                onIntent(AllNotesIntent.GoBack)
            }
        },
        topBarActions = {
            if (state.isSelectionMode) {
                IconButton(onClick = { onIntent(AllNotesIntent.CancelSelectionMode) }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel")
                }
                IconButton(onClick = { onIntent(AllNotesIntent.MoveSelectedNotes) }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Move")
                }
                IconButton(onClick = { onIntent(AllNotesIntent.DeleteSelectedNotes) }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        },
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
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
            state.notes.isEmpty() -> {
                EmptyStateView(
                    title = "No notes",
                    message = "Tap the + button to create a note.",
                    icon = Icons.Default.Warning,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalItemSpacing = 16.dp
                ) {
                    items(
                        items = state.notes,
                        key = { it.id }
                    ) { note ->
                        val noteColorEnum = note.colorCode
                        val (colorDark, colorLight, textColor) = noteColorEnum.toComposeColors()

                        NoteCard(
                            title = note.title,
                            body = note.body,
                            colorDark = colorDark,
                            colorLight = colorLight,
                            textColor = textColor,
                            isPinned = note.isPinned,
                            isSelected = note.isSelected,
                            onClick = { onIntent(AllNotesIntent.OnNoteClick(note)) },
                            onLongClick = { onIntent(AllNotesIntent.OnNoteLongClick(note)) }
                        )
                    }
                }
            }
        }

        SelectCategorySheet(
            isVisible = state.isMoveSheetVisible,
            categories = state.availableCategories,
            onCategorySelected = { onIntent(AllNotesIntent.OnCategorySelected(it)) },
            onDismissRequest = { onIntent(AllNotesIntent.ToggleMoveSheet(false)) }
        )
    }
}
