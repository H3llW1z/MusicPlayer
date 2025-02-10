package com.panassevich.musicplayer.domain.usecase

import com.panassevich.musicplayer.domain.repository.LocalTracksRepository
import javax.inject.Inject

class GetLocalTracksUseCase @Inject constructor(private val repository: LocalTracksRepository) {

    suspend fun getAllTracks() = repository.getAllTracks()

    suspend fun search(query: String) = repository.search(query)
}