package com.duhapp.dnotes.foundation.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes for the Compose navigation graph.
 *
 * Each object / data class here is a route. Compose Navigation 2.8+
 * supports @Serializable routes — no more string-based routes or SafeArgs.
 *
 * Usage in NavHost:
 * ```
 * composable<Route.Home> { HomeScreenRoute() }
 * ```
 * Usage in navigation:
 * ```
 * navController.navigate(Route.NoteEditor(noteId = 42))
 * ```
 */
sealed interface Route {

    /** Bottom-nav tab: main note list with sort + group options */
    @Serializable
    object Home : Route

    /** Bottom-nav tab: manage categories (add / edit / delete) */
    @Serializable
    object ManageCategory : Route

    /** Bottom-nav tab: notifications (placeholder) */
    @Serializable
    object Notifications : Route

    /**
     * Note editor screen — doubles as create (noteId = null) and edit (noteId = Int).
     * @param noteId null when creating a new note, non-null when editing an existing one.
     */
    @Serializable
    data class NoteEditor(val noteId: Int? = null) : Route

    /**
     * All-notes screen filtered to a single category.
     * @param categoryId the ID of the category whose notes are displayed.
     */
    @Serializable
    data class AllNotes(val categoryId: Int) : Route

    /** Global search screen */
    @Serializable
    object Search : Route

    /** App settings screen */
    @Serializable
    object Settings : Route

    /** Export notes screen */
    @Serializable
    object ExportNotes : Route

    /** Import notes screen */
    @Serializable
    object ImportNotes : Route
}
