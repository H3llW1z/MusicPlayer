package com.panassevich.musicplayer.domain.usecase

import com.panassevich.musicplayer.domain.repository.PlaybackRepository
import javax.inject.Inject

class LoadNextDataUseCase @Inject constructor(private val repository: PlaybackRepository) {

    suspend operator fun invoke() = repository.loadNextData()
}