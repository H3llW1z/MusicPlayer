package com.panassevich.musicplayer

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.panassevich.musicplayer.di.ApplicationComponent
import com.panassevich.musicplayer.di.DaggerApplicationComponent

class MusicApp: Application() {

    lateinit var component: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        component = DaggerApplicationComponent.factory().create(this)
    }
}

@Composable
fun getApplicationComponent(): ApplicationComponent {
    return (LocalContext.current.applicationContext as MusicApp).component
}