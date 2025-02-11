package com.panassevich.musicplayer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavGraph(
    navHostController: NavHostController,
    onlineTracksContent: @Composable () -> Unit,
    localTracksContent: @Composable () -> Unit,
    playerContent: @Composable () -> Unit
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.OnlineTracks.route,
    ) {
        composable(route = Screen.OnlineTracks.route) {
            onlineTracksContent()
        }
        composable(route = Screen.LocalTracks.route) {
            localTracksContent()
        }
        composable(route = Screen.Player.route) {
            playerContent()
        }
    }
}