package com.panassevich.musicplayer.data.datastore

import com.panassevich.musicplayer.domain.entity.Track
import com.panassevich.musicplayer.domain.datastore.LocalTracksDataStore
import javax.inject.Inject

class LocalTracksDataStoreImpl @Inject constructor() : LocalTracksDataStore {

    override suspend fun getAllLocalTracks(): List<Track> {
        TODO("Not yet implemented")
    }

    override suspend fun searchLocalTracks(query: String): List<Track> {
        TODO("Not yet implemented")
    }
}