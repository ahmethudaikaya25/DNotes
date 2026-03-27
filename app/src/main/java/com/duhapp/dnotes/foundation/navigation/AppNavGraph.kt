package com.duhapp.dnotes.foundation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.duhapp.dnotes.features.home.HomeScreenRoute
import com.duhapp.dnotes.features.all_notes.ui.AllNotesScreenRoute
import com.duhapp.dnotes.features.manage_category.ui.ManageCategoryScreenRoute
import com.duhapp.dnotes.features.note.ui.NoteEditorScreenRoute
import com.duhapp.dnotes.features.search.ui.SearchScreenRoute
import com.duhapp.dnotes.features.settings.ui.ExportScreenRoute
import com.duhapp.dnotes.features.settings.ui.ImportScreenRoute
import com.duhapp.dnotes.features.settings.ui.SettingsScreenRoute

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
    NavHost(
        navController = navController,
        startDestination = Route.Home,
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
            // TODO: NotificationsScreenRoute()
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
