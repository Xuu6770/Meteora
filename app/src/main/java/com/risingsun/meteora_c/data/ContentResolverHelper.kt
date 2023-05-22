package com.risingsun.meteora_c.data

import android.content.ContentUris
import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.WorkerThread
import com.risingsun.meteora_c.Meteora
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ContentResolverHelper @Inject constructor(@ApplicationContext val context: Context) {
    private val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.DURATION
    )

    @WorkerThread
    fun queryAudios(): MutableList<Audio> {
        val audios = mutableListOf<Audio>()
        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null
        )?.use { cursor ->
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            Log.i(Meteora.LOG_TAG, "找到了 ${cursor.count} 首歌曲")

            while (cursor.moveToNext()) {
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val album = cursor.getString(albumColumn)
                val id = cursor.getLong(idColumn)
                val duration = cursor.getLong(durationColumn)
                val playUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                val retriever = MediaMetadataRetriever().apply {
                    setDataSource(context, playUri)
                }
                val coverBytes = retriever.embeddedPicture
                val audio = Audio(
                    id = id,
                    title = title,
                    artist = artist,
                    album = album,
                    albumCover = BitmapFactory.decodeByteArray(coverBytes, 0, coverBytes!!.size),
                    playUri = playUri,
                    duration = duration
                )
                audios += audio
            }
        }
        return audios
    }
}