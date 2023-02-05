package com.risingsun.meteora_c.ui.audio

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.risingsun.meteora_c.data.Audio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioListScreen(
    audioList: List<Audio>, onItemClick: (Audio) -> Unit, onNavigateToPlayScreen: () -> Unit
) {
    LazyColumn {
        items(audioList) {
            ListItem(headlineText = { Text(text = "${it.artist} - ${it.title}") },
                modifier = Modifier.clickable {
                    onItemClick.invoke(it)
                    onNavigateToPlayScreen.invoke()
                },
                supportingText = {
                    Text(
                        text = "${it.album} - ${it.duration}"
                    )
                },
                leadingContent = {
                    Icon(
                        bitmap = it.albumCover.asImageBitmap(),
                        contentDescription = "专辑封面",
                        modifier = Modifier.size(50.dp, 50.dp),
                        tint = Color.Unspecified
                    )
                })
        }
    }
}