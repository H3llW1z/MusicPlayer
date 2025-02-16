package com.panassevich.musicplayer.presentation.player

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.panassevich.musicplayer.R
import com.panassevich.musicplayer.domain.entity.PlaybackState
import com.panassevich.musicplayer.getApplicationComponent
import com.panassevich.musicplayer.navigation.Route
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun PlayerScreen(
    paddingValues: PaddingValues,
    trackIdToPlay: Long,
    onClickBack: () -> Unit
) {
    val component = getApplicationComponent()
    val viewModel: PlayerViewModel = viewModel(factory = component.getViewModelFactory())
    val state: State<PlaybackState> = viewModel.playbackState.collectAsState()
    val sliderPosition = viewModel.sliderPosition.collectAsState()

    LaunchedEffect(true) {
        if (trackIdToPlay != Route.Player.NO_ID) {
            viewModel.playTrack(trackIdToPlay)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp)
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

            is PlaybackState.CurrentTrack -> {
                PlayerContent(viewModel, playbackState, sliderPosition)
            }
        }
    }
}


@Composable
fun PlayerContent(
    viewModel: PlayerViewModel,
    state: PlaybackState.CurrentTrack,
    positionInTrackState: State<Long>,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TrackInfo(state)
        Spacer(modifier = Modifier.weight(1f))
        SliderSection(
            positionInTrackState,
            state.track.durationMs,
            onValueChange = { viewModel.onSliderValueChange(it) },
            onValueChangeFinished = { viewModel.onSliderValueChangeFinished() })
        Controls(
            state = state,
            onClickPrevious = { viewModel.playPrevious() },
            onClickNext = { viewModel.playNext() },
            onClickPlayPause = { if (state.isPlaying()) viewModel.pause() else viewModel.resume() }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SliderSection(
    sliderPosition: State<Long>,
    trackDurationMs: Long,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit
) {
    Slider(
        value = sliderPosition.value.toFloat(),
        onValueChange = { newValue ->
            onValueChange(newValue)
        },
        onValueChangeFinished = {
            onValueChangeFinished()
        },
        valueRange = 0f..trackDurationMs.toFloat()
    )
    Row {
        Text(
            text = formatDurationTime(sliderPosition.value)
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = formatDurationTime(trackDurationMs)
        )
    }
}

private fun formatDurationTime(durationMs: Long) =
    durationMs.milliseconds.toComponents { minutes, seconds, _ ->
        String.format(
            Locale.getDefault(),
            "%d:%02d",
            minutes,
            seconds,
        )
    }

@Composable
private fun TrackInfo(state: PlaybackState.CurrentTrack) {
    val animatedPadding by animateDpAsState(
        if (state.isPlaying()) {
            0.dp
        } else {
            20.dp
        },
        label = "padding"
    )
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .padding(animatedPadding)
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
    state: PlaybackState.CurrentTrack,
    onClickPrevious: () -> Unit,
    onClickPlayPause: () -> Unit,
    onClickNext: () -> Unit
) {
    val switchButtonsSize = 50.dp
    val indication = ripple()
    Row(
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
    if (this !is PlaybackState.CurrentTrack) return false
    return currentState == PlaybackState.CurrentState.PLAYING
}