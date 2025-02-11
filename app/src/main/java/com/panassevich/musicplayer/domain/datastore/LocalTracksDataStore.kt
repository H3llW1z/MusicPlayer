package com.panassevich.musicplayer.domain.datastore

import com.panassevich.musicplayer.domain.entity.Track

interface LocalTracksDataStore {

    suspend fun getAllLocalTracks(): List<Track>

    suspend fun searchLocalTracks(query: String): List<Track>
}