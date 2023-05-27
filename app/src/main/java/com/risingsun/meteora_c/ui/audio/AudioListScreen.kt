package com.risingsun.meteora_c.ui.audio

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.risingsun.meteora_c.R.drawable
import com.risingsun.meteora_c.data.Audio

@Composable
fun AudioListScreen(
    audioList: List<Audio>,
    onItemClick: (Audio) -> Unit,
    onNavigateToPlayScreen: () -> Unit
) {
    LazyColumn {
        items(audioList) {
            ListItem(
                headlineContent = { Text(text = it.title, maxLines = 1) },
                modifier = Modifier.clickable {
                    onItemClick(it)
                    onNavigateToPlayScreen()
                },
                supportingContent = { Text(text = it.artist, maxLines = 1) },
                leadingContent = {
                    Image(
                        bitmap = it.albumCover.asImageBitmap(),
                        contentDescription = "专辑封面",
                        modifier = Modifier.size(50.dp).clip(RoundedCornerShape(10.dp))
                    )
                },
                trailingContent = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            painter = painterResource(id = drawable.round_more_vert_24),
                            contentDescription = "更多操作"
                        )
                    }
                })
            if (audioList.lastIndexOf(it) != audioList.count() - 1) Divider()
        }
    }
}