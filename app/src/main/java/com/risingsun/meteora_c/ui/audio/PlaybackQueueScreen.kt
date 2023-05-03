package com.risingsun.meteora_c.ui.audio

import android.support.v4.media.session.MediaSessionCompat.QueueItem
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaybackQueueScreen(queue: List<QueueItem>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(queue) {
            ListItem(headlineContent = { Text(text = "${it.description.title}") })
        }
    }
}