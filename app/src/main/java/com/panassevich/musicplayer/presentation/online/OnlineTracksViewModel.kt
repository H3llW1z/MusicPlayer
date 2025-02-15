package com.panassevich.musicplayer.presentation.online

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panassevich.musicplayer.domain.usecase.GetOnlineTracksUseCase
import com.panassevich.musicplayer.domain.usecase.LoadChartTracksUseCase
import com.panassevich.musicplayer.domain.usecase.LoadNextDataUseCase
import com.panassevich.musicplayer.domain.usecase.SearchOnlineTracksUseCase
import com.panassevich.musicplayer.extensions.mergeWith
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class OnlineTracksViewModel @Inject constructor(
    private val loadChartTracksUseCase: LoadChartTracksUseCase,
    private val searchOnlineTracksUseCase: SearchOnlineTracksUseCase,
    private val loadNextDataUseCase: LoadNextDataUseCase,
    getOnlineTracksUseCase: GetOnlineTracksUseCase
) : ViewModel() {

    private val loadNextDataEvents = MutableSharedFlow<Unit>()
    private val loadNextDataFlow = flow {
        loadNextDataEvents.collect {
            emit(
                OnlineTracksScreenState.Content(
                    tracks = onlineTracksFlow.value.tracks,
                    type = onlineTracksFlow.value.tracksType,
                    nextDataIsLoading = onlineTracksFlow.value.hasMoreTracks,
                    hasError = onlineTracksFlow.value.hasError
                )
            )
        }
    }

    private val onlineTracksFlow = getOnlineTracksUseCase()

    val state = onlineTracksFlow.map { result ->
        val state = if (result.tracks.isEmpty() && !result.hasMoreTracks) {
            OnlineTracksScreenState.NoTracksFound
        } else {
            OnlineTracksScreenState.Content(result.tracks, result.tracksType, hasError = result.hasError)
        }
        state as OnlineTracksScreenState
    }.mergeWith(loadNextDataFlow)

    fun search(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                loadChartTracksUseCase()
                return@launch
            }
            searchOnlineTracksUseCase(query)
        }
    }

    fun loadNextTracks() {
        viewModelScope.launch {
            loadNextDataEvents.emit(Unit)
            loadNextDataUseCase()
        }
    }

}