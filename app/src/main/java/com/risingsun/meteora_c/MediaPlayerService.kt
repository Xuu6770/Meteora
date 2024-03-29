package com.risingsun.meteora_c

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.risingsun.meteora_c.exoplayer.MediaPlayerNotificationManager
import com.risingsun.meteora_c.exoplayer.MediaSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MediaPlayerService : MediaBrowserServiceCompat() {

    @Inject
    lateinit var dataSourceFactory: CacheDataSource.Factory

    @Inject
    lateinit var player: ExoPlayer

    @Inject
    lateinit var mediaSource: MediaSource

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var mediaPlayerNotificationManager: MediaPlayerNotificationManager
    private var currentPlayingMedia: MediaMetadataCompat? = null
    private val isPlayerInitialized = false
    private var isForegroundService = false

    companion object {
        private const val TAG = "MediaPlayerService"
        var currentDuration = 0L
    }

    override fun onCreate() {
        super.onCreate()
        val sessionActivityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(
                this, 0, it, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        mediaSession = MediaSessionCompat(this, TAG).apply {
            setSessionActivity(sessionActivityIntent)
            isActive = true
        }
        sessionToken = mediaSession.sessionToken
        mediaPlayerNotificationManager = MediaPlayerNotificationManager(
            this, mediaSession.sessionToken, PlayerNotificationListener()
        )
        serviceScope.launch { mediaSource.load() }
        mediaSessionConnector = MediaSessionConnector(mediaSession).apply {
            setPlaybackPreparer(AudioMediaPlayerBackPreparer())
            setQueueNavigator(MediaQueueNavigator(mediaSession))
            setPlayer(player)
        }
        mediaPlayerNotificationManager.showNotification(player)
    }

    override fun onGetRoot(
        clientPackageName: String, clientUid: Int, rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot(Meteora.MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        when (parentId) {
            Meteora.MEDIA_ROOT_ID -> {
                val resultSent = mediaSource.whenReady {
                    if (it) {
                        result.sendResult(mediaSource.asMediaItem())
                    } else {
                        result.sendResult(null)
                    }
                }
                if (!resultSent) {
                    result.detach()
                }
            }

            else -> Unit
        }
    }

    override fun onCustomAction(action: String, extras: Bundle?, result: Result<Bundle>) {
        super.onCustomAction(action, extras, result)
        when (action) {
            Meteora.START_PLAY -> {
                mediaPlayerNotificationManager.showNotification(player)
            }
            Meteora.REFRESH_PLAY -> {
                mediaSource.refresh()
                notifyChildrenChanged(Meteora.MEDIA_ROOT_ID)
            }
            else -> Unit
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        player.stop()
        player.clearMediaItems()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        player.release()
    }

    inner class PlayerNotificationListener : PlayerNotificationManager.NotificationListener {
        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(Service.STOP_FOREGROUND_REMOVE)
            }
            isForegroundService = false
            stopSelf()
        }

        override fun onNotificationPosted(
            notificationId: Int, notification: Notification, ongoing: Boolean
        ) {
            if (ongoing && !isForegroundService) {
                ContextCompat.startForegroundService(
                    applicationContext,
                    Intent(applicationContext, this@MediaPlayerService.javaClass)
                )
                startForeground(notificationId, notification)
                isForegroundService = true
            }
        }
    }

    inner class AudioMediaPlayerBackPreparer : MediaSessionConnector.PlaybackPreparer {
        override fun onCommand(
            player: Player, command: String, extras: Bundle?, cb: ResultReceiver?
        ): Boolean {
            return false
        }

        override fun getSupportedPrepareActions(): Long {
            return PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
        }

        override fun onPrepare(playWhenReady: Boolean) = Unit

        override fun onPrepareFromMediaId(
            mediaId: String, playWhenReady: Boolean, extras: Bundle?
        ) {
            mediaSource.whenReady {
                val audioToPlay = mediaSource.audioMediaMetaData.find {
                    it.description.mediaId == mediaId
                }
                currentPlayingMedia = audioToPlay
                preparePlayer(
                    mediaMetadata = mediaSource.audioMediaMetaData,
                    audioToPlay = audioToPlay,
                    playWhenReady = playWhenReady
                )
            }
        }

        override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) {
            return
        }

        override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) {
            return
        }

        private fun preparePlayer(
            mediaMetadata: List<MediaMetadataCompat>,
            audioToPlay: MediaMetadataCompat?,
            playWhenReady: Boolean
        ) {
            val indexToPlay = if (currentPlayingMedia == null) 0
            else mediaMetadata.indexOf(audioToPlay)

            player.addListener(PlayerEventListener())
            player.setMediaSource(mediaSource.asMediaSource(dataSourceFactory))
            player.prepare()
            player.seekTo(indexToPlay, 0)
            player.playWhenReady = playWhenReady
        }

        private inner class PlayerEventListener : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING, Player.STATE_READY -> {
                        mediaPlayerNotificationManager.showNotification(player)
                    }
                    else -> {
                        mediaPlayerNotificationManager.hideNotification()
                    }
                }
            }

            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)
                currentDuration = player.duration
            }

            override fun onPlayerError(error: PlaybackException) {
                if (error.errorCode == PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND) {

                }
            }
        }
    }

    inner class MediaQueueNavigator(mediaSessionCompat: MediaSessionCompat) :
        TimelineQueueNavigator(mediaSessionCompat) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            if (windowIndex < mediaSource.audioMediaMetaData.size) {
                return mediaSource.audioMediaMetaData[windowIndex].description
            }
            return MediaDescriptionCompat.Builder().build()
        }
    }
}