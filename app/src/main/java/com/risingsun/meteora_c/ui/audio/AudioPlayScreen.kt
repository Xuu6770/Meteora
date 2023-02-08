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
import androidx.compose.ui.text.style.TextAlign
import com.risingsun.meteora_c.R
import com.risingsun.meteora_c.data.Audio

@Composable
fun AudioPlayScreen(
    audio: Audio?,
    currentPlaybackPosition: String,
    sliderProgress: Float,
    onProgressChange: (Float) -> Unit,
    onProgressChangeFinished: () -> Unit,
    getTotalDuration: (Audio) -> String,
    isAudioPlaying: Boolean,
    onSkipToPrevious: () -> Unit,
    playOrPause: (Audio) -> Unit,
    onSkipToNext: () -> Unit,
    onBack: () -> Unit,
    isShuffleModeOn: Boolean,
    openShuffleMode: (Boolean) -> Unit
) {
    audio?.let { currentPlayAudio ->
        Column(modifier = Modifier.fillMaxSize()) {
            // 音频信息
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

            // 播放进度
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentPlaybackPosition,
                    modifier = Modifier.weight(0.15f),
                    textAlign = TextAlign.Center
                )
                Slider(
                    value = sliderProgress,
                    onValueChange = onProgressChange,
                    valueRange = 0f..100f,
                    modifier = Modifier.weight(0.7f),
                    onValueChangeFinished = onProgressChangeFinished
                )
                Text(
                    text = getTotalDuration.invoke(audio),
                    modifier = Modifier.weight(0.15f),
                    textAlign = TextAlign.Center
                )
            }

            // 播放控制
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                IconButton(onClick = { onSkipToPrevious.invoke() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_skip_previous_24),
                        contentDescription = "上一首"
                    )
                }
                if (isAudioPlaying) {
                    IconButton(onClick = { playOrPause.invoke(currentPlayAudio) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_pause_circle_24),
                            contentDescription = "暂停"
                        )
                    }
                } else {
                    IconButton(onClick = { playOrPause.invoke(currentPlayAudio) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_play_circle_24),
                            contentDescription = "播放"
                        )
                    }
                }
                IconButton(onClick = { onSkipToNext.invoke() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_skip_next_24),
                        contentDescription = "下一首"
                    )
                }
            }

            // 其它操作
            Row {
                IconButton(onClick = { onBack.invoke() }, modifier = Modifier.weight(1f)) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_arrow_back_24),
                        contentDescription = "返回"
                    )
                }
                if (isShuffleModeOn) {
                    IconButton(
                        onClick = { openShuffleMode.invoke(false) }, modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_shuffle_on_24),
                            contentDescription = "随机播放"
                        )
                    }
                } else {
                    IconButton(
                        onClick = { openShuffleMode.invoke(true) }, modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_shuffle_24),
                            contentDescription = "随机播放"
                        )
                    }
                }
                IconButton(onClick = { /*TODO*/ }, modifier = Modifier.weight(1f)) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_playlist_play_24),
                        contentDescription = "播放列表"
                    )
                }
            }
        }
    }
}