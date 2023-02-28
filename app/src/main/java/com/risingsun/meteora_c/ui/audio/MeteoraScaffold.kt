package com.risingsun.meteora_c.ui.audio

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.risingsun.meteora_c.R
import com.risingsun.meteora_c.data.Audio
import com.risingsun.meteora_c.data.MeteoraNavigationBarItem
import com.risingsun.meteora_c.data.NavigationScreen
import com.risingsun.meteora_c.formattedToMMSS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeteoraScaffold(
    navController: NavHostController,
    navigationBarItemList: List<MeteoraNavigationBarItem>,
    playbackWithShuffleMode: () -> Unit,
    audioList: SnapshotStateList<Audio>,
    audioViewModel: AudioViewModel
) {
    Scaffold(bottomBar = {
        MeteoraNavigationBar(
            navController = navController, itemList = navigationBarItemList
        )
    }, floatingActionButton = {
        // TODO: 在脚手架中设置的浮动按钮会出现在除了音乐列表界面以外的界面，这是不合理的
        FloatingActionButton(onClick = { playbackWithShuffleMode.invoke() }) {
            Icon(
                painter = painterResource(id = R.drawable.round_shuffle_24),
                contentDescription = "随机播放"
            )
        }
    }) {
        NavHost(
            navController = navController,
            startDestination = NavigationScreen.AudioListScreen.route,
            modifier = Modifier.padding(it)
        ) {
            composable(
                route = NavigationScreen.AudioListScreen.route
            ) {
                AudioListScreen(audioList = audioList, onItemClick = { audio ->
                    audioViewModel.playAudio(audio)
                }, onNavigateToPlayScreen = {
                    navController.navigate(NavigationScreen.AudioPlayScreen.route)
                })
            }
            composable(route = NavigationScreen.AudioPlayScreen.route) {
                AudioPlayScreen(audio = audioViewModel.currentPlaying.value,
                    currentPlaybackPosition = audioViewModel.playbackPositionFormat,
                    sliderProgress = audioViewModel.currentAudioProgress.value,
                    onProgressChange = { progress ->
                        audioViewModel.currentAudioProgress.value = progress
                        audioViewModel.newPosition = progress
                    },
                    onProgressChangeFinished = {
                        audioViewModel.seekTo(audioViewModel.newPosition)
                    },
                    getTotalDuration = { audio ->
                        audio.duration.formattedToMMSS()
                    },
                    isAudioPlaying = audioViewModel.isAudioPlaying,
                    skipToPrevious = { audioViewModel.skipToPrevious() },
                    playOrPause = { play ->
                        audioViewModel.playOrPause(play = play)
                    },
                    skipToNext = { audioViewModel.skipToNext() },
                    onBack = { navController.popBackStack() },
                    isShuffleModeOn = audioViewModel.isShuffleModeOn,
                    openShuffleMode = { set ->
                        audioViewModel.setShuffleMode(set)
                    },
                    navigateToQueueScreen = {
                        navController.navigate(NavigationScreen.PlaybackQueueScreen.route)
                    })
            }
            composable(route = NavigationScreen.PlaybackQueueScreen.route) {
                PlaybackQueueScreen(audioViewModel.playbackQueue!!)
            }
        }
    }
}