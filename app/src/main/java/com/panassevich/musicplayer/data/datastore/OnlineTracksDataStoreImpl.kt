package com.panassevich.musicplayer.data.datastore

import android.util.Log
import com.panassevich.musicplayer.data.mapper.toEntities
import com.panassevich.musicplayer.data.network.api.ApiService
import com.panassevich.musicplayer.domain.entity.Track
import com.panassevich.musicplayer.domain.datastore.OnlineTracksDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.Utf8
import java.net.URLEncoder
import javax.inject.Inject

class OnlineTracksDataStoreImpl @Inject constructor(
    private val apiService: ApiService
) : OnlineTracksDataStore {

    override suspend fun searchOnlineTracks(query: String, nextFrom: Int): List<Track> =
        withContext(Dispatchers.IO) {
            //parts that come from "unreliable sources" must be encoded to prevent errors
            val escapedQuery = URLEncoder.encode(query, Charsets.UTF_8.name())
            val result = apiService.search(escapedQuery, nextFrom = nextFrom)
            result.tracksList.toEntities()
        }

    override suspend fun getOnlineChart(nextFrom: Int): List<Track> =
        withContext(Dispatchers.IO) {
        val result = apiService.getChart(nextFrom = nextFrom)
        result.tracksList.toEntities()
    }
}