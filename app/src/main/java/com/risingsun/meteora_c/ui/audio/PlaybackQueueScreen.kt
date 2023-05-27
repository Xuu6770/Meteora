package com.risingsun.meteora_c.ui.audio

import android.support.v4.media.session.MediaSessionCompat.QueueItem
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun PlaybackQueueScreen(queue: List<QueueItem>) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(text = "播放队列", fontSize = 24.sp)
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(queue) {
            ListItem(
                headlineContent = { Text(text = "${it.description.title}") },
                modifier = Modifier.clickable { })
        }
    }
}