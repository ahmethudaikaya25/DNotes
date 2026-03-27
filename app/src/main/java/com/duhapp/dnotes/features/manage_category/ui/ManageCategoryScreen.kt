package com.duhapp.dnotes.features.manage_category.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.foundation.mvi.MviScreen
import com.duhapp.dnotes.foundation.uicomponents.BaseScreenScaffold
import com.duhapp.dnotes.foundation.uicomponents.CategoryCard
import com.duhapp.dnotes.foundation.uicomponents.ConfirmDialog
import com.duhapp.dnotes.foundation.uicomponents.EmptyStateView
import com.duhapp.dnotes.foundation.uicomponents.LoadingScreen

@Composable
fun ManageCategoryScreenRoute(
    onNavigateBack: () -> Unit,
    viewModel: ManageCategoryViewModel = hiltViewModel()
) {
    var categoryToDelete by remember { mutableStateOf<CategoryUIModel?>(null) }
    var categoryBeingEdited by remember { mutableStateOf<CategoryUIModel?>(null) }
    var showType by remember { mutableStateOf(CategoryShowType.Add) }
    var showEditSheet by remember { mutableStateOf(false) }

    MviScreen(
        viewModel = viewModel,
        onEffect = { effect ->
            when (effect) {
                is ManageCategoryEffect.NavigateBack -> onNavigateBack()
                is ManageCategoryEffect.ShowAddEditCategorySheet -> {
                    categoryBeingEdited = effect.category
                    showType = if (effect.category == null) CategoryShowType.Add else CategoryShowType.Edit
                    showEditSheet = true
                }
                is ManageCategoryEffect.ShowDeleteSuccess -> {
                    // Show snackbar or similar in Story 5.3
                }
                is ManageCategoryEffect.ShowError -> {
                    // Show toast/dialog
                }
            }
        }
    ) { state ->
        ManageCategoryScreen(
            state = state,
            onIntent = viewModel::processIntent,
            onDeleteRequest = { categoryToDelete = it }
        )

        if (showEditSheet) {
            AddEditCategorySheet(
                category = categoryBeingEdited,
                showType = showType,
                onDismissRequest = { showEditSheet = false },
                onSaved = { 
                    showEditSheet = false
                    viewModel.processIntent(ManageCategoryIntent.LoadCategories)
                }
            )
        }

        categoryToDelete?.let { category ->
            ConfirmDialog(
                title = "Delete Category",
                body = "Are you sure you want to delete category '${category.name}'? All notes in this category will be moved to Default.",
                onConfirm = {
                    viewModel.processIntent(ManageCategoryIntent.OnDeleteCategory(category))
                    categoryToDelete = null
                },
                onDismiss = {
                    categoryToDelete = null
                }
            )
        }
    }
}

@Composable
fun ManageCategoryScreen(
    state: ManageCategoryState,
    onIntent: (ManageCategoryIntent) -> Unit,
    onDeleteRequest: (CategoryUIModel) -> Unit
) {
    BaseScreenScaffold(
        title = "Manage Categories",
        showBackButton = true,
        onBackClick = { onIntent(ManageCategoryIntent.NavigationBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onIntent(ManageCategoryIntent.OnAddCategoryClick) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Category")
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
            state.categories.isEmpty() -> {
                EmptyStateView(
                    title = "No Categories",
                    message = "Tap the + button to create a category.",
                    icon = Icons.Default.Add,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = state.categories,
                        key = { it.id }
                    ) { category ->
                        CategoryCard(
                            name = category.name,
                            emoji = category.emoji,
                            description = category.description,
                            colorOrdinal = category.color?.color?.ordinal ?: 0,
                            onClick = { onIntent(ManageCategoryIntent.OnCategoryClick(category)) },
                            onDeleteClick = { onDeleteRequest(category) }
                        )
                    }
                }
            }
        }
    }
}
