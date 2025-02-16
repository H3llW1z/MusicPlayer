package com.panassevich.musicplayer.data.repository

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
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
import kotlinx.coroutines.Job
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
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.LinkedList
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class PlaybackRepositoryImpl @Inject constructor(
    private val player: ExoPlayer,
    private val localTracksDataStore: LocalTracksDataStore,
    private val onlineTracksDataStore: OnlineTracksDataStore
) : PlaybackRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val coroutineScopeMain = CoroutineScope(Dispatchers.Main)
    private val nextDataNeededRequests =
        MutableSharedFlow<LoadRequest>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val _onlineTracks: MutableList<Track> = LinkedList() //TODO("Why LinkedList")
    private val onlineTracks: List<Track>
        get() = _onlineTracks.toList()

    init {
        player.addListener(object : Player.Listener {

            var job: Job? = null

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    job?.cancel()
                    job = coroutineScopeMain.launch {
                        while (isActive) {  //end cycle if coroutine is not active anymore
                            delay(1.seconds / 2)  //update state 10 times per second
                            currentPositionInTrack.emit(player.currentPosition)
                        }
                    }
                } else {
                    job?.cancel()
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                player.prepare()
            }

            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)
                coroutineScope.launch {
                    updatePlaybackStateEvents.emit(Unit)
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                if(reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                    coroutineScopeMain.launch {
                        currentPositionInTrack.emit(player.currentPosition)
                    }
                }
            }

        })
    }

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

    override fun getOnlineTracks(): StateFlow<OnlineTracksResult> = onlineTracksFlow

    override suspend fun loadNextData() {
        nextDataNeededRequests.emit(
            LoadRequest(
                currentLoadState.tracksType,
                currentLoadState.query
            )
        )
    }

    private val updatePlaybackStateEvents =
        MutableSharedFlow<Unit>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val playbackStateFlow: StateFlow<PlaybackState> = flow {

        updatePlaybackStateEvents.collect {
            val track: Track? = getCurrentTrack()
            val state = if (track == null) {
                PlaybackState.NoTrack
            } else {
                PlaybackState.CurrentTrack(
                    track = track,
                    currentState = if (player.playWhenReady) PlaybackState.CurrentState.PLAYING else PlaybackState.CurrentState.PAUSED,
                    hasPrevious = player.hasPreviousMediaItem(),
                    hasNext = player.hasNextMediaItem()
                )
            }
            emit(state)
        }
    }.stateIn(
        scope = coroutineScopeMain,
        started = SharingStarted.Lazily,
        initialValue = PlaybackState.NoTrack
    )

    private val currentPositionInTrack = MutableStateFlow(0L)

    override fun getCurrentPositionInTrack(): StateFlow<Long> = currentPositionInTrack.asStateFlow()

    override fun getCurrentState(): StateFlow<PlaybackState> = playbackStateFlow

    override fun resume() {
        Log.d("PLAYER_TEST", "resume")
        player.play()
    }

    override fun startPlay(trackId: Long) {
        val playlist = onlineTracks.toMediaItems()
        Log.d("PLAYER_TEST", "startPlay")
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
        Log.d("PLAYER_TEST", "pause")
        player.pause()
    }

    override fun seekTo(ms: Long) {
        Log.d("PLAYER_TEST", "seekTo")
        val track = getCurrentTrack()
        if (track != null) {
            player.duration
            player.seekTo(ms)
        }
    }

    override fun playPrevious() {
        Log.d("PLAYER_TEST", "playPrevious")
        player.seekToPrevious()
        if (!player.isPlaying) {
            player.play()
        }
    }

    override fun playNext() {
        Log.d("PLAYER_TEST", "playNext")
        player.seekToNext()
        if (!player.isPlaying) {
            player.play()
        }
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

    private fun getOnlineTracksResult(hasMoreTracks: Boolean = true, hasError: Boolean = false) =
        OnlineTracksResult(currentLoadState.tracksType, onlineTracks, hasMoreTracks, hasError)

    private fun getCurrentTrack(): Track? =
        player.currentMediaItem?.localConfiguration?.tag as? Track

    private fun List<Track>.toMediaItems() = map { track ->
        MediaItem.Builder().setUri(track.previewUrl).setMediaId(track.id.toString()).setTag(track)
            .build()
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