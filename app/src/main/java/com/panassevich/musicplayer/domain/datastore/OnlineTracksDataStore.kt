package com.panassevich.musicplayer.domain.datastore

import com.panassevich.musicplayer.domain.entity.Track

interface OnlineTracksDataStore {

    suspend fun searchOnlineTracks(query: String, nextFrom: Int = 0): List<Track>

    suspend fun getOnlineChart(nextFrom: Int = 0): List<Track>
}