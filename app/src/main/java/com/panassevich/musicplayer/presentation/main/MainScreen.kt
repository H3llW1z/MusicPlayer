package com.panassevich.musicplayer.presentation.main

import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.panassevich.musicplayer.R
import com.panassevich.musicplayer.navigation.AppNavGraph
import com.panassevich.musicplayer.navigation.Route
import com.panassevich.musicplayer.navigation.rememberNavigationState
import com.panassevich.musicplayer.presentation.local.LocalTracksScreen
import com.panassevich.musicplayer.presentation.online.OnlineTracksScreen
import com.panassevich.musicplayer.presentation.player.PlayerScreen

@Composable
fun MainScreen() {

    val navigationState = rememberNavigationState()

    val snackbarHostState = SnackbarHostState()

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            val navBackStackEntry by navigationState.navHostController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            val isPlayer = currentDestination?.hasRoute(Route.Player::class) == true
            if (!isPlayer) {
                NavigationBar {
                    val items = listOf(
                        NavigationItem.OnlineTracks,
                        NavigationItem.LocalTracks
                    )

                    items.forEach { item ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true,
                            onClick = {
                                navigationState.navigate(item.route)
                            },
                            icon = {
                                Icon(
                                    modifier = Modifier.size(30.dp),
                                    painter = painterResource(item.iconResId),
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(text = stringResource(id = item.titleResId))
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            val navBackStackEntry by navigationState.navHostController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val fabVisible =
                currentDestination?.hierarchy?.none { it.hasRoute(Route.Player::class) } == true

            if (fabVisible) {
                FloatingActionButton(
                    onClick = {
                        navigationState.navigateToPlayer()
                    }
                ) {
                    Icon(painter = painterResource(R.drawable.ic_player), contentDescription = null)
                }
            }
        }
    ) { paddingValues ->
        AppNavGraph(
            navHostController = navigationState.navHostController,
            onlineTracksContent = {
                OnlineTracksScreen(
                    paddingValues = paddingValues,
                    onTrackClick = { trackIdToPlay ->
                        navigationState.navigateToPlayer(trackIdToPlay)
                    },
                    snackbarHostState = snackbarHostState
                )
            },
            localTracksContent = {
                LocalTracksScreen(paddingValues)
            },
            playerContent = { trackIdToPlay ->
                PlayerScreen(paddingValues, trackIdToPlay)
            }
        )

    }
}