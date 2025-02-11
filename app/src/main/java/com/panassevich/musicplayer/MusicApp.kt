package com.panassevich.musicplayer

import android.app.Application
import com.panassevich.musicplayer.di.ApplicationComponent
import com.panassevich.musicplayer.di.DaggerApplicationComponent

class MusicApp: Application() {

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.factory().create(this)
    }
}