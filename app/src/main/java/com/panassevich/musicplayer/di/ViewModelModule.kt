package com.panassevich.musicplayer.di

import androidx.lifecycle.ViewModel
import com.panassevich.musicplayer.presentation.online.OnlineTracksViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(OnlineTracksViewModel::class)
    fun bindNewsFeedViewModel(viewModel: OnlineTracksViewModel): ViewModel

}