package com.panassevich.musicplayer.domain.entity

import java.util.Calendar

data class PlaybackState(
    val track: Track,
    val currentState: CurrentState,
    val timeFromStart: Calendar,
    val progressPercent: Int,
    val hasPrevious: Boolean,
    val hasNext: Boolean
) {

    enum class CurrentState {
        PLAYING, PAUSED
    }
}
