package com.panassevich.musicplayer.presentation.player

import androidx.lifecycle.ViewModel
import com.panassevich.musicplayer.domain.usecase.ControlPlaybackUseCase
import com.panassevich.musicplayer.domain.usecase.GetPlaybackStateUseCase
import com.panassevich.musicplayer.domain.usecase.StartPlayTrackUseCase
import javax.inject.Inject

class PlayerViewModel @Inject constructor(
    private val getPlaybackStateUseCase: GetPlaybackStateUseCase,
    private val controlPlaybackUseCase: ControlPlaybackUseCase,
    private val startPlayTrackUseCase: StartPlayTrackUseCase
) : ViewModel() {


    val state = getPlaybackStateUseCase()

    fun playTrack(trackId: Long) {
        startPlayTrackUseCase(trackId)
    }

    fun pause() {
        controlPlaybackUseCase.pause()
    }

    fun resume() {
        controlPlaybackUseCase.resume()
    }

    fun playPrevious() {
        controlPlaybackUseCase.playPrevious()
    }

    fun playNext() {
        controlPlaybackUseCase.playNext()
    }

    fun seekTo(fraction: Float) {
        controlPlaybackUseCase.seekTo(30*fraction)
    }

}