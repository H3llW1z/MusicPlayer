package com.panassevich.musicplayer.domain.entity

data class OnlineTracksResult(
    val tracksType: OnlineTracksType,
    val tracks: List<Track>,
    val hasMoreTracks: Boolean
)
