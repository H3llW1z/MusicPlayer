package com.panassevich.musicplayer.domain.usecase

import com.panassevich.musicplayer.domain.repository.PlaybackRepository
import javax.inject.Inject

class SearchOnlineTracksUseCase @Inject constructor(private val repository: PlaybackRepository) {

    suspend operator fun invoke(query: String) = repository.searchOnlineTracks(query)
}