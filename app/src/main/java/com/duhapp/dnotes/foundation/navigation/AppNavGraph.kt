package com.duhapp.dnotes.foundation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.duhapp.dnotes.features.home.HomeScreenRoute

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
        composable<Route.NoteEditor> {
            // TODO(Sprint 3): NoteEditorScreenRoute(navController)
        }

        // ── All Notes ─────────────────────────────────────────────────────────
        composable<Route.AllNotes> {
            // TODO(Sprint 4): AllNotesScreenRoute(navController)
        }

        // ── Manage Category ───────────────────────────────────────────────────
        composable<Route.ManageCategory> {
            // TODO(Sprint 5): ManageCategoryScreenRoute(navController)
        }

        // ── Notifications ─────────────────────────────────────────────────────
        composable<Route.Notifications> {
            // TODO: NotificationsScreenRoute()
        }

        // ── Search ────────────────────────────────────────────────────────────
        composable<Route.Search> {
            // TODO(Sprint 7): SearchScreenRoute(navController)
        }

        // ── Settings ──────────────────────────────────────────────────────────
        composable<Route.Settings> {
            // TODO(Sprint 8): SettingsScreenRoute(navController)
        }

        // ── Export ────────────────────────────────────────────────────────────
        composable<Route.ExportNotes> {
            // TODO(Sprint 8): ExportScreenRoute(navController)
        }

        // ── Import ────────────────────────────────────────────────────────────
        composable<Route.ImportNotes> {
            // TODO(Sprint 8): ImportScreenRoute(navController)
        }
    }
}
