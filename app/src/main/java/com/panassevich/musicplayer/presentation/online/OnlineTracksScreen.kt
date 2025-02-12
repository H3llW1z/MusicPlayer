package com.panassevich.musicplayer.presentation.online

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.panassevich.musicplayer.domain.entity.Track
import com.panassevich.musicplayer.getApplicationComponent

@Composable
fun OnlineTracksScreen(paddingValues: PaddingValues, onTrackClick: (Track) -> Unit) {

    val component = getApplicationComponent()
    val viewModel: OnlineTracksViewModel = viewModel(factory = component.getViewModelFactory())

    val screenState = viewModel.state.collectAsState()

    when (val state = screenState.value) {
        is OnlineTracksScreenState.Initial -> {}
        is OnlineTracksScreenState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is OnlineTracksScreenState.Content -> {
            TrackList(paddingValues, state.tracks, onTrackClick)
        }
    }

}

@Composable
private fun TrackList(
    paddingValues: PaddingValues,
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(paddingValues),
        contentPadding = PaddingValues(
            top = 16.dp,
            start = 8.dp,
            end = 8.dp,
            bottom = 32.dp
        ),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(items = tracks, key = { it.id }) { track ->
            TrackCard(
                modifier = Modifier.clickable {
                    onTrackClick(track)
                },
                track = track
            )
        }
    }
}

@Composable
private fun TrackCard(modifier: Modifier = Modifier, track: Track) {
    Row(
        modifier = modifier.padding(8.dp)
    ) {
        AsyncImage(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(5.dp)),
            model = track.coverUrl,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = track.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = track.artistName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}