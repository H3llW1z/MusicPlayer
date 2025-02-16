package com.panassevich.musicplayer.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

@Composable
fun AppNavGraph(
    navHostController: NavHostController,
    onlineTracksContent: @Composable () -> Unit,
    localTracksContent: @Composable () -> Unit,
    playerContent: @Composable (Long) -> Unit
) {
    NavHost(
        navController = navHostController,
        startDestination = Route.OnlineTracks,
    ) {
        composable<Route.OnlineTracks> {
            onlineTracksContent()
        }
        composable<Route.LocalTracks> {
            localTracksContent()
        }
        composable<Route.Player>(
            enterTransition = {
                slideIn(
                    animationSpec = tween(300),
                    initialOffset = { size -> IntOffset(0, size.height) }
                )
            },
            exitTransition = {
                slideOut(
                    animationSpec = tween(500),
                    targetOffset = { size -> IntOffset(0, size.height) }
                )
            }
        ){
            val player: Route.Player = it.toRoute()
            playerContent(player.trackIdToPlay)
        }
    }
}