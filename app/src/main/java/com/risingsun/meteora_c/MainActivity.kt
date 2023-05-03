package com.risingsun.meteora_c

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.risingsun.meteora_c.data.MeteoraNavigationBarItem
import com.risingsun.meteora_c.data.NavigationScreen
import com.risingsun.meteora_c.ui.PermissionRequestScreen
import com.risingsun.meteora_c.ui.audio.AudioViewModel
import com.risingsun.meteora_c.ui.audio.MeteoraScaffold
import com.risingsun.meteora_c.ui.theme.MeteoraCTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @ExperimentalPermissionsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 底部导航栏项目
        val navigationBarItemList = listOf(
            MeteoraNavigationBarItem(
                label = { Text(text = "音乐") },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_outline_library_music_24),
                        contentDescription = "音乐"
                    )
                },
                selectedIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_library_music_24),
                        contentDescription = "音乐"
                    )
                },
                route = NavigationScreen.AudioListScreen.route
            )
        )

        // 声明需要申请的权限
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) permissions.add(android.Manifest.permission.FOREGROUND_SERVICE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) permissions.add(android.Manifest.permission.READ_MEDIA_AUDIO)
        else permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)

        setContent {
            MeteoraCTheme {
                val navController = rememberNavController()
                val permissionsState = rememberMultiplePermissionsState(permissions = permissions)

                /*
                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(key1 = lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            permissionState.launchMultiplePermissionRequest()
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)

                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }
                */

                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    if (permissionsState.allPermissionsGranted) {
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
                        PermissionRequestScreen(permissionsState = permissionsState)
                    }
                }
            }
        }
    }
}