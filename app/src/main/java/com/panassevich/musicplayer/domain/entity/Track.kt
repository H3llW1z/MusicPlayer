package com.panassevich.musicplayer.domain.entity

import java.util.Calendar

data class Track(
    val id: Int,
    val name: String,
    val albumName: String?,
    val author: String,
    val duration: Calendar,
    val coverUrl: String?,
)
