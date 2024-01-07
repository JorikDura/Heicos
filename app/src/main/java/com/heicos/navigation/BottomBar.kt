package com.heicos.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptionsBuilder
import com.heicos.presentation.NavGraphs
import com.heicos.presentation.appCurrentDestinationAsState
import com.heicos.presentation.destinations.Destination
import com.heicos.presentation.destinations.FullCosplayScreenDestination
import com.heicos.presentation.startAppDestination
import com.ramcosta.composedestinations.navigation.navigate

@Composable
fun BottomBar(
    navController: NavController
) {
    val currentDestination: Destination = navController.appCurrentDestinationAsState().value
        ?: NavGraphs.root.startAppDestination

    if (currentDestination.route == FullCosplayScreenDestination.route) {
        return
    }

    NavigationBar {
        BottomBarDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = currentDestination == destination.direction,
                onClick = {
                    navController.navigate(destination.direction, fun NavOptionsBuilder.() {
                        launchSingleTop = true
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                    })
                },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = stringResource(id = destination.label)
                    )
                },
                label = { Text(stringResource(destination.label)) },
                alwaysShowLabel = true
            )
        }
    }
}