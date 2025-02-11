package com.panassevich.musicplayer.data.mapper

import com.panassevich.musicplayer.data.network.dto.TrackDto
import com.panassevich.musicplayer.domain.entity.Track

fun TrackDto.toEntity() = Track(
    id = id,
    name = title,
    albumName = album.title,
    artistName = artist.name,
    durationSeconds = duration,
    coverUrl = album.coverUrl,
    previewUrl = previewUrl
)

fun List<TrackDto>.toEntities() = map { it.toEntity() }