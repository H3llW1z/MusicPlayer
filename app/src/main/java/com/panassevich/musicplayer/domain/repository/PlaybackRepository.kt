package com.panassevich.musicplayer.domain.repository

import com.panassevich.musicplayer.domain.entity.PlaybackState
import kotlinx.coroutines.flow.Flow

interface PlaybackRepository {

    val currentState: Flow<PlaybackState>

    fun play()

    fun pause()

    fun rewind(seconds: Int)

    fun playPrevious()

    fun playNext()
}