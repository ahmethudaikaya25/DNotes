package com.duhapp.dnotes.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.duhapp.dnotes.features.manage_category.domain.GetCategories
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryBottomSheetScreen
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryBottomSheetViewModel
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryShowType
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.all_notes.ui.AllNotesScreen
import com.duhapp.dnotes.features.all_notes.ui.AllNotesScreenIntent
import com.duhapp.dnotes.features.all_notes.ui.AllNotesViewModel
import com.duhapp.dnotes.features.home.ui.HomeScreen
import com.duhapp.dnotes.features.home.ui.HomeScreenEffect
import com.duhapp.dnotes.features.home.ui.HomeScreenIntent
import com.duhapp.dnotes.features.home.ui.HomeViewModel
import com.duhapp.dnotes.features.home.home_screen_category.ui.BaseNoteUIModel
import com.duhapp.dnotes.features.manage_category.ui.ManageCategoryScreen
import com.duhapp.dnotes.features.manage_category.ui.ManageCategoryScreenEffect
import com.duhapp.dnotes.features.manage_category.ui.ManageCategoryViewModel
import com.duhapp.dnotes.features.note.ui.NoteScreen
import com.duhapp.dnotes.features.note.ui.NoteScreenEffect
import com.duhapp.dnotes.features.note.ui.NoteScreenIntent
import com.duhapp.dnotes.features.note.ui.NoteViewModel
import com.duhapp.dnotes.features.note.ui.getDarkColorFromOrdinal
import com.duhapp.dnotes.ui.navigation.Screen
import com.duhapp.dnotes.ui.theme.BottomNavBar
import com.duhapp.dnotes.ui.theme.BottomNavBarUnselected
import com.duhapp.dnotes.ui.theme.PrimaryColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BottomNavItem(
    val route: String,
    val icon: @Composable () -> Unit,
    val label: String
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCategories: GetCategories
) : ViewModel() {
    var categories by mutableStateOf<List<CategoryUIModel>>(emptyList())
        private set

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            categories = getCategories.invoke()
        }
    }
}

