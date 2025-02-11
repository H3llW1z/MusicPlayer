package com.panassevich.musicplayer.data.network.dto

import com.google.gson.annotations.SerializedName

data class TracksDataDto (
    @SerializedName("data")
    val tracksList: List<TrackDto>,
    @SerializedName("total")
    val totalCount: Int
)