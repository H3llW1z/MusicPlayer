package com.panassevich.musicplayer.data.repository

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.panassevich.musicplayer.data.network.api.ApiService
import com.panassevich.musicplayer.domain.datastore.LocalTracksDataStore
import com.panassevich.musicplayer.domain.datastore.OnlineTracksDataStore
import com.panassevich.musicplayer.domain.entity.OnlineTracksResult
import com.panassevich.musicplayer.domain.entity.OnlineTracksType
import com.panassevich.musicplayer.domain.entity.PlaybackState
import com.panassevich.musicplayer.domain.entity.Track
import com.panassevich.musicplayer.domain.repository.PlaybackRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.stateIn
import java.util.LinkedList
import javax.inject.Inject

class PlaybackRepositoryImpl @Inject constructor(
    private val player: ExoPlayer,
    private val localTracksDataStore: LocalTracksDataStore,
    private val onlineTracksDataStore: OnlineTracksDataStore
) : PlaybackRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val nextDataNeededRequests =
        MutableSharedFlow<LoadRequest>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val _onlineTracks: MutableList<Track> = LinkedList() //TODO("Why LinkedList")
    private val onlineTracks: List<Track>
        get() = _onlineTracks.toList()

    private var currentLoadState: CurrentLoadState = CurrentLoadState(OnlineTracksType.CHART, 0)

    private val onlineTracksFlow: StateFlow<OnlineTracksResult> = flow {

        nextDataNeededRequests.emit(
            LoadRequest(
                currentLoadState.tracksType,
                currentLoadState.query
            )
        )

        nextDataNeededRequests.collect { request ->
            if (currentLoadState.tracksType != request.tracksType || currentLoadState.query != request.query) {
                currentLoadState =
                    CurrentLoadState(tracksType = request.tracksType, nextFrom = 0, request.query)
                _onlineTracks.clear()
            }

            if (currentLoadState.endReached) {
                emit(getOnlineTracksResult(hasMoreTracks = false))
                return@collect
            }

            val nextFrom = currentLoadState.nextFrom
            val response = when (currentLoadState.tracksType) {
                OnlineTracksType.CHART -> {
                    val result = onlineTracksDataStore.getOnlineChart(nextFrom)
                    Log.d("TEST", "chart loaded")
                    result
                }

                OnlineTracksType.SEARCH -> {
                    val query = currentLoadState.query
                        ?: throw IllegalStateException("For search mode query must be not null!")
                    val rawResponse = onlineTracksDataStore.searchOnlineTracks(query, nextFrom)
                    Log.d("TEST", "search loaded")
                    val lastExisted = _onlineTracks.lastOrNull()
                    if (lastExisted != null) {
                        val overlapIndex = rawResponse.indexOfFirst { it.id == lastExisted.id }
                        if (overlapIndex == -1) {
                            rawResponse // no overlapping
                        } else {
                            rawResponse.drop(overlapIndex + 1) //drop overlapping tracks
                        }
                    } else {
                        rawResponse
                    }
                }
            }
            if (response.isEmpty()) {
                currentLoadState = currentLoadState.copy(endReached = true)
                emit(getOnlineTracksResult(hasMoreTracks = false))
                return@collect
            }
            val currentNextFrom = currentLoadState.nextFrom
            currentLoadState =
                currentLoadState.copy(nextFrom = currentNextFrom + ApiService.DEFAULT_PAGE_LIMIT)
            _onlineTracks.addAll(response)
            emit(getOnlineTracksResult())
        }
    }.catch { exception ->
        emit(getOnlineTracksResult(hasError = true))
        throw exception
    }.retry {
        delay(RETRY_TIMEOUT_MILLIS)
        true
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = getOnlineTracksResult()
    )

    private fun getOnlineTracksResult(hasMoreTracks: Boolean = true, hasError: Boolean = false) =
        OnlineTracksResult(currentLoadState.tracksType, onlineTracks, hasMoreTracks, hasError)

    override fun getOnlineTracks(): StateFlow<OnlineTracksResult> = onlineTracksFlow

    override suspend fun loadNextData() {
        nextDataNeededRequests.emit(
            LoadRequest(
                currentLoadState.tracksType,
                currentLoadState.query
            )
        )
    }

    private val playbackStateFlow: StateFlow<PlaybackState> = flow {

        while (true) {

            val track: Track? = getCurrentTrack()

            val state = if (track == null) {
                PlaybackState.NoTrack
            } else {
                PlaybackState.Current(
                    track = track,
                    if (player.isPlaying) PlaybackState.Current.CurrentState.PLAYING else PlaybackState.Current.CurrentState.PAUSED,
                    secondsFromStart = (player.currentPosition / 1000).toInt(),
                    progressPercent = calculateProgressPercent(player.currentPosition, player.duration),
                    hasPrevious = player.hasPreviousMediaItem(),
                    hasNext = player.hasNextMediaItem()
                )
            }
            emit(state)
            delay(1000L)
        }
    }.stateIn(
        scope = CoroutineScope(Dispatchers.Main),
        started = SharingStarted.Lazily,
        initialValue = PlaybackState.NoTrack
    )

    override fun getCurrentState(): StateFlow<PlaybackState> =  playbackStateFlow

    override fun resume() {
        player.play()
    }

    override fun startPlay(trackId: Long) {
        val playlist = onlineTracks.toMediaItems()
        player.setMediaItems(playlist)
        if (player.playbackState == Player.STATE_IDLE) {
            player.prepare()
        }
        val index = onlineTracks.indexOfFirst { it.id == trackId }
        if (index > 0) {
            player.seekTo(index, 0L)
        }
        player.play()
    }

    override fun pause() {
        player.pause()
    }

    override fun seekTo(fraction: Float) {
        val track = getCurrentTrack()
        if (track != null) {
            player.duration
            player.seekTo((track.durationSeconds * 1000L * fraction).toLong())
        }
    }

    override fun playPrevious() {
        player.seekToPrevious()
    }

    override fun playNext() {
        player.seekToNext()
    }

    override suspend fun getAllLocalTracks(): List<Track> {
        TODO("Not yet implemented")
    }

    override suspend fun searchLocalTracks(query: String): List<Track> {
        TODO("Not yet implemented")
    }

    override suspend fun searchOnlineTracks(query: String) {
        Log.d("PlaybackRepositoryImpl", "searchOnlineTracks")
        if (query.isBlank()) {
            return
        }
        nextDataNeededRequests.emit(LoadRequest(OnlineTracksType.SEARCH, query))
    }

    override suspend fun loadOnlineChart() {
        Log.d("PlaybackRepositoryImpl", "loadOnlineChart")
        nextDataNeededRequests.emit(LoadRequest(OnlineTracksType.CHART))
    }

    private fun getCurrentTrack(): Track? =
        player.currentMediaItem?.localConfiguration?.tag as? Track

    private fun List<Track>.toMediaItems() = map { track ->
        MediaItem.Builder().setUri(track.previewUrl).setMediaId(track.id.toString()).setTag(track)
            .build()
    }

    private fun calculateProgressPercent(currentPosition: Long, duration: Long): Int {
        return if (duration > 0) ((currentPosition.toDouble() / duration) * 100).toInt() else 0
    }

    private data class LoadRequest(val tracksType: OnlineTracksType, val query: String? = null)

    private data class CurrentLoadState(
        val tracksType: OnlineTracksType,
        val nextFrom: Int = 0,
        val query: String? = null,
        val endReached: Boolean = false
    )

    private companion object {
        private const val RETRY_TIMEOUT_MILLIS = 2000L
        private const val DELA = 2000L
    }
}