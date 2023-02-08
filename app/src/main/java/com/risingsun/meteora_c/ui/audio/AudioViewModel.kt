package com.risingsun.meteora_c.ui.audio

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.risingsun.meteora_c.AudioRepository
import com.risingsun.meteora_c.MediaPlayerService
import com.risingsun.meteora_c.Meteora
import com.risingsun.meteora_c.data.Audio
import com.risingsun.meteora_c.exoplayer.PlayerServiceConnection
import com.risingsun.meteora_c.exoplayer.currentPosition
import com.risingsun.meteora_c.exoplayer.isPlaying
import com.risingsun.meteora_c.formattedToMMSS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(
    private val repository: AudioRepository, connection: PlayerServiceConnection
) : ViewModel() {
    var audioList = mutableStateListOf<Audio>()
    var newPosition = 0f
    val currentPlaying = connection.currentPlayingAudio
    private val isConnected = connection.isConnected
    private lateinit var rootMediaId: String
    private var currentPlaybackPosition by mutableStateOf(0L)
    val playbackPositionFormat: String
        get() = currentPlaybackPosition.formattedToMMSS()
    private var updatePosition = true
    private val playbackState = connection.playBackState
    val isAudioPlaying: Boolean
        get() = playbackState.value?.isPlaying == true
    val isShuffleModeOn: Boolean
        get() = serviceConnection.mediaControllerCompat.shuffleMode != PlaybackStateCompat.SHUFFLE_MODE_NONE

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>
        ) {
            super.onChildrenLoaded(parentId, children)
        }
    }

    private val serviceConnection = connection.also {
        updatePlayback()
    }
    private val currentDuration: Long
        get() = MediaPlayerService.currentDuration
    val currentAudioProgress = mutableStateOf(0f)

    init {
        viewModelScope.launch {
            audioList += getAndFormatAudioData()
            isConnected.collect {
                if (it) {
                    rootMediaId = serviceConnection.rootMediaId
                    serviceConnection.playBackState.value?.apply {
                        currentPlaybackPosition = position
                    }
                    serviceConnection.subscribe(rootMediaId, subscriptionCallback)
                }
            }
        }
    }

    private suspend fun getAndFormatAudioData() = repository.getAudios()

    fun playAudio(audio: Audio) {
        serviceConnection.playAudio(audioList)
        if (audio.id == currentPlaying.value?.id) {
            if (isAudioPlaying) serviceConnection.transportControls.pause()
            else serviceConnection.transportControls.play()
        } else {
            serviceConnection.transportControls.playFromMediaId(audio.id.toString(), null)
        }
    }

    fun stopPlayback() {
        serviceConnection.transportControls.stop()
    }

    fun fastForward() {
        serviceConnection.transportControls.fastForward()
    }

    fun rewind() {
        serviceConnection.transportControls.rewind()
    }

    fun skipToNext() {
        serviceConnection.transportControls.skipToNext()
    }

    fun skipToPrevious() {
        serviceConnection.transportControls.skipToPrevious()
    }

    fun seekTo(value: Float) {
        serviceConnection.transportControls.seekTo((currentDuration * value / 100f).toLong())
    }

    fun setShuffleMode(mode: Boolean) {
        if (mode) serviceConnection.transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL)
        else serviceConnection.transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE)
    }

    private fun updatePlayback() {
        viewModelScope.launch {
            val position = playbackState.value?.currentPosition ?: 0
            if (currentPlaybackPosition != position) currentPlaybackPosition = position
            if (currentDuration > 0) {
                currentAudioProgress.value =
                    (currentPlaybackPosition.toFloat() / currentDuration.toFloat() * 100f)
            }
            delay(1000L)
            if (updatePosition) updatePlayback()
        }
    }

    override fun onCleared() {
        super.onCleared()
        serviceConnection.unSubscribe(
            Meteora.Con.MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {})
        updatePosition = false
    }
}