package com.panassevich.musicplayer.navigation

import kotlinx.serialization.Serializable

sealed class Route {

    @Serializable
    data object OnlineTracks : Route()

    @Serializable
    data object LocalTracks : Route()

    @Serializable
    data class Player(val trackIdToPlay: Long = NO_ID) : Route() {
        companion object {
            const val NO_ID = 0L
        }
    }
}