package com.panassevich.musicplayer.data.network.api

import com.panassevich.musicplayer.data.network.dto.ChartResponseDto
import com.panassevich.musicplayer.data.network.dto.TracksDataDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("chart")
    suspend fun getChart(): ChartResponseDto

    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("index") nextFrom: Int = 0
    ): TracksDataDto
}