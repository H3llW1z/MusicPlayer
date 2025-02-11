package com.panassevich.musicplayer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class NavigationState(
    val navHostController: NavHostController
) {

    fun navigateTo(route: String) {
        navHostController.navigate(route) {
            popUpTo(navHostController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToPlayer() {
        navHostController.navigate(Screen.Player.route)
    }
}

@Composable
fun rememberNavigationState(navHostController: NavHostController = rememberNavController()): NavigationState {
    return remember { NavigationState(navHostController) }
}