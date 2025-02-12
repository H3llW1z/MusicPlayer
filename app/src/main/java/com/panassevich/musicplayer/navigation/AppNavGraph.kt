package com.panassevich.musicplayer.navigation

import androidx.compose.runtime.Composable
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
        composable<Route.Player>{
            val player: Route.Player = it.toRoute()
            playerContent(player.trackIdToPlay)
        }
    }
}