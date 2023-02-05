package com.risingsun.meteora_c.ui.audio

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.risingsun.meteora_c.R.*
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
                leadingContent = {
                    Icon(
                        bitmap = it.albumCover.asImageBitmap(),
                        contentDescription = "专辑封面",
                        modifier = Modifier.size(50.dp, 50.dp),
                        tint = Color.Unspecified
                    )
                },
                trailingContent = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            painter = painterResource(id = drawable.baseline_more_vert_24),
                            contentDescription = "更多操作"
                        )
                    }
                })
            if (audioList.lastIndexOf(it) != audioList.count() - 1) Divider()
        }
    }
}