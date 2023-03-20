package projekt.cloud.piece.music.player.service.playback

import android.app.Notification
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ART
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ARTIST
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PAUSE
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_PAUSE
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SEEK_TO
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_NEXT
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM
import android.support.v4.media.session.PlaybackStateCompat.ACTION_STOP
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_NONE
import android.support.v4.media.session.PlaybackStateCompat.RepeatMode
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_NONE
import android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import android.support.v4.media.session.PlaybackStateCompat.ShuffleMode
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.media.session.MediaButtonReceiver
import com.bumptech.glide.Glide
import kotlinx.coroutines.withContext
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseLifecycleMediaBrowserService
import projekt.cloud.piece.music.player.storage.runtime.RuntimeDatabase.RuntimeDatabaseUtil.runtimeDatabase
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity
import projekt.cloud.piece.music.player.util.CoroutineUtil.default
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.CoroutineUtil.main
import projekt.cloud.piece.music.player.util.ServiceUtil.startSelf
import projekt.cloud.piece.music.player.util.ServiceUtil.startSelfForeground
import projekt.cloud.piece.music.player.util.UriUtil.albumArtUri
import projekt.cloud.piece.music.player.util.UriUtil.audioUri

class PlaybackService: BaseLifecycleMediaBrowserService() {

    private companion object PlaybackServiceConst {
        const val TAG = "PlaybackService"

        const val PLAYBACK_SPEED = 1F
        const val PLAYBACK_ACTION =
            ACTION_PLAY or ACTION_PAUSE or ACTION_PLAY_PAUSE or ACTION_STOP or
                    ACTION_SEEK_TO or
                    ACTION_PLAY_FROM_MEDIA_ID or
                    ACTION_SKIP_TO_NEXT or ACTION_SKIP_TO_PREVIOUS or ACTION_SKIP_TO_QUEUE_ITEM

        const val START_COMMAND_PLAYBACK = "${TAG}.Playback"
        const val START_COMMAND_PLAYBACK_START = "$START_COMMAND_PLAYBACK.Start"
        const val START_COMMAND_PLAYBACK_PAUSE = "$START_COMMAND_PLAYBACK.Pause"

        const val NOTIFICATION_ID = 1552980358

        const val DURATION_START = 0L
    }

    private var _mediaSessionCompat: MediaSessionCompat? = null
    private val mediaSessionCompat: MediaSessionCompat
        get() = _mediaSessionCompat!!

    private var playbackStateCompat = PlaybackStateCompat.Builder()
        .setActions(PLAYBACK_ACTION)
        .setState(STATE_NONE, DURATION_START, PLAYBACK_SPEED)
        .build()

    @ShuffleMode
    @Volatile
    private var shuffleMode = SHUFFLE_MODE_NONE
    @RepeatMode
    @Volatile
    private var repeatMode = REPEAT_MODE_NONE

    private lateinit var playbackNotificationHelper: PlaybackNotificationHelper

    private var _audioMetadata: AudioMetadataEntity? = null
    private val audioMetadata: AudioMetadataEntity
        get() = _audioMetadata!!
    private var order = -1

    private val audioPlayer by lazy { AudioPlayer(this) }

    override fun onCreate() {
        super.onCreate()

        // Setup player
        audioPlayer.setupPlayer(this@PlaybackService)

        _mediaSessionCompat = MediaSessionCompat(this, TAG)
        with(mediaSessionCompat) {
            val runtimeDatabase = runtimeDatabase

            setCallback(
                object: MediaSessionCompat.Callback() {
                    override fun onPlay() {
                        Log.d(TAG, "onPlay: audioMetadata=$audioMetadata")
                        if (playbackStateCompat.playbackState != STATE_PAUSED
                            && playbackStateCompat.playbackState != STATE_BUFFERING) {
                            return
                        }

                        lifecycleScope.main {
                            if (!audioPlayer.isPlaying()) {
                                audioPlayer.play()
                            }

                            // Update state
                            playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                                .setState(STATE_PLAYING, audioPlayer.currentPosition(), PLAYBACK_SPEED)
                                .build()
                            setPlaybackState(playbackStateCompat)

                            startSelfForeground(bundleOf(START_COMMAND_PLAYBACK to START_COMMAND_PLAYBACK_START))
                        }
                    }

                    override fun onPause() {
                        Log.d(TAG, "onPause: audioMetadata=$audioMetadata")
                        if (playbackStateCompat.playbackState == STATE_PLAYING) {
                            // Pause player

                            lifecycleScope.main {
                                if (audioPlayer.isPlaying()) {
                                    audioPlayer.pause()
                                }

                                playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                                    .setState(STATE_PAUSED, audioPlayer.currentPosition(), PLAYBACK_SPEED)
                                    .build()
                                setPlaybackState(playbackStateCompat)
                            }

                            startSelf(bundleOf(START_COMMAND_PLAYBACK to START_COMMAND_PLAYBACK_PAUSE))
                        }
                    }

                    override fun onSkipToPrevious() {
                        super.onSkipToPrevious()
                    }

                    override fun onSkipToNext() {
                        super.onSkipToNext()
                    }

                    override fun onPlayFromMediaId(mediaId: String, extras: Bundle?) {
                        lifecycleScope.main {
                            // Get data
                            order = withContext(default) {
                                runtimeDatabase.playbackDao()
                                    .queryOrder(mediaId)
                            }

                            val audioMetadata = withContext(default) {
                                runtimeDatabase.audioMetadataDao()
                                    .query(mediaId)
                            }
                            _audioMetadata = audioMetadata

                            Log.d(TAG, "onPlayFromMediaId: order=$order audioMetadata=$audioMetadata")

                            // Update state
                            playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                                .setState(STATE_BUFFERING, DURATION_START, PLAYBACK_SPEED)
                                .build()
                            setPlaybackState(playbackStateCompat)
                            setMetadata(
                                MediaMetadataCompat.Builder()
                                    .putString(METADATA_KEY_TITLE, audioMetadata.title)
                                    .putString(METADATA_KEY_ARTIST, audioMetadata.artistName)
                                    .putString(METADATA_KEY_ALBUM, audioMetadata.albumTitle)
                                    .putLong(METADATA_KEY_DURATION, audioMetadata.duration)
                                    .build()
                            )

                            // Prepare
                            audioPlayer.prepareUri(audioMetadata.id.audioUri)

                            // Start play
                            onPlay()
                        }
                    }

                    override fun onSkipToQueueItem(id: Long) {
                        super.onSkipToQueueItem(id)
                    }

                    override fun onSeekTo(pos: Long) {
                        if (pos >= DURATION_START) {
                            lifecycleScope.main {
                                // Update player pos
                                audioPlayer.seekTo(pos)

                                // Update state
                                playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                                    .setState(playbackStateCompat.state, pos, PLAYBACK_SPEED)
                                    .build()
                                setPlaybackState(playbackStateCompat)
                            }
                        }
                    }
                }
            )

            setPlaybackState(playbackStateCompat)
            setMetadata(
                MediaMetadataCompat.Builder()
                    .build()
            )

            setShuffleMode(shuffleMode)
            setRepeatMode(repeatMode)

            isActive = true
        }
        sessionToken = mediaSessionCompat.sessionToken

        playbackNotificationHelper = PlaybackNotificationHelper(this)
    }

