package com.panassevich.musicplayer.data.network.dto

import com.google.gson.annotations.SerializedName

data class ArtistDto (
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String
)
