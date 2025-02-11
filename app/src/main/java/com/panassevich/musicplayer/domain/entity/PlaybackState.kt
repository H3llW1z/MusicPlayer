package com.panassevich.musicplayer.domain.entity


sealed class PlaybackState {
    data object NoTrack : PlaybackState()

    data class Current(
        val track: Track,
        val currentState: CurrentState,
        val secondsFromStart: Int,
        val progressPercent: Int,
        val hasPrevious: Boolean,
        val hasNext: Boolean
    ) : PlaybackState() {

        enum class CurrentState {
            PLAYING, PAUSED
        }
    }
}

