package com.panassevich.musicplayer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.panassevich.musicplayer.domain.entity.Track

class NavigationState(
    val navHostController: NavHostController
) {

    fun navigate(route: Route) {
        navHostController.navigate(route) {
            popUpTo(navHostController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToPlayer(track: Track) {
        navHostController.navigate(Route.Player(trackIdToPlay = track.id))
    }

    fun navigateToPlayer() {
        navHostController.navigate(Route.Player())
    }
}

@Composable
fun rememberNavigationState(navHostController: NavHostController = rememberNavController()): NavigationState {
    return remember { NavigationState(navHostController) }
}