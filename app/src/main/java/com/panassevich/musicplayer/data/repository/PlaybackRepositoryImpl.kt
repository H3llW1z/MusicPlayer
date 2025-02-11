package com.panassevich.musicplayer.data.repository

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.panassevich.musicplayer.domain.entity.PlaybackState
import com.panassevich.musicplayer.domain.entity.Track
import com.panassevich.musicplayer.domain.datastore.LocalTracksDataStore
import com.panassevich.musicplayer.domain.datastore.OnlineTracksDataStore
import com.panassevich.musicplayer.domain.repository.PlaybackRepository
import javax.inject.Inject

class PlaybackRepositoryImpl @Inject constructor(
    private val player: ExoPlayer,
    private val localTracksDataStore: LocalTracksDataStore,
    private val onlineTracksDataStore: OnlineTracksDataStore
) : PlaybackRepository {

    private var onlineTracks = listOf<Track>()

    override fun getCurrentState(): PlaybackState {
        val track: Track? = getCurrentTrack()
        val state = if (track == null) {
            PlaybackState.NoTrack
        } else {
            val state = PlaybackState.Current(
                track = track,
                if (player.isPlaying) PlaybackState.Current.CurrentState.PLAYING else PlaybackState.Current.CurrentState.PAUSED,
                secondsFromStart = (player.currentPosition / 1000).toInt(),
                progressPercent = calculateProgressPercent(player.currentPosition, player.duration),
                hasPrevious = player.hasPreviousMediaItem(),
                hasNext = player.hasNextMediaItem()
            )
            state
        }
        return state
    }

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

    override fun seekTo(seconds: Int) {
        player.seekTo(seconds * 1000L)
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

    override suspend fun searchOnlineTracks(query: String): List<Track> {
        val tracks = onlineTracksDataStore.searchOnlineTracks(query)
        onlineTracks = tracks
        return tracks
    }

    override suspend fun getOnlineChart(): List<Track> {
        val tracks = onlineTracksDataStore.getOnlineChart()
        onlineTracks = tracks
        return tracks
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
}