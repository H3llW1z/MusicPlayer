package com.panassevich.musicplayer.data.mapper

import com.panassevich.musicplayer.data.network.dto.TrackDto
import com.panassevich.musicplayer.domain.entity.Track

fun TrackDto.toEntity() = Track(
    id = id,
    name = title,
    albumName = album.title,
    artistName = artist.name,
    durationMs = 30 * 1000L, // Stab duration as 30 because preview track always 30 second long
    coverUrlRegular = album.coverUrlRegular,
    coverUrlHD = album.coverUrlHD,
    previewUrl = previewUrl
)

fun List<TrackDto>.toEntities() = map { it.toEntity() }