package com.risingsun.meteora_c

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import java.text.SimpleDateFormat
import java.util.Locale

@HiltAndroidApp
class Meteora : Application() {
    companion object Con {
        /**
         * 这里获取的是 Application 中的 Context 而非 Activity 或 Service 中的 Context
         * 它全局只会存在一份实例，并且在整个应用程序的生命周期内都不会回收，因此并不存在内存泄漏风险。
         * 使用注解消除警告
         */
        lateinit var context: Context
        const val LOG_TAG = "Meteora"
        const val MEDIA_ROOT_ID = "Xuu6770"
        const val NOTIFICATION_ID = 47
        const val NOTIFICATION_CHANNEL_ID = "Meteora_Playback"
        const val START_PLAY = "START_PLAY"
        const val PAUSE_PLAY = "暂停播放"
        const val REFRESH_PLAY = "刷新播放"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}

/**
 * 将歌曲时长以「m:ss」格式作为字符串返回。虽然编写简单，但是不能对毫秒进行四舍五入
 */
fun Long.formattedToMMSS(): String {
    val dateFormat = SimpleDateFormat("m:ss", Locale.getDefault())
    return dateFormat.format(this)
}