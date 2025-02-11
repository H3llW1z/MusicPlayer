package com.panassevich.musicplayer.data.network.dto

import com.google.gson.annotations.SerializedName

data class TrackDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("duration")
    val duration: Int,
    @SerializedName("preview")
    val previewUrl: String,
    @SerializedName("artist")
    val artist: ArtistDto,
    @SerializedName("album")
    val album: AlbumDto
)
