package com.panassevich.musicplayer.data.datastore

import com.panassevich.musicplayer.data.mapper.toEntities
import com.panassevich.musicplayer.data.network.api.ApiService
import com.panassevich.musicplayer.domain.entity.Track
import com.panassevich.musicplayer.domain.datastore.OnlineTracksDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OnlineTracksDataStoreImpl @Inject constructor(
    private val apiService: ApiService
) : OnlineTracksDataStore {

    override suspend fun searchOnlineTracks(query: String): List<Track> =
        withContext(Dispatchers.IO) {
            val result = apiService.search(query)
            result.tracksList.toEntities()
        }

    override suspend fun getOnlineChart(): List<Track> = withContext(Dispatchers.IO) {
        val result = apiService.getChart()
        result.tracks.tracksList.toEntities()
    }
}