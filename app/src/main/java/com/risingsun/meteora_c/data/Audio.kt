package com.risingsun.meteora_c.data

import android.graphics.Bitmap
import android.net.Uri

data class Audio(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumUri: Uri,
    val albumCover: Bitmap,
    val playUri: Uri,
    val duration: Long
)
