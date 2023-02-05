package com.risingsun.meteora_c

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Meteora : Application() {
    object Con {
        const val MEDIA_ROOT_ID = "Xuu6770"
        const val NOTIFICATION_ID = 47
        const val NOTIFICATION_CHANNEL_ID = "Meteora_Playback"
        const val START_PLAY = "START_PLAY"
        const val PAUSE_PLAY = "暂停播放"
        const val REFRESH_PLAY = "刷新播放"
    }
}