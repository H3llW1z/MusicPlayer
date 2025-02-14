package com.panassevich.musicplayer.domain.repository

import com.panassevich.musicplayer.domain.entity.OnlineTracksResult
import com.panassevich.musicplayer.domain.entity.PlaybackState
import com.panassevich.musicplayer.domain.entity.Track
import kotlinx.coroutines.flow.StateFlow

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

    fun getOnlineTracks(): StateFlow<OnlineTracksResult>

    suspend fun loadNextData()

    suspend fun searchOnlineTracks(query: String)

    suspend fun loadOnlineChart()

}