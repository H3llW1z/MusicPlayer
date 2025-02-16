package com.panassevich.musicplayer.domain.usecase

import com.panassevich.musicplayer.domain.repository.PlaybackRepository
import javax.inject.Inject

class GetLocalTracksUseCase @Inject constructor(private val repository: PlaybackRepository) {

    operator fun invoke() = repository.getLocalTracks()
}