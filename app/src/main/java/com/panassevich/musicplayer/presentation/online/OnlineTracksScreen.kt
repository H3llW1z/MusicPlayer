package com.panassevich.musicplayer.presentation.online

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.panassevich.musicplayer.R
import com.panassevich.musicplayer.domain.entity.Track
import com.panassevich.musicplayer.getApplicationComponent

@Composable
fun OnlineTracksScreen(paddingValues: PaddingValues, onTrackClick: (Track) -> Unit) {

    val component = getApplicationComponent()
    val viewModel: OnlineTracksViewModel = viewModel(factory = component.getViewModelFactory())

    val screenState = viewModel.state.collectAsState()
    val searchState = rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(paddingValues)
    ) {
        SearchField(
            state = searchState,
            onValueChange = { text ->
                searchState.value = text
            },
            onSearchClicked = { query ->
                viewModel.search(query)
            }
        )
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
                val descriptionTextResId = when (state.type) {
                    TracksType.CHART -> R.string.text_chart
                    TracksType.SEARCH -> R.string.text_search_results
                }
                Text(
                    modifier = Modifier.padding(start = 12.dp, bottom = 4.dp),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    text = stringResource(descriptionTextResId)
                )
                TrackList(
                    state.tracks,
                    onTrackClick,
                )
            }
        }
    }


}

@Composable
private fun TrackList(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit
) {
    LazyColumn(
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
fun SearchField(
    modifier: Modifier = Modifier,
    state: State<String>,
    onValueChange: ((String) -> Unit),
    onSearchClicked: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        value = state.value,
        keyboardActions = KeyboardActions(
            onDone = {
                onSearchClicked(state.value)
                focusManager.clearFocus()
            }
        ),
        onValueChange = { onValueChange(it) },
        singleLine = true,
        shape = RoundedCornerShape(5.dp),
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            if (state.value.isNotEmpty()) {
                IconButton(onClick = {
                    onValueChange("")
                    onSearchClicked("")
                }) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                }
            }
        },
        placeholder = {
            Text(
                text = stringResource(R.string.placeholder_search)
            )
        }
    )
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