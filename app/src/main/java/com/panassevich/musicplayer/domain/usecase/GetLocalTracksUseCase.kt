package com.panassevich.musicplayer.domain.usecase

import com.panassevich.musicplayer.domain.repository.PlaybackRepository
import javax.inject.Inject

class GetLocalTracksUseCase @Inject constructor(private val repository: PlaybackRepository) {

    suspend fun getAllTracks() = repository.getAllLocalTracks()

    suspend fun search(query: String) = repository.searchLocalTracks(query)
}