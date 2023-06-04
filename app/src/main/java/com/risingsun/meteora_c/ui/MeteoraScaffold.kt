package com.risingsun.meteora_c.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.risingsun.meteora_c.R
import com.risingsun.meteora_c.data.Audio
import com.risingsun.meteora_c.data.MeteoraNavigationBarItem
import com.risingsun.meteora_c.data.NavigationScreen
import com.risingsun.meteora_c.formattedToMMSS
import com.risingsun.meteora_c.ui.audio.AudioListScreen
import com.risingsun.meteora_c.ui.audio.AudioPlayScreen
import com.risingsun.meteora_c.ui.audio.AudioViewModel

@Composable
fun MeteoraScaffold(
    navController: NavHostController,
    navigationBarItemList: List<MeteoraNavigationBarItem>,
    playbackWithShuffleMode: () -> Unit,
    audioList: SnapshotStateList<Audio>,
    audioViewModel: AudioViewModel
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    Scaffold(
        bottomBar = {
            if (currentRoute != NavigationScreen.AudioPlayScreen.route) {
                MeteoraNavigationBar(
                    navController = navController,
                    itemList = navigationBarItemList
                )
            }
        }, floatingActionButton = {
            if (currentRoute == NavigationScreen.AudioListScreen.route) {
                FloatingActionButton(onClick = { playbackWithShuffleMode.invoke() }) {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.round_shuffle_24
                        ), contentDescription = "随机播放"
                    )
                }
            }
        }) {
        NavHost(
            navController = navController,
            startDestination = NavigationScreen.AudioListScreen.route,
            modifier = Modifier.padding(it)
        ) {
            composable(route = NavigationScreen.AudioListScreen.route) {
                AudioListScreen(audioList = audioList, onItemClick = { audio ->
                    audioViewModel.playAudio(audio)
                }, onNavigateToPlayScreen = {
                    navController.navigate(NavigationScreen.AudioPlayScreen.route)
                })
            }
            composable(route = NavigationScreen.AudioPlayScreen.route) {
                AudioPlayScreen(
                    audio = audioViewModel.currentPlaying.value,
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
                    playOrPause = { audioViewModel.playOrPause() },
                    skipToNext = { audioViewModel.skipToNext() },
                    onBack = { navController.popBackStack() },
                    isShuffleModeOn = audioViewModel.isShuffleModeOn,
                    openShuffleMode = { set ->
                        audioViewModel.setShuffleMode(set)
                    },
                    viewModel = audioViewModel
                )
            }
        }
    }
}