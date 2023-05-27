package com.risingsun.meteora_c.ui.audio

import android.support.v4.media.session.MediaSessionCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.risingsun.meteora_c.R
import com.risingsun.meteora_c.data.Audio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayScreen(
    audio: Audio?,
    currentPlaybackPosition: String,
    sliderProgress: Float,
    onProgressChange: (Float) -> Unit,
    onProgressChangeFinished: () -> Unit,
    getTotalDuration: (Audio) -> String,
    isAudioPlaying: Boolean,
    skipToPrevious: () -> Unit,
    playOrPause: (Boolean) -> Unit,
    skipToNext: () -> Unit,
    onBack: () -> Unit,
    isShuffleModeOn: Boolean,
    openShuffleMode: (Boolean) -> Unit,
    playbackQueue: List<MediaSessionCompat.QueueItem>
) {
    audio?.let { currentPlayAudio ->
        Column(modifier = Modifier.fillMaxSize()) {
            // 音频信息
            Column(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    bitmap = currentPlayAudio.albumCover.asImageBitmap(),
                    contentDescription = "专辑封面",
                    modifier = Modifier
                        .weight(0.9f)
                        .fillMaxWidth()
                        .padding(20.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.FillBounds
                )
                Text(text = currentPlayAudio.title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(text = currentPlayAudio.artist, modifier = Modifier.padding(top = 10.dp))
            }

            // 播放进度
            Row(
                modifier = Modifier
                    .weight(0.2f)
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
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

            // 播放控制：随机（开关）/ 上一首 / 暂停（播放）/ 下一首 / 队列
            Row(
                modifier = Modifier
                    .weight(0.2f)
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.Center
            ) {
                if (isShuffleModeOn) {
                    IconButton(
                        onClick = { openShuffleMode.invoke(false) }, modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_shuffle_on_24),
                            contentDescription = "随机播放",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    IconButton(
                        onClick = { openShuffleMode.invoke(true) }, modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_shuffle_24),
                            contentDescription = "随机播放",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
                IconButton(
                    onClick = { skipToPrevious.invoke() }, modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_skip_previous_24),
                        contentDescription = "上一首",
                        modifier = Modifier.size(40.dp)
                    )
                }
                if (isAudioPlaying) {
                    IconButton(
                        onClick = { playOrPause.invoke(false) }, modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_pause_circle_24),
                            contentDescription = "暂停",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                } else {
                    IconButton(
                        onClick = { playOrPause.invoke(true) }, modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_play_circle_24),
                            contentDescription = "播放",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                IconButton(
                    onClick = { skipToNext.invoke() }, modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_skip_next_24),
                        contentDescription = "下一首",
                        modifier = Modifier.size(40.dp)
                    )
                }
                var openBottomSheet by rememberSaveable { mutableStateOf(false) }
                IconButton(
                    onClick = { openBottomSheet = !openBottomSheet },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_queue_music_24),
                        contentDescription = "播放队列",
                        modifier = Modifier.size(30.dp)
                    )
                }
                if (openBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { openBottomSheet = false },
                        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                    ) {
                        PlaybackQueueScreen(queue = playbackQueue)
                    }
                }
            }
        }
    }
}