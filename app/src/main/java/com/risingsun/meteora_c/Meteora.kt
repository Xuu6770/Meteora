package com.risingsun.meteora_c

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import java.text.SimpleDateFormat
import java.util.*

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

/**
 * 将歌曲时长以「m:ss」格式作为字符串返回。虽然编写简单，但是不能对毫秒进行四舍五入
 */
fun Long.formattedToMMSS(): String {
    val dateFormat = SimpleDateFormat("m:ss", Locale.getDefault())
    return dateFormat.format(this)
}