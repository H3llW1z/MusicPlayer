package com.panassevich.musicplayer.presentation.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.panassevich.musicplayer.R
import com.panassevich.musicplayer.navigation.AppNavGraph
import com.panassevich.musicplayer.navigation.Screen
import com.panassevich.musicplayer.navigation.rememberNavigationState
import com.panassevich.musicplayer.presentation.local.LocalTracksScreen
import com.panassevich.musicplayer.presentation.online.OnlineTracksScreen
import com.panassevich.musicplayer.presentation.player.PlayerScreen

@Composable
fun MainScreen() {

    val navigationState = rememberNavigationState()

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navigationState.navHostController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            NavigationBar {
                val items = listOf(
                    NavigationItem.OnlineTracks,
                    NavigationItem.LocalTracks
                )
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.screen.route,
                        onClick = { navigationState.navigateTo(item.screen.route) },
                        icon = {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                painter = painterResource(item.iconResId), contentDescription = null
                            )
                        },
                        label = {
                            Text(text = stringResource(id = item.titleResId))
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            val navBackStackEntry by navigationState.navHostController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val fabVisible = currentRoute != Screen.Player.route
            AnimatedVisibility(
                visible = fabVisible,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
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
                OnlineTracksScreen(paddingValues)
            },
            localTracksContent = {
                LocalTracksScreen(paddingValues)
            },
            playerContent = {
                PlayerScreen(paddingValues)
            }
        )

    }
}