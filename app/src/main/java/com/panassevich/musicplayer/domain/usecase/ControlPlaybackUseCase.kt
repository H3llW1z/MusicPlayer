package com.panassevich.musicplayer.domain.usecase


import com.panassevich.musicplayer.domain.repository.PlaybackRepository
import javax.inject.Inject

class ControlPlaybackUseCase @Inject constructor(private val repository: PlaybackRepository) {

    fun play() = repository.play()

    fun pause() = repository.pause()

    fun playPrevious() = repository.playPrevious()

    fun playNext() = repository.playNext()

    fun rewind(seconds: Int) = repository.rewind(seconds)

}