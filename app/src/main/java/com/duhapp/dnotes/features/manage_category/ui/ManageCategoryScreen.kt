package com.duhapp.dnotes.features.manage_category.ui

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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import com.duhapp.dnotes.features.add_or_update_category.ui.AddEditCategorySheet
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryShowType
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
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

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
                    coroutineScope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Category '${effect.categoryName}' deleted",
                            actionLabel = "Undo",
                            withDismissAction = true,
                            duration = SnackbarDuration.Short
                        )
                        if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                            viewModel.processIntent(ManageCategoryIntent.OnUndoDelete)
                        }
                    }
                }
                is ManageCategoryEffect.ShowError -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                }
                is ManageCategoryEffect.ShowErrorRes -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(context.getString(effect.messageRes))
                    }
                }
            }
        }
    ) { state ->
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { _ ->
            ManageCategoryScreen(
                state = state,
                onIntent = viewModel::processIntent,
                onDeleteRequest = { categoryToDelete = it },
                isInteractionEnabled = !showEditSheet && categoryToDelete == null
            )
        }

        if (showEditSheet) {
            AddEditCategorySheet(
                category = categoryBeingEdited,
                showType = showType,
                onDismissRequest = { showEditSheet = false },
                onSaved = { 
                    showEditSheet = false
                    viewModel.processIntent(ManageCategoryIntent.LoadCategories)
                },
                onDeleted = { categoryName ->
                    showEditSheet = false
                    viewModel.processIntent(ManageCategoryIntent.OnCategoryDeleted(categoryName))
                }
            )
        }

        categoryToDelete?.let { category ->
            val otherCategories = state.categories.filter { it.id != category.id }
            if (category.isDefault) {
                DeleteDefaultCategoryDialog(
                    category = category,
                    candidates = otherCategories,
                    onConfirm = { newDefaultCategoryId ->
                        viewModel.processIntent(
                            ManageCategoryIntent.OnDeleteCategory(category, newDefaultCategoryId)
                        )
                        categoryToDelete = null
                    },
                    onDismiss = {
                        categoryToDelete = null
                    }
                )
            } else {
                val defaultCategoryName = otherCategories
                    .firstOrNull { it.isDefault }?.name ?: "the default category"
                ConfirmDialog(
                    title = "Delete Category",
                    message = "Are you sure you want to delete category '${category.name}'? All notes in this category will be moved to '$defaultCategoryName'.",
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
}

@Composable
fun ManageCategoryScreen(
    state: ManageCategoryState,
    onIntent: (ManageCategoryIntent) -> Unit,
    onDeleteRequest: (CategoryUIModel) -> Unit,
    isInteractionEnabled: Boolean = true
) {
    BaseScreenScaffold(
        title = "Manage Categories",
        showBackButton = false,
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
                            // the last remaining category has nothing to hand its notes
                            // and the default role over to
                            canDelete = state.categories.size > 1,
                            onClick = { if (isInteractionEnabled) onIntent(ManageCategoryIntent.OnCategoryClick(category)) },
                            onDeleteClick = { if (isInteractionEnabled) onDeleteRequest(category) },
                            enabled = isInteractionEnabled
                        )
                    }
                }
            }
        }
    }
}
