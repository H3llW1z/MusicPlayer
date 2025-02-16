package com.panassevich.musicplayer.domain.entity


sealed class PlaybackState {
    data object NoTrack : PlaybackState()

    data class CurrentTrack(
        val track: Track,
        val currentState: CurrentState,
        val hasPrevious: Boolean,
        val hasNext: Boolean
    ) : PlaybackState()

    enum class CurrentState {
        PLAYING, PAUSED
    }
}

