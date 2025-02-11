package com.panassevich.musicplayer.domain.datastore

import com.panassevich.musicplayer.domain.entity.Track

interface OnlineTracksDataStore {

    suspend fun searchOnlineTracks(query: String): List<Track>

    suspend fun getOnlineChart(): List<Track>
}