package com.panassevich.musicplayer.domain.usecase

import com.panassevich.musicplayer.domain.repository.OnlineTracksRepository
import javax.inject.Inject

class GetChartTracksUseCase @Inject constructor(private val repository: OnlineTracksRepository) {

    suspend operator fun invoke() = repository.getChart()
}