@Composable
fun MainScreen(
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val categoryBottomSheetViewModel: CategoryBottomSheetViewModel = hiltViewModel()
    val manageCategoryViewModel: ManageCategoryViewModel = hiltViewModel()

    var showCategoryBottomSheet by rememberSaveable { mutableStateOf(false) }
    var bottomSheetCategory by rememberSaveable { mutableStateOf(CategoryUIModel()) }
    var bottomSheetShowType by rememberSaveable { mutableStateOf(CategoryShowType.Add) }
    var showMoveDialog by remember { mutableStateOf(false) }
    var noteToMove by remember { mutableStateOf<BaseNoteUIModel?>(null) }
    var selectedMoveCategory by remember { mutableStateOf<CategoryUIModel?>(null) }
    var activeAllNotesViewModel by remember { mutableStateOf<AllNotesViewModel?>(null) }

    val bottomNavItems = listOf(
        BottomNavItem(
            route = Screen.Home.route,
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = "Home"
        ),
        BottomNavItem(
            route = Screen.ManageCategory.route,
            icon = { Icon(Icons.Default.Category, contentDescription = "Categories") },
            label = "Categories"
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.ManageCategory.route
    )

    val showFab = showBottomBar

    val onFabClick: () -> Unit = {
        when (currentRoute) {
            Screen.Home.route -> {
                navController.navigate(Screen.Note.createRoute(null))
            }
            Screen.ManageCategory.route -> {
                bottomSheetCategory = CategoryUIModel()
                bottomSheetShowType = CategoryShowType.Add
                showCategoryBottomSheet = true
            }
        }
    }

    Scaffold(
        containerColor = BottomNavBar,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = BottomNavBar,
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            icon = item.icon,
                            label = { Text(item.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryColor,
                                selectedTextColor = PrimaryColor,
                                unselectedIconColor = BottomNavBarUnselected,
                                unselectedTextColor = BottomNavBarUnselected
                            )
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(
                    onClick = onFabClick,
                    containerColor = PrimaryColor,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ) {
                composable(Screen.Home.route) {
                    val homeViewModel: HomeViewModel = hiltViewModel()
                    val state by homeViewModel.state.collectAsState()

                    LaunchedEffect(Unit) {
                        homeViewModel.effect.collectLatest { effect ->
                            when (effect) {
                                is HomeScreenEffect.NavigateToNote -> {
                                    navController.navigate(Screen.Note.createRoute(effect.noteId.toString()))
                                }
                                is HomeScreenEffect.NavigateToAllNotes -> {
                                    navController.navigate(Screen.AllNotes.createRoute(effect.categoryId))
                                }
                                is HomeScreenEffect.ShowError -> {}
                            }
                        }
                    }

                    HomeScreen(
                        state = state,
                        onIntent = { intent -> homeViewModel.processIntent(intent) }
                    )
                }
                composable(Screen.ManageCategory.route) {
                    val state by manageCategoryViewModel.state.collectAsState()

                    LaunchedEffect(Unit) {
                        manageCategoryViewModel.effect.collectLatest { effect ->
                            when (effect) {
                                is ManageCategoryScreenEffect.NavigateToCategoryBottomSheet -> {
                                    bottomSheetCategory = effect.category
                                    bottomSheetShowType = effect.showType
                                    showCategoryBottomSheet = true
                                }
                                is ManageCategoryScreenEffect.ShowDeleteConfirmation -> {
                                    mainViewModel.loadCategories()
                                }
                                is ManageCategoryScreenEffect.ShowError -> {}
                            }
                        }
                    }

                    ManageCategoryScreen(
                        state = state,
                        onIntent = { intent -> manageCategoryViewModel.processIntent(intent) }
                    )
                }
                composable(
                    route = Screen.Note.route,
                    arguments = listOf(
                        navArgument("noteItem") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val noteViewModel: NoteViewModel = hiltViewModel()
                    val state by noteViewModel.state.collectAsState()
                    val noteItemArg = backStackEntry.arguments?.getString("noteItem")

                    LaunchedEffect(noteItemArg) {
                        val noteId = noteItemArg?.toIntOrNull()
                        noteViewModel.processIntent(NoteScreenIntent.LoadNote(noteId))
                    }

                    LaunchedEffect(Unit) {
                        noteViewModel.effect.collectLatest { effect ->
                            when (effect) {
                                is NoteScreenEffect.NavigateBack -> {
                                    navController.popBackStack()
                                }
                                is NoteScreenEffect.NoteSaved -> {}
                                is NoteScreenEffect.ShowError -> {}
                            }
                        }
                    }

                    NoteScreen(
                        state = state,
                        onIntent = { intent -> noteViewModel.processIntent(intent) },
                        categories = mainViewModel.categories
                    )
                }
                composable(
                    route = Screen.AllNotes.route,
                    arguments = listOf(
                        navArgument("categoryId") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: 0
                    val allNotesViewModel: AllNotesViewModel = hiltViewModel()
                    val state by allNotesViewModel.state.collectAsState()

                    DisposableEffect(allNotesViewModel) {
                        activeAllNotesViewModel = allNotesViewModel
                        onDispose {
                            if (activeAllNotesViewModel == allNotesViewModel) {
                                activeAllNotesViewModel = null
                            }
                        }
                    }

                    LaunchedEffect(categoryId) {
                        allNotesViewModel.processIntent(AllNotesScreenIntent.LoadNotes(categoryId))
                    }

                    LaunchedEffect(Unit) {
                        allNotesViewModel.effect.collectLatest { effect ->
                            when (effect) {
                                is com.duhapp.dnotes.features.all_notes.ui.AllNotesScreenEffect.NavigateToNote -> {
                                    navController.navigate(Screen.Note.createRoute(effect.note.id.toString()))
                                }
                                is com.duhapp.dnotes.features.all_notes.ui.AllNotesScreenEffect.NavigateBack -> {
                                    navController.popBackStack()
                                }
                                is com.duhapp.dnotes.features.all_notes.ui.AllNotesScreenEffect.ShowMoveDialog -> {
                                    noteToMove = effect.note
                                    selectedMoveCategory = mainViewModel.categories.firstOrNull { category ->
                                        category.id == effect.note.category.id
                                    } ?: mainViewModel.categories.firstOrNull()
                                    showMoveDialog = true
                                    if (mainViewModel.categories.isEmpty()) {
                                        mainViewModel.loadCategories()
                                    }
                                }
                                is com.duhapp.dnotes.features.all_notes.ui.AllNotesScreenEffect.NotesDeleted -> {}
                                is com.duhapp.dnotes.features.all_notes.ui.AllNotesScreenEffect.ShowError -> {}
                            }
                        }
                    }

                    AllNotesScreen(
                        state = state,
                        onIntent = { intent -> allNotesViewModel.processIntent(intent) }
                    )
                }
            }
        }
    }

    if (showCategoryBottomSheet) {
        CategoryBottomSheetScreen(
            categoryUIModel = bottomSheetCategory,
            categoryShowType = bottomSheetShowType,
            viewModel = categoryBottomSheetViewModel,
            onSave = { savedCategory ->
                showCategoryBottomSheet = false
                mainViewModel.loadCategories()
                manageCategoryViewModel.onCategoryUpserted()
            },
            onDismiss = {
                showCategoryBottomSheet = false
            }
        )
    }

    if (showMoveDialog) {
        val categories = mainViewModel.categories
        AlertDialog(
            onDismissRequest = {
                showMoveDialog = false
                noteToMove = null
                selectedMoveCategory = null
            },
            title = {
                Text("Move note to category")
            },
            text = {
                if (categories.isEmpty()) {
                    Text("No categories available.")
                } else {
                    Column {
                        categories.forEach { category ->
                            val isSelected = category.id == selectedMoveCategory?.id
                            TextButton(
                                onClick = { selectedMoveCategory = category }
                            ) {
                                Text(
                                    text = "${category.emoji} ${category.name}",
                                    color = if (isSelected) {
                                        getDarkColorFromOrdinal(category.color.color.ordinal)
                                    } else {
                                        Color.Black
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val note = noteToMove
                        val category = selectedMoveCategory
                        if (note != null && category != null) {
                            activeAllNotesViewModel?.moveNotesToCategory(listOf(note), category)
                        }
                        showMoveDialog = false
                        noteToMove = null
                        selectedMoveCategory = null
                    },
                    enabled = noteToMove != null && selectedMoveCategory != null
                ) {
                    Text("Move")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showMoveDialog = false
                        noteToMove = null
                        selectedMoveCategory = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
