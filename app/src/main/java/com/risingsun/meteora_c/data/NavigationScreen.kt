package com.risingsun.meteora_c.data

sealed class NavigationScreen(val route: String) {
    object AudioListScreen : NavigationScreen("audio_list_screen")
    object AudioPlayScreen : NavigationScreen("audio_play_screen")
    object PlaybackQueueScreen : NavigationScreen("playback_queue_screen")
}
