package com.duhapp.dnotes.foundation.uicomponents

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import /* androidx.compose.material.icons.filled.Notifications */ androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.duhapp.dnotes.R
import com.duhapp.dnotes.foundation.navigation.Route

/**
 * Data class representing a tab in the bottom navigation.
 */
private data class BottomNavDestination(
    val route: Route,
    val titleRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val bottomNavDestinations = listOf(
    BottomNavDestination(
        route = Route.Home,
        titleRes = R.string.title_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavDestination(
        route = Route.ManageCategory,
        titleRes = R.string.Manage_Category_Fragment,
        selectedIcon = Icons.Filled.List,
        unselectedIcon = Icons.Outlined.List
    ),
    BottomNavDestination(
        route = Route.Notifications,
        titleRes = R.string.title_notifications,
        selectedIcon = Icons.Filled.Notifications,
        unselectedIcon = Icons.Outlined.Notifications
    )
)

/**
 * Standard Bottom Navigation Bar for DNotes.
 *
 * @param currentRoute The currently active route to highlight the proper tab.
 * @param onNavigate Callback triggered when a tab is selected.
 * @param modifier Modifier for styling or layout.
 */
@Composable
fun DNotesBottomNavigation(
    currentRoute: Route?,
    onNavigate: (Route) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        bottomNavDestinations.forEach { dest ->
            val isSelected = currentRoute?.javaClass == dest.route.javaClass
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(dest.route) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) dest.selectedIcon else dest.unselectedIcon,
                        contentDescription = stringResource(id = dest.titleRes)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = dest.titleRes),
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                alwaysShowLabel = true
            )
        }
    }
}
