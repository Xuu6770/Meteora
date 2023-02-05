package com.risingsun.meteora_c.ui.audio

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.risingsun.meteora_c.R
import com.risingsun.meteora_c.data.Audio

@Composable
fun AudioPlayScreen(
    audio: Audio?,
    progress: Float,
    onProgressChange: (Float) -> Unit,
    onProgressChangeFinished: () -> Unit,
    isAudioPlaying: Boolean,
    onPrevious: () -> Unit,
    onPlayOrPause: (Audio) -> Unit,
    onNext: () -> Unit
) {
    audio?.let { currentPlayAudio ->
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    bitmap = currentPlayAudio.albumCover.asImageBitmap(),
                    contentDescription = "专辑封面"
                )
                Text(text = currentPlayAudio.title)
                Text(text = currentPlayAudio.artist)
            }

            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "00:00", modifier = Modifier.padding(end = 5.dp))
                Slider(
                    value = progress,
                    onValueChange = onProgressChange,
                    valueRange = 0f..100f,
                    modifier = Modifier
                        .padding(start = 5.dp, end = 5.dp)
                        .weight(1f),
                    onValueChangeFinished = onProgressChangeFinished
                )
                Text(text = "00:00", modifier = Modifier.padding(start = 5.dp))
            }

            Row {
                IconButton(onClick = { onPrevious.invoke() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_skip_previous_24),
                        contentDescription = "上一首"
                    )
                }
                if (isAudioPlaying) {
                    IconButton(onClick = { onPlayOrPause.invoke(currentPlayAudio) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_pause_circle_24),
                            contentDescription = "暂停"
                        )
                    }
                } else {
                    IconButton(onClick = { onPlayOrPause.invoke(currentPlayAudio) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_play_circle_24),
                            contentDescription = "播放"
                        )
                    }
                }
                IconButton(onClick = { onNext.invoke() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_skip_next_24),
                        contentDescription = "下一首"
                    )
                }
            }
        }
    }
}