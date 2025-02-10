package com.panassevich.musicplayer.domain.repository

import com.panassevich.musicplayer.domain.entity.Track

interface OnlineTracksRepository {

    suspend fun search(query: String): List<Track>

    suspend fun getChart(): List<Track>
}