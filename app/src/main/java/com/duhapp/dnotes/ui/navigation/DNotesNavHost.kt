package com.duhapp.dnotes.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.duhapp.dnotes.ui.screens.AllNotesScreen
import com.duhapp.dnotes.ui.screens.HomeScreen
import com.duhapp.dnotes.ui.screens.ManageCategoryScreen
import com.duhapp.dnotes.ui.screens.NoteScreen
import com.duhapp.dnotes.ui.screens.NotificationsScreen

@Composable
fun DNotesNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNoteClick = { noteId ->
                    navController.navigate(Screen.Note.createRoute(noteId))
                },
                onViewAllClick = { categoryId ->
                    navController.navigate(Screen.AllNotes.createRoute(categoryId))
                }
            )
        }

        composable(Screen.ManageCategory.route) {
            ManageCategoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen()
        }

        composable(
            route = Screen.Note.route,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1
            NoteScreen(
                noteId = if (noteId == -1) null else noteId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.AllNotes.route,
            arguments = listOf(
                navArgument("categoryId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: 0
            AllNotesScreen(
                categoryId = categoryId,
                onNoteClick = { noteId ->
                    navController.navigate(Screen.Note.createRoute(noteId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
