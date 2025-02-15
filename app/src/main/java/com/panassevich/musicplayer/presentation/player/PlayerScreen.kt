package com.panassevich.musicplayer.presentation.player

import androidx.annotation.DrawableRes
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.panassevich.musicplayer.R
import com.panassevich.musicplayer.domain.entity.PlaybackState
import com.panassevich.musicplayer.getApplicationComponent
import com.panassevich.musicplayer.navigation.Route
import com.panassevich.musicplayer.presentation.ui.theme.MusicPlayerTheme
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

@Composable
fun PlayerScreen(
    paddingValues: PaddingValues,
    trackIdToPlay: Long,
    onClickBack: () -> Unit
) {
    val component = getApplicationComponent()
    val viewModel: PlayerViewModel = viewModel(factory = component.getViewModelFactory())
    val state: State<PlaybackState> = viewModel.state.collectAsState(PlaybackState.NoTrack)

    LaunchedEffect(true) {
        if (trackIdToPlay != Route.Player.NO_ID) {
            viewModel.playTrack(trackIdToPlay)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
    ) {
        IconButton(
            modifier = Modifier.padding(top = 16.dp),
            onClick = {
                onClickBack()
            }
        ) {
            Icon(
                modifier = Modifier.size(40.dp),
                painter = painterResource(R.drawable.ic_arrow_down),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        when (val playbackState = state.value) {
            PlaybackState.NoTrack -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.text_no_playback),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            is PlaybackState.Current -> {
                PlayerContent(playbackState, viewModel)
            }
        }
    }
}

@Composable
fun PlayerContent(state: PlaybackState.Current, viewModel: PlayerViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TrackInfo(state)
        Spacer(modifier = Modifier.height(32.dp))
        var sliderPosition by remember { mutableFloatStateOf(0f) }
        Slider(
            value = sliderPosition,
            onValueChange = { newValue -> sliderPosition = newValue  },
            onValueChangeFinished = {
                //call seek
            },
        )
        Row(
        ) {
            Text(
                text = formatDurationTime(state.secondsFromStart)
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = formatDurationTime(state.track.durationSeconds)
            )
        }

        Controls(
            state = state,
            onClickPrevious = { viewModel.playPrevious() },
            onClickNext = { viewModel.playNext() },
            onClickPlayPause = { if (state.isPlaying()) viewModel.pause() else viewModel.resume() }
        )
    }
}

private fun formatDurationTime(durationSeconds: Int) =
    durationSeconds.seconds.toComponents { minutes, seconds, _ ->
        String.format(
            Locale.getDefault(),
            "%d:%02d",
            minutes,
            seconds,
        )
    }

@Composable
private fun TrackInfo(state: PlaybackState.Current) {
    AsyncImage(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp)),
        model = state.track.coverUrlHD,
        placeholder = painterResource(R.drawable.cover_placeholder),
        error = painterResource(R.drawable.cover_placeholder),
        contentDescription = null
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = state.track.name,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = state.track.artistName,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
    val albumName = state.track.albumName
    if (albumName != null && albumName != state.track.name) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.template_album_name, albumName),
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun Controls(
    state: PlaybackState.Current,
    onClickPrevious: () -> Unit,
    onClickPlayPause: () -> Unit,
    onClickNext: () -> Unit
) {
    val switchButtonsSize = 50.dp
    val indication = ripple()
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(
            16.dp,
            alignment = Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlaybackControlButton(
            modifier = Modifier.size(switchButtonsSize),
            iconResId = R.drawable.ic_previous,
            enabled = state.hasPrevious,
            onClick = { onClickPrevious() },
            indication = indication
        )
        PlaybackControlButton(
            modifier = Modifier.size(80.dp),
            iconResId = if (state.isPlaying()) R.drawable.ic_pause else R.drawable.ic_play,
            onClick = { onClickPlayPause() },
            indication = indication
        )
        PlaybackControlButton(
            modifier = Modifier.size(switchButtonsSize),
            iconResId = R.drawable.ic_next,
            enabled = state.hasNext,
            onClick = { onClickNext() },
            indication = indication
        )
    }
}

@Composable
private fun PlaybackControlButton(
    modifier: Modifier = Modifier,
    @DrawableRes iconResId: Int,
    enabled: Boolean = true,
    onClick: () -> Unit,
    indication: IndicationNodeFactory //better move indication to outer scope to save allocations. This object can be reused in multiple components
) {
    Box(  //used Box instead of IconButton to increase ripple radius (IconButton has embedded fixed radius)
        modifier = modifier
            .clip(CircleShape)
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = indication
            ) {
                onClick()
            }
    ) {
        val alpha = if (enabled) 1f else 0.5f
        Icon(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .alpha(alpha),
            painter = painterResource(iconResId),
            contentDescription = null
        )
    }
}

private fun PlaybackState.isPlaying(): Boolean {
    if (this !is PlaybackState.Current) return false
    return currentState == PlaybackState.Current.CurrentState.PLAYING
}

@Composable
@Preview
private fun PlayerPreview() {
    MusicPlayerTheme(darkTheme = false) {
        PlayerScreen(
            paddingValues = PaddingValues(0.dp),
            trackIdToPlay = 0
        ) {

        }
    }
}