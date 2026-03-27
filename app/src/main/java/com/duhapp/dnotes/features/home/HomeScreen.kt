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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.duhapp.dnotes.R
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
            IconButton(onClick = { /* TODO: Navigate to Search -> intent eventually */ }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Notes"
                )
            }
            IconButton(onClick = { /* TODO: Navigate to Settings */ }) {
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
            // Placeholder for Story 2.2 / 2.3 lists
            // Will render flat notes list or grouped categories based on state.groupBy
        }
    }
}
