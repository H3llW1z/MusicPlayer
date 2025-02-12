package com.panassevich.musicplayer.presentation.main

import androidx.annotation.DrawableRes
import com.panassevich.musicplayer.R
import com.panassevich.musicplayer.navigation.Route

sealed class NavigationItem(
    val route: Route,
    val titleResId: Int,
    @DrawableRes val iconResId: Int,
) {

    data object OnlineTracks : NavigationItem(
        route = Route.OnlineTracks,
        titleResId = R.string.bottom_bar_title_online_tracks,
        iconResId = R.drawable.ic_music_cloud
    )

    data object LocalTracks :
        NavigationItem(
            route = Route.LocalTracks,
            titleResId = R.string.bottom_bar_title_local_tracks,
            iconResId = R.drawable.ic_music_local
        )
}