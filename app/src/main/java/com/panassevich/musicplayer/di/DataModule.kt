package com.panassevich.musicplayer.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.panassevich.musicplayer.data.datastore.LocalTracksDataStoreImpl
import com.panassevich.musicplayer.data.datastore.OnlineTracksDataStoreImpl
import com.panassevich.musicplayer.data.network.api.ApiFactory
import com.panassevich.musicplayer.data.network.api.ApiService
import com.panassevich.musicplayer.data.repository.PlaybackRepositoryImpl
import com.panassevich.musicplayer.domain.datastore.LocalTracksDataStore
import com.panassevich.musicplayer.domain.datastore.OnlineTracksDataStore
import com.panassevich.musicplayer.domain.repository.PlaybackRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @[ApplicationScope Binds]
    fun bindLocalTracksDataStore(impl: LocalTracksDataStoreImpl): LocalTracksDataStore

    @[ApplicationScope Binds]
    fun bindOnlineTracksDataStore(impl: OnlineTracksDataStoreImpl): OnlineTracksDataStore

    @[ApplicationScope Binds]
    fun bindPlaybackRepository(impl: PlaybackRepositoryImpl): PlaybackRepository

    companion object {

        @[ApplicationScope Provides]
        fun provideApiService(): ApiService = ApiFactory.apiService

//        @[ApplicationScope Provides]
//        fun provideExoplayer(context: Context): ExoPlayer = ExoPlayer.Builder(context).build()
    }
}