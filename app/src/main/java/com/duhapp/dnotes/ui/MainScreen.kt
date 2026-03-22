package com.duhapp.dnotes.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryBottomSheetScreen
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryBottomSheetViewModel
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryShowType
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.all_notes.ui.AllNotesScreen
import com.duhapp.dnotes.features.home.home_screen_category.ui.BaseNoteUIModel
import com.duhapp.dnotes.features.home.ui.HomeScreen
import com.duhapp.dnotes.features.manage_category.ui.ManageCategoryScreen
import com.duhapp.dnotes.features.manage_category.ui.ManageCategoryViewModel
import com.duhapp.dnotes.features.note.ui.NoteScreen
import com.duhapp.dnotes.ui.notifications.NotificationsScreen
import com.duhapp.dnotes.ui.notifications.NotificationsViewModel
import com.duhapp.dnotes.ui.navigation.Screen
import com.duhapp.dnotes.ui.theme.BottomNavBar
import com.duhapp.dnotes.ui.theme.BottomNavBarUnselected
import com.duhapp.dnotes.ui.theme.PrimaryColor

data class BottomNavItem(
    val route: String,
    val icon: @Composable () -> Unit,
    val label: String
)

@Composable
fun MainScreen(
    onNavigateToNote: () -> Unit,
    onNavigateToAllNotes: (Int) -> Unit,
    onNavigateToCategoryBottomSheet: () -> Unit
) {
    val navController = rememberNavController()
    
    val categoryBottomSheetViewModel: CategoryBottomSheetViewModel = hiltViewModel()
    
    var showCategoryBottomSheet by remember { mutableStateOf(false) }
    var bottomSheetCategory by remember { mutableStateOf(CategoryUIModel()) }
    var bottomSheetShowType by remember { mutableStateOf(CategoryShowType.Add) }
    var refreshCategoriesTrigger by remember { mutableIntStateOf(0) }
    
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
    
    val showBottomBar = currentDestination?.route in listOf(
        Screen.Home.route,
        Screen.ManageCategory.route
    )

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
            if (showBottomBar) {
                FloatingActionButton(
                    onClick = onNavigateToNote,
                    containerColor = PrimaryColor,
                    contentColor = androidx.compose.ui.graphics.Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Note")
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
                    HomeScreen(
                        onNavigateToNote = { noteItem ->
                            navController.navigate(Screen.Note.route)
                        },
                        onNavigateToAllNotes = { categoryId ->
                            navController.navigate(Screen.AllNotes.createRoute(categoryId))
                        }
                    )
                }
                composable(Screen.ManageCategory.route) {
                    val manageCategoryViewModel: ManageCategoryViewModel = hiltViewModel()
                    ManageCategoryScreen(
                        viewModel = manageCategoryViewModel,
                        onNavigateToCategoryBottomSheet = { category, showType ->
                            bottomSheetCategory = category
                            bottomSheetShowType = showType
                            showCategoryBottomSheet = true
                        },
                        onCategorySaved = {
                            manageCategoryViewModel.onCategoryUpserted()
                            refreshCategoriesTrigger++
                        }
                    )
                }
                composable(Screen.Notifications.route) {
                    NotificationsScreen(viewModel = NotificationsViewModel())
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
                ) {
                    NoteScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onCategoryClick = { }
                    )
                }
                composable(
                    route = Screen.AllNotes.route,
                    arguments = listOf(
                        navArgument("categoryId") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: 0
                    AllNotesScreen(
                        categoryId = categoryId,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToNote = { note ->
                            // TODO: Navigate to note
                        }
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
            },
            onDismiss = {
                showCategoryBottomSheet = false
            }
        )
    }
}
