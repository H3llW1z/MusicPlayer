package com.panassevich.musicplayer.presentation.player

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PlayerScreen(paddingValues: PaddingValues) {
    Text(modifier = Modifier.padding(paddingValues), text = "Player tracks content (placeholder)")
}