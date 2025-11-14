package com.duhapp.dnotes.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.duhapp.dnotes.features.home.HomeUIEvent
import com.duhapp.dnotes.features.home.HomeUIState
import com.duhapp.dnotes.features.home.HomeViewModel
import com.duhapp.dnotes.ui.components.CategoryCard

@Composable
fun HomeScreen(
    onNoteClick: (Int) -> Unit,
    onViewAllClick: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val uiEvent by viewModel.uiEvent.collectAsState(initial = null)

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }

    LaunchedEffect(uiEvent) {
        when (val event = uiEvent) {
            is HomeUIEvent.OnNoteClicked -> {
                onNoteClick(event.noteUIModel.id)
            }
            is HomeUIEvent.OnViewAllClicked -> {
                onViewAllClick(event.homeCategoryUIModel.id)
            }
            else -> {}
        }
    }

    when (val state = uiState) {
        is HomeUIState.Success -> {
            if (state.categories.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No categories yet.\nCreate a category to get started!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.categories) { category ->
                        CategoryCard(
                            category = category,
                            onNoteClick = { note ->
                                viewModel.onNoteClick(note)
                            },
                            onViewAllClick = {
                                viewModel.onCategoryViewAllClicked(category)
                            }
                        )
                    }
                }
            }
        }

        is HomeUIState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error loading categories",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
