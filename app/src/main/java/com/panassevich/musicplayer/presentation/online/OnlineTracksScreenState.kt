package com.panassevich.musicplayer.presentation.online

import com.panassevich.musicplayer.domain.entity.Track

sealed class OnlineTracksScreenState {
    data object Initial : OnlineTracksScreenState()
    data object Loading : OnlineTracksScreenState()
    data class Content(val tracks: List<Track>, val type: TracksType) : OnlineTracksScreenState()
}

enum class TracksType {
    CHART, SEARCH
}