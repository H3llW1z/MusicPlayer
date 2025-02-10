package com.panassevich.musicplayer.domain.usecase

import com.panassevich.musicplayer.domain.repository.OnlineTracksRepository
import javax.inject.Inject

class SearchOnlineTracksUseCase @Inject constructor(private val repository: OnlineTracksRepository) {

    suspend operator fun invoke(query: String) = repository.search(query)
}