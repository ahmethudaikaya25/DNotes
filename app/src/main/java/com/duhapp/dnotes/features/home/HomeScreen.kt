package com.duhapp.dnotes.features.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import com.duhapp.dnotes.R
import com.duhapp.dnotes.features.home.components.GroupByToolbar
import com.duhapp.dnotes.features.home.components.SortByToolbar
import com.duhapp.dnotes.foundation.mvi.MviScreen
import com.duhapp.dnotes.foundation.navigation.Route
import com.duhapp.dnotes.foundation.uicomponents.BaseScreenScaffold
import com.duhapp.dnotes.foundation.uicomponents.EmptyStateView
import com.duhapp.dnotes.foundation.uicomponents.LoadingScreen

@Composable
fun HomeScreenRoute(
    onNavigate: (Route) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    MviScreen(
        viewModel = viewModel,
        onEffect = { effect ->
            when (effect) {
                is HomeEffect.NavigateToNote -> onNavigate(Route.NoteEditor(effect.noteId))
                is HomeEffect.NavigateToAllNotes -> onNavigate(Route.AllNotes(effect.categoryId))
                is HomeEffect.ShowToast -> { /* Handle toast if needed */ }
            }
        }
    ) { state ->
        HomeScreen(
            state = state,
            onIntent = viewModel::processIntent,
            onNavigate = onNavigate
        )
    }
}

@Composable
fun HomeScreen(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    onNavigate: (Route) -> Unit
) {
    BaseScreenScaffold(
        title = stringResource(id = R.string.title_home),
        topBarActions = {
            IconButton(onClick = { onNavigate(Route.Search) }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Notes"
                )
            }
            IconButton(onClick = { onNavigate(Route.Settings) }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onIntent(HomeIntent.OnAddNoteClicked) }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Note"
                )
            }
        }
    ) { paddingValues ->
        HomeContent(
            state = state,
            onIntent = onIntent,
            paddingValues = paddingValues
        )
    }
}

@Composable
private fun HomeContent(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    paddingValues: PaddingValues
) {
    val modifier = Modifier.padding(paddingValues)

    when {
        state.isLoading -> {
            LoadingScreen(modifier = modifier)
        }
        state.errorMessage != null -> {
            EmptyStateView(
                title = "Oops!",
                message = state.errorMessage,
                modifier = modifier,
                buttonText = "Retry",
                onButtonClick = { onIntent(HomeIntent.LoadContent) }
            )
        }
        state.notes.isEmpty() && state.categories.isEmpty() -> {
            EmptyStateView(
                title = "No Notes Yet",
                message = "Tap the + button to create your first note.",
                modifier = modifier
            )
        }
        else -> {
            Column(modifier = modifier.fillMaxSize()) {
                // To keep it clean, maybe just show them dynamically or side-by-side 
                // but since they are LazyRows, they must be separated into rows.
                SortByToolbar(
                    currentSort = state.sortBy,
                    onSortChanged = { sortBy -> onIntent(HomeIntent.OnSortByChanged(sortBy)) }
                )
                GroupByToolbar(
                    currentGroup = state.groupBy,
                    onGroupChanged = { groupBy -> onIntent(HomeIntent.OnGroupByChanged(groupBy)) }
                )

                if (state.groupBy == GroupBy.NONE) {
                    // Flat note list
                    com.duhapp.dnotes.features.home.components.NoteList(
                        notes = state.notes,
                        onNoteClick = { noteId -> onIntent(HomeIntent.OnNoteClicked(noteId)) },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    // Grouped by Category (Story 2.3)
                    com.duhapp.dnotes.features.home.components.GroupedNoteList(
                        categories = state.categories,
                        onNoteClick = { noteId -> onIntent(HomeIntent.OnNoteClicked(noteId)) },
                        onViewAllClick = { categoryId -> onIntent(HomeIntent.OnCategoryViewAllClicked(categoryId)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
