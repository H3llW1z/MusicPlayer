package com.panassevich.musicplayer.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.panassevich.musicplayer.presentation.main.MainScreen
import com.panassevich.musicplayer.presentation.ui.theme.MusicPlayerTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicPlayerTheme {
                MainScreen()
            }
        }
    }

}