package com.duhapp.dnotes.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object ManageCategory : Screen("manage_category")
    data object Notifications : Screen("notifications")
    data object Note : Screen("note?noteItem={noteItem}") {
        fun createRoute(noteItem: String? = null): String {
            return if (noteItem != null) "note?noteItem=$noteItem" else "note"
        }
    }
    data object AllNotes : Screen("all_notes/{categoryId}") {
        fun createRoute(categoryId: Int): String = "all_notes/$categoryId"
    }
}
