package com.duhapp.dnotes.features.search.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.duhapp.dnotes.features.home.home_screen_category.ui.BasicNoteUIModel
import com.duhapp.dnotes.foundation.mvi.MviScreen
import com.duhapp.dnotes.foundation.theme.toComposeColors
import com.duhapp.dnotes.foundation.uicomponents.BaseScreenScaffold
import com.duhapp.dnotes.foundation.uicomponents.EmptyStateView
import com.duhapp.dnotes.foundation.uicomponents.LoadingScreen
import com.duhapp.dnotes.foundation.uicomponents.NoteCard

@Composable
fun SearchScreenRoute(
    onNavigateBack: () -> Unit,
    onNavigateToNote: (Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    MviScreen(
        viewModel = viewModel,
        onEffect = { effect ->
            when (effect) {
                is SearchEffect.NavigateBack -> onNavigateBack()
                is SearchEffect.NavigateToNote -> onNavigateToNote(effect.noteId)
            }
        }
    ) { state ->
        SearchScreen(
            state = state,
            onIntent = viewModel::processIntent
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchState,
    onIntent: (SearchIntent) -> Unit
) {
    BaseScreenScaffold(
        title = "", // Title handled by search bar
        showBackButton = false, // Back button handled by search bar
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchBar(
                query = state.query,
                onQueryChange = { onIntent(SearchIntent.Search(it)) },
                onSearch = { /* Handled real-time */ },
                active = true,
                onActiveChange = { if (!it) onIntent(SearchIntent.NavigationBack) },
                placeholder = { Text("Search title or body...") },
                leadingIcon = {
                    IconButton(onClick = { onIntent(SearchIntent.NavigationBack) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                trailingIcon = {
                    if (state.query.isNotEmpty()) {
                        IconButton(onClick = { onIntent(SearchIntent.Search("")) }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                colors = SearchBarDefaults.colors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp)
            ) {
                when {
                    state.isLoading -> LoadingScreen()
                    state.errorMessage != null -> {
                        EmptyStateView(
                            title = "Error",
                            message = state.errorMessage,
                            icon = Icons.Default.Search
                        )
                    }
                    state.results.isEmpty() && state.query.length > 1 -> {
                        EmptyStateView(
                            title = "No results",
                            message = "No notes found matching current search.",
                            icon = Icons.Default.Search
                        )
                    }
                    else -> {
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalItemSpacing = 12.dp
                        ) {
                            items(
                                items = state.results,
                                key = { it.id }
                            ) { note ->
                                val (colorDark, colorLight, textColor) = note.category.color.color.toComposeColors()
                                NoteCard(
                                    title = note.title,
                                    body = note.body,
                                    categoryEmoji = note.category.emoji,
                                    categoryName = note.category.name,
                                    colorDark = colorDark,
                                    colorLight = colorLight,
                                    textColor = textColor,
                                    isPinned = note.isPinned,
                                    onClick = { onIntent(SearchIntent.OnNoteClicked(note.id)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
