package com.panassevich.musicplayer.domain.repository

import com.panassevich.musicplayer.domain.entity.Track

interface LocalTracksRepository {

    suspend fun getAllTracks(): List<Track>

    suspend fun search(query: String): List<Track>
}