package com.panassevich.musicplayer.domain.usecase

import com.panassevich.musicplayer.domain.repository.PlaybackRepository
import javax.inject.Inject

class GetPlaybackStateUseCase @Inject constructor(private val repository: PlaybackRepository) {

    fun getCurrentState() = repository.getCurrentState()
    fun getCurrentPositionInTrack() = repository.getCurrentPositionInTrack()
}