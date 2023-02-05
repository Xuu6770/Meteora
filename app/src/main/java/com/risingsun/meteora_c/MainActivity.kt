package com.risingsun.meteora_c

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.risingsun.meteora_c.data.MeteoraNavigationBarItem
import com.risingsun.meteora_c.data.NavigationScreen
import com.risingsun.meteora_c.ui.audio.AudioListScreen
import com.risingsun.meteora_c.ui.audio.AudioPlayScreen
import com.risingsun.meteora_c.ui.audio.AudioViewModel
import com.risingsun.meteora_c.ui.theme.MeteoraCTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navigationBarItemList = listOf(MeteoraNavigationBarItem(label = {
            Text(text = "音乐")
        }, icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_outline_library_music_24),
                contentDescription = "音乐"
            )
        }, selectedIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_library_music_24),
                contentDescription = "音乐"
            )
        }, route = NavigationScreen.AudioListScreen.route))

        setContent {
            MeteoraCTheme {
                val navController = rememberNavController()
                val permissionState =
                    rememberPermissionState(android.Manifest.permission.READ_MEDIA_AUDIO)
                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(key1 = lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            permissionState.launchPermissionRequest()
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)

                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    if (permissionState.status.isGranted) {
                        val audioViewModel = viewModel(AudioViewModel::class.java)
                        val audioList = audioViewModel.audioList
                        Scaffold(bottomBar = {
                            MeteoraNavigationBar(
                                navController = navController, itemList = navigationBarItemList
                            )
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
                                        totalDuration = { audio ->
                                            audio.duration.formattedToMMSS()
                                        },
                                        isAudioPlaying = audioViewModel.isAudioPlaying,
                                        onPrevious = { audioViewModel.skipToPrevious() },
                                        onPlayOrPause = { audio ->
                                            audioViewModel.playAudio(audio)
                                        },
                                        onNext = { audioViewModel.skipToNext() })
                                }
                            }
                        }
                    } else {

                    }
                }
            }
        }
    }
}

@Composable
fun MeteoraNavigationBar(navController: NavController, itemList: List<MeteoraNavigationBarItem>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    NavigationBar {
        itemList.forEach { screen ->
            NavigationBarItem(selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id)
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = if (currentDestination?.hierarchy?.any { it.route == screen.route } == true) screen.selectedIcon else screen.icon,
                label = screen.label)
        }
    }
}