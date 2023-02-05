package com.risingsun.meteora_c.exoplayer

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.risingsun.meteora_c.AudioRepository
import javax.inject.Inject

class MediaSource @Inject constructor(private val repository: AudioRepository) {
    private val onReadyListeners: MutableList<OnReadyListener> = mutableListOf()
    var audioMediaMetaData: List<MediaMetadataCompat> = emptyList()
    private var state: AudioSourceState = AudioSourceState.STATE_CREATED
        set(value) {
            if (value == AudioSourceState.STATE_CREATED || value == AudioSourceState.STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach {
                        it.invoke(isReady)
                    }
                }
            } else {
                field = value
            }
        }

    fun whenReady(listener: OnReadyListener) =
        if (state == AudioSourceState.STATE_CREATED || state == AudioSourceState.STATE_INITIALIZING) {
            onReadyListeners += listener
            false
        } else {
            listener.invoke(isReady)
            true
        }

    suspend fun load() {
        state = AudioSourceState.STATE_INITIALIZING
        val data = repository.getAudios()
        audioMediaMetaData = data.map {
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, it.id.toString())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, it.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, it.title)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, it.playUri.toString())
                .build()
        }
        state = AudioSourceState.STATE_INITIALIZED
    }

    fun asMediaSource(dataSource: CacheDataSource.Factory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        audioMediaMetaData.forEach {
            val mediaItem =
                com.google.android.exoplayer2.MediaItem.fromUri(it.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI))
            val mediaSource =
                ProgressiveMediaSource.Factory(dataSource).createMediaSource(mediaItem)
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    fun asMediaItem() = audioMediaMetaData.map {
        val description = MediaDescriptionCompat.Builder().setTitle(it.description.title)
            .setMediaId(it.description.mediaId).setSubtitle(it.description.subtitle)
            .setMediaUri(it.description.mediaUri).build()
        MediaBrowserCompat.MediaItem(description, FLAG_PLAYABLE)
    }.toMutableList()

    fun refresh() {
        onReadyListeners.clear()
        state = AudioSourceState.STATE_CREATED
    }

    private val isReady: Boolean
        get() = state == AudioSourceState.STATE_INITIALIZED
}

enum class AudioSourceState {
    STATE_CREATED, STATE_INITIALIZING, STATE_INITIALIZED, STATE_ERROR
}

typealias OnReadyListener = (Boolean) -> Unit