    private var isForeground = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getStringExtra(START_COMMAND_PLAYBACK)) {
            START_COMMAND_PLAYBACK_START -> {
                Log.d(TAG, "onStartCommand: START_COMMAND_PLAYBACK_START")

                val audioMetadata = audioMetadata

                // Update foreground first
                startForegroundIfRequired(createNotification(audioMetadata, null))

                // Update image
                lifecycleScope.main {
                    val largeIconSize = resources.getDimensionPixelSize(
                        R.dimen.notification_large_image_size_64
                    )
                    val bitmap = requestAlbumCover(
                        audioMetadata.album.albumArtUri, largeIconSize
                    ) ?: requestDefaultCover(largeIconSize)

                    playbackNotificationHelper.notifyNotification(
                        this@PlaybackService,
                        createNotification(audioMetadata, bitmap),
                        NOTIFICATION_ID
                    )

                    mediaSessionCompat.setMetadata(
                        MediaMetadataCompat.Builder()
                            .putString(METADATA_KEY_TITLE, audioMetadata.title)
                            .putString(METADATA_KEY_ARTIST, audioMetadata.artistName)
                            .putString(METADATA_KEY_ALBUM, audioMetadata.albumTitle)
                            .putLong(METADATA_KEY_DURATION, audioMetadata.duration)
                            .putBitmap(METADATA_KEY_ART, bitmap)
                            .build()
                    )
                }
            }
            START_COMMAND_PLAYBACK_PAUSE -> {
                Log.d(TAG, "onStartCommand: START_COMMAND_PLAYBACK_PAUSE")
                if (isForeground) {
                    // stopForeground(Boolean) deprecated at SDK33
                    // https://developer.android.com/reference/android/app/Service#stopForeground(boolean)
                    when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                            stopForeground(STOP_FOREGROUND_DETACH)
                        }
                        else -> {
                            @Suppress("DEPRECATION")
                            stopForeground(false)
                        }
                    }
                    isForeground = false
                }
                stopSelf()
            }
            else -> {
                Log.d(TAG, "onStartCommand: MediaButtonReceiver.handleIntent")
                MediaButtonReceiver.handleIntent(mediaSessionCompat, intent)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundIfRequired(notification: Notification) {
        when {
            isForeground -> {
                playbackNotificationHelper.notifyNotification(
                    this, notification, NOTIFICATION_ID
                )
            }
            else -> {
                startForeground(NOTIFICATION_ID, notification)
                isForeground = true
            }
        }
    }

    private fun createNotification(
        audioMetadata: AudioMetadataEntity, bitmap: Bitmap?
    ) = playbackNotificationHelper.createNotification(
        this,
        audioMetadata.title,
        audioMetadata.artistName,
        audioMetadata.albumTitle,
        bitmap
    )

    private suspend fun requestAlbumCover(uri: Uri, imageSize: Int): Bitmap? {
        return withContext(io) {
            @Suppress("BlockingMethodInNonBlockingContext")
            Glide.with(this@PlaybackService)
                .asBitmap()
                .load(uri)
                .submit(imageSize, imageSize)
                .get()
        }
    }

    private suspend fun requestDefaultCover(imageSize: Int): Bitmap? {
        return withContext(default) {
            ContextCompat.getDrawable(
                this@PlaybackService,
                R.drawable.ic_round_audiotrack_24
            )?.toBitmap(imageSize, imageSize)
        }
    }

    override fun onGetRoot(
        clientPackageName: String, clientUid: Int, rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot(packageName, null)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.detach()
    }

    override fun onDestroy() {
        audioPlayer.close()

        super.onDestroy()

        mediaSessionCompat.release()
        _mediaSessionCompat = null
    }

}