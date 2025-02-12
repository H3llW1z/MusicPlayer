package com.panassevich.musicplayer.presentation.online

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panassevich.musicplayer.domain.usecase.GetChartTracksUseCase
import com.panassevich.musicplayer.domain.usecase.SearchOnlineTracksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class OnlineTracksViewModel @Inject constructor(
    private val getChartTracksUseCase: GetChartTracksUseCase,
    private val searchOnlineTracksUseCase: SearchOnlineTracksUseCase,
): ViewModel() {

    private val _state = MutableStateFlow<OnlineTracksScreenState>(OnlineTracksScreenState.Initial)
    val state = _state.asStateFlow()

    init {
        loadChart()
    }

    fun loadChart() {
        viewModelScope.launch {
            _state.value = OnlineTracksScreenState.Loading
            val result = getChartTracksUseCase()
            _state.value = OnlineTracksScreenState.Content(result)
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            _state.value = OnlineTracksScreenState.Loading
            val result = searchOnlineTracksUseCase(query)
            _state.value = OnlineTracksScreenState.Content(result)
        }
    }

}