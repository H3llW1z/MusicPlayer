package com.panassevich.musicplayer.navigation

sealed class Screen(
    val route: String
) {

    data object OnlineTracks : Screen(ROUTE_ONLINE_TRACKS)
    data object LocalTracks : Screen(ROUTE_LOCAL_TRACKS)
    data object Player : Screen(ROUTE_PLAYER)

    private companion object {

        const val ROUTE_ONLINE_TRACKS = "online_tracks"
        const val ROUTE_LOCAL_TRACKS = "local_tracks"
        const val ROUTE_PLAYER = "player"
    }

}