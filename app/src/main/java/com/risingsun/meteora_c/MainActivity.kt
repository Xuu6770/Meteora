package com.risingsun.meteora_c

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.risingsun.meteora_c.data.MeteoraNavigationBarItem
import com.risingsun.meteora_c.data.NavigationScreen
import com.risingsun.meteora_c.ui.audio.AudioViewModel
import com.risingsun.meteora_c.ui.audio.MeteoraScaffold
import com.risingsun.meteora_c.ui.theme.MeteoraCTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
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
                        MeteoraScaffold(
                            navController = navController,
                            navigationBarItemList = navigationBarItemList,
                            playbackWithShuffleMode = {
                                audioViewModel.setShuffleMode(true)
                                val audio = audioList.random()
                                audioViewModel.playAudio(audio = audio)
                                navController.navigate(NavigationScreen.AudioPlayScreen.route)

                                // TODO: 从音乐列表点击随机播放进入时应将当前播放的音乐置于播放队列之首
                                audioViewModel.playbackQueue?.let {
                                    val temp = it.find { item ->
                                        item.description.mediaId == audio.id.toString()
                                    }
                                    Collections.swap(it, it.indexOf(temp), 0)
                                }
                            },
                            audioList = audioList,
                            audioViewModel = audioViewModel
                        )
                    } else {

                    }
                }
            }
        }
    }
}