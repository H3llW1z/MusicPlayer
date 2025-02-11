package com.panassevich.musicplayer.data.network.dto

import com.google.gson.annotations.SerializedName

data class ChartResponseDto(
    @SerializedName("tracks")
    val tracks: TracksDataDto
)
