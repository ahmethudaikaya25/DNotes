package com.duhapp.dnotes.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object ManageCategory : Screen("manage_category")
    object Notifications : Screen("notifications")
    object Note : Screen("note?noteId={noteId}") {
        fun createRoute(noteId: Int? = null) = if (noteId != null) {
            "note?noteId=$noteId"
        } else {
            "note"
        }
    }
    object AllNotes : Screen("all_notes/{categoryId}") {
        fun createRoute(categoryId: Int) = "all_notes/$categoryId"
    }
}

sealed class BottomNavItem(
    val screen: Screen,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Home : BottomNavItem(
        Screen.Home,
        "Home",
        androidx.compose.material.icons.Icons.Default.Home
    )

    object ManageCategory : BottomNavItem(
        Screen.ManageCategory,
        "Categories",
        androidx.compose.material.icons.Icons.Default.List
    )

    object Notifications : BottomNavItem(
        Screen.Notifications,
        "Notifications",
        androidx.compose.material.icons.Icons.Default.Notifications
    )
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.ManageCategory,
    BottomNavItem.Notifications
)
