package com.panassevich.musicplayer.presentation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panassevich.musicplayer.domain.usecase.ControlPlaybackUseCase
import com.panassevich.musicplayer.domain.usecase.GetPlaybackStateUseCase
import com.panassevich.musicplayer.domain.usecase.StartPlayTrackUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlayerViewModel @Inject constructor(
    getPlaybackStateUseCase: GetPlaybackStateUseCase,
    private val controlPlaybackUseCase: ControlPlaybackUseCase,
    private val startPlayTrackUseCase: StartPlayTrackUseCase
) : ViewModel() {

    val playbackState = getPlaybackStateUseCase.getCurrentState()

    private val positionInTrackFlow = getPlaybackStateUseCase.getCurrentPositionInTrack()
    private val _sliderPosition = MutableStateFlow(0L)
    val sliderPosition = _sliderPosition.asStateFlow()

    init {
        viewModelScope.launch {
            positionInTrackFlow.collect {
                if (!isDragging) {
                    _sliderPosition.emit(it)
                }
            }
        }

    }

    //when user is dragging slider updated by drag not by track played time
    private var isDragging = false

    fun onSliderValueChange(position: Float) {
        isDragging = true
        viewModelScope.launch {
            _sliderPosition.emit(position.toLong())
        }
    }

    fun onSliderValueChangeFinished() {
        isDragging = false
        seekTo(sliderPosition.value)
    }

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
        viewModelScope.launch {
            _sliderPosition.emit(0L)
        }
    }

    fun playNext() {
        controlPlaybackUseCase.playNext()
        viewModelScope.launch {
            _sliderPosition.emit(0L)
        }
    }

    private fun seekTo(ms: Long) {
        controlPlaybackUseCase.seekTo(ms)
    }

}