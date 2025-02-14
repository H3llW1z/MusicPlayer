package com.panassevich.musicplayer.domain.entity

data class Track(
    val id: Long,
    val name: String,
    val albumName: String?,
    val artistName: String,
    val durationSeconds: Int,
    val coverUrlHD: String?,   //high quality cover
    val coverUrlRegular: String?,   //regular quality cover
    val previewUrl: String
)
