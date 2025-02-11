package com.panassevich.musicplayer.presentation.main

import androidx.annotation.DrawableRes
import com.panassevich.musicplayer.R
import com.panassevich.musicplayer.navigation.Screen

sealed class NavigationItem(
    val screen: Screen,
    val titleResId: Int,
    @DrawableRes val iconResId: Int,
) {

    data object OnlineTracks : NavigationItem(
        screen = Screen.OnlineTracks,
        titleResId = R.string.bottom_bar_title_online_tracks,
        iconResId = R.drawable.ic_music_cloud
    )

    data object LocalTracks :
        NavigationItem(
            screen = Screen.LocalTracks,
            titleResId = R.string.bottom_bar_title_local_tracks,
            iconResId = R.drawable.ic_music_local
        )
}