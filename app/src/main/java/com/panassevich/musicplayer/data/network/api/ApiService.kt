package com.panassevich.musicplayer.data.network.api

import com.panassevich.musicplayer.data.network.dto.TracksDataDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("chart/0/tracks")
    suspend fun getChart(
        @Query("index") nextFrom: Int = 0,
        @Query("limit") limit: Int = DEFAULT_PAGE_LIMIT
    ): TracksDataDto

    @GET("search/track")
    suspend fun search(
        @Query("q") query: String,
        @Query("index") nextFrom: Int = 0,
        @Query("limit") limit: Int = DEFAULT_PAGE_LIMIT
    ): TracksDataDto

    companion object{
        const val DEFAULT_PAGE_LIMIT = 25
    }
}