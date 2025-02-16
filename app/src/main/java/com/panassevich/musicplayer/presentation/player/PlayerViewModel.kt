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
    private val getPlaybackStateUseCase: GetPlaybackStateUseCase,
    private val controlPlaybackUseCase: ControlPlaybackUseCase,
    private val startPlayTrackUseCase: StartPlayTrackUseCase
) : ViewModel() {

    val playbackState = getPlaybackStateUseCase.getCurrentState()

    val positionInTrackFlow = getPlaybackStateUseCase.getCurrentPositionInTrack()
    private val _sliderPosition = MutableStateFlow(0L)
    val sliderPosition = _sliderPosition.asStateFlow()


    init {
        viewModelScope.launch {
            positionInTrackFlow.collect{
                if(!isDragging) {
                    _sliderPosition.emit(it)
                }
            }
        }

    }



    var isDragging = false

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

    fun seekTo(ms: Long) {
        controlPlaybackUseCase.seekTo(ms)
    }

}