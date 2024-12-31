package solid.javad.soundplayer.ui.screen

import solid.javad.soundplayer.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import solid.javad.soundplayer.ui.state.AudiosScreenState
import solid.javad.soundplayer.ui.theme.Typography
import solid.javad.soundplayer.ui.viewmodel.AudiosScreenViewModel
import kotlin.math.roundToInt

@Composable
fun AudiosScreen(modifier: Modifier = Modifier, viewModel: AudiosScreenViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    when(state) {
        is AudiosScreenState.Error -> {
            val errorState = state as AudiosScreenState.Error

            Box(modifier = modifier, contentAlignment = Alignment.Center) {
                Card(modifier = Modifier.fillMaxWidth(2/3f)) {
                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 100.dp)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon (
                            modifier = Modifier.size(48.dp),
                            imageVector = Icons.Rounded.Warning,
                            contentDescription = "Error"
                        )

                        Text (
                            text = errorState.message,
                            style = Typography.titleMedium
                        )
                    }
                }
            }
        }

        is AudiosScreenState.Loading -> {
            Column (
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Text(text = "Loading", style = Typography.titleLarge)
            }
        }

        is AudiosScreenState.Successful -> {
            val successfulState = state as AudiosScreenState.Successful

            if (successfulState.permissionIsGranted)
                LazyColumn (
                    modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    itemsIndexed(successfulState.audios) {index, audio ->
                        val albumArt = if (audio.albumArt == null) painterResource(R.drawable.audio) else BitmapPainter(audio.albumArt.asImageBitmap())

                        Audio (
                            modifier = Modifier.fillMaxWidth(),
                            playAudio = { viewModel.playAudio(index) },
                            pauseAudio = { viewModel.pauseAudio() },
                            seekTo = { viewModel.seekTo(it) },
                            albumArt = albumArt,
                            name = audio.name,
                            albumArtist = audio.albumArtist,
                            isPlaying = successfulState.playerState.currentAudio == index,
                            duration = successfulState.playerState.duration,
                            currentSecond = successfulState.playerState.currentSecond
                        )

                        if (index != successfulState.audios.size-1)
                            Box (
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color.LightGray)
                            )
                    }
                }
            else {
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text (
                        text = "Please allow access to your audios",
                        style = Typography.titleMedium
                    )

                    val launcher = viewModel.getLauncher()

                    OutlinedButton(onClick = { viewModel.requestPermission(launcher) }) {
                        Text(text = "Request permission")
                    }
                }
            }
        }
    }
}

@Composable
private fun Audio(
    modifier: Modifier = Modifier,
    pauseAudio: () -> Unit,
    playAudio: () -> Unit,
    seekTo: (second: Int) -> Unit,
    albumArt: Painter,
    name: String,
    albumArtist: String,
    isPlaying: Boolean,
    duration: Int?,
    currentSecond: Int?
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.Center) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.size(48.dp)
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = albumArt,
                    contentDescription = "Album Art"
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text(
                    text = name,
                    style = Typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = albumArtist,
                    style = Typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier
                    .defaultMinSize(minWidth = 36.dp)
                    .wrapContentHeight()
                    .weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                IconButton(
                    onClick = {
                        if (isPlaying) {
                            pauseAudio()
                        } else {
                            playAudio()
                        }
                    }
                ) {
                    if (isPlaying)
                        Icon(
                            painter = painterResource(R.drawable.pause),
                            contentDescription = "Pause Audio"
                        )
                    else
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = "Play Audio"
                        )
                }
            }
        }

        if (isPlaying && currentSecond != null && duration != null) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Slider(
                    modifier = Modifier.weight(1f),
                    value = currentSecond.toFloat() / duration,
                    onValueChange = {
                        seekTo((it * duration).roundToInt())
                    }
                )

                Text(text = currentSecond.secondsToTimeString(), style = Typography.bodySmall)
            }
        }
    }
}

private fun Int.secondsToTimeString(): String {
    val hours = this / 3600
    val minutes = (this - hours * 3600) / 60
    val seconds = this - (hours * 3600) - (minutes * 60)

    val secondsString = if (seconds > 9) seconds.toString() else "0$seconds"
    val minutesString = if (minutes > 9) minutes.toString() else "0$minutes"

    return if (hours == 0) "$minutesString:$secondsString"
        else "$hours:$minutesString:$secondsString"
}
