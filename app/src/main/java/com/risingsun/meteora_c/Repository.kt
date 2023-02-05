package com.risingsun.meteora_c

import com.risingsun.meteora_c.data.Audio
import com.risingsun.meteora_c.data.ContentResolverHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AudioRepository @Inject constructor(private val contentResolverHelper: ContentResolverHelper) {
    suspend fun getAudios(): List<Audio> = withContext(Dispatchers.IO) {
        contentResolverHelper.queryAudios()
    }
}