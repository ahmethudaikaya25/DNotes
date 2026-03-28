package com.duhapp.dnotes.foundation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.duhapp.dnotes.features.home.HomeScreenRoute
import com.duhapp.dnotes.features.all_notes.ui.AllNotesScreenRoute
import com.duhapp.dnotes.features.manage_category.ui.ManageCategoryScreenRoute
import com.duhapp.dnotes.features.note.ui.NoteEditorScreenRoute
import com.duhapp.dnotes.features.notifications.ui.NotificationsScreenRoute
import com.duhapp.dnotes.features.search.ui.SearchScreenRoute
import com.duhapp.dnotes.features.settings.ui.ExportScreenRoute
import com.duhapp.dnotes.features.settings.ui.ImportScreenRoute
import com.duhapp.dnotes.features.settings.ui.SettingsScreenRoute
import com.duhapp.dnotes.foundation.uicomponents.DNotesBottomNavigation

/**
 * Root navigation graph for the DNotes Compose UI.
 *
 * During the migration, each [composable] destination will be filled in
 * sprint-by-sprint. Placeholder [TODO] screens are used until the real
 * Compose screens are ready.
 *
 * Full target graph:
 *  Home ──► NoteEditor (create / edit)
 *       ──► AllNotes(categoryId)
 *  ManageCategory (bottom-nav tab)
 *  Notifications (bottom-nav tab, placeholder)
 *  Search
 *  Settings ──► ExportNotes
 *           ──► ImportNotes
 */
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentBottomRoute: Route? = when {
        currentDestination?.hasRoute<Route.Home>() == true -> Route.Home
        currentDestination?.hasRoute<Route.ManageCategory>() == true -> Route.ManageCategory
        currentDestination?.hasRoute<Route.Notifications>() == true -> Route.Notifications
        else -> null
    }

    Scaffold(
        bottomBar = {
            if (currentBottomRoute != null) {
                DNotesBottomNavigation(
                    currentRoute = currentBottomRoute,
                    onNavigate = { target ->
                        navController.navigate(target, navOptions {
                            popUpTo(Route.Home) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        })
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Home,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400))
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400))
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400))
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400))
            },
            modifier = Modifier.padding(innerPadding)
        ) {

        // ── Home ──────────────────────────────────────────────────────────────
        composable<Route.Home> {
            HomeScreenRoute(
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }

        // ── Note Editor ───────────────────────────────────────────────────────
        composable<Route.NoteEditor> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.NoteEditor>()
            NoteEditorScreenRoute(
                noteId = route.noteId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ── All Notes ─────────────────────────────────────────────────────────
        composable<Route.AllNotes> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.AllNotes>()
            AllNotesScreenRoute(
                categoryId = route.categoryId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToNote = { noteId ->
                    navController.navigate(Route.NoteEditor(noteId))
                }
            )
        }

        // ── Manage Category ───────────────────────────────────────────────────
        composable<Route.ManageCategory> {
            ManageCategoryScreenRoute(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ── Search ──────────────────────────────────────────────────────────
        composable<Route.Search> {
            SearchScreenRoute(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNote = { noteId ->
                    navController.navigate(Route.NoteEditor(noteId))
                }
            )
        }

        // ── Notifications ─────────────────────────────────────────────────────
        composable<Route.Notifications> {
            NotificationsScreenRoute()
        }

        // ── Settings ──────────────────────────────────────────────────────────
        composable<Route.Settings> {
            SettingsScreenRoute(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToExport = { navController.navigate(Route.ExportNotes) },
                onNavigateToImport = { navController.navigate(Route.ImportNotes) }
            )
        }

        // ── Export ────────────────────────────────────────────────────────────
        composable<Route.ExportNotes> {
            ExportScreenRoute(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Import ────────────────────────────────────────────────────────────
        composable<Route.ImportNotes> {
            ImportScreenRoute(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        }
    }
}
