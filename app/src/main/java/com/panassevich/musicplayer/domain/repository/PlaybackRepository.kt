package com.panassevich.musicplayer.domain.repository

import com.panassevich.musicplayer.domain.entity.PlaybackState
import com.panassevich.musicplayer.domain.entity.Track

interface PlaybackRepository {

    fun getCurrentState(): PlaybackState

    fun startPlay(trackId: Long)

    fun resume()

    fun pause()

    fun seekTo(seconds: Int)

    fun playPrevious()

    fun playNext()

    suspend fun getAllLocalTracks(): List<Track>

    suspend fun searchLocalTracks(query: String): List<Track>

    suspend fun searchOnlineTracks(query: String): List<Track>

    suspend fun getOnlineChart(): List<Track>

}