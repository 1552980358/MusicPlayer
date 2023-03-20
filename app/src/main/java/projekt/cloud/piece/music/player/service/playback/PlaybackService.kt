package projekt.cloud.piece.music.player.service.playback

import android.app.Notification
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
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
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
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
import projekt.cloud.piece.music.player.util.UriUtil.albumArtUri

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

        const val NOTIFICATION_ID = 1

        const val DURATION_START = 0L
    }

    private var _mediaSessionCompat: MediaSessionCompat? = null
    private val mediaSessionCompat: MediaSessionCompat
        get() = _mediaSessionCompat!!

    private var playbackStateCompat = PlaybackStateCompat.Builder()
        .setActions(PLAYBACK_ACTION)
        .setState(STATE_NONE, 0, PLAYBACK_SPEED)
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

    override fun onCreate() {
        super.onCreate()

        _mediaSessionCompat = MediaSessionCompat(this, TAG)
        with(mediaSessionCompat) {
            val runtimeDatabase = runtimeDatabase

            setCallback(
                object: MediaSessionCompat.Callback() {
                    override fun onPlay() {
                        if (playbackStateCompat.playbackState != STATE_PAUSED
                            && playbackStateCompat.playbackState != STATE_BUFFERING) {
                            return
                        }

                        // Update state
                        playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                            // .setState(STATE_PLAYING, , PLAYBACK_SPEED)
                            .build()
                    }

                    override fun onPause() {
                        if (playbackStateCompat.playbackState == STATE_PLAYING) {
                            // Pause player

                            playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                                // .setState(STATE_PAUSED, , PLAYBACK_SPEED)
                                .build()
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
                            _audioMetadata = withContext(default) {
                                runtimeDatabase.audioMetadataDao()
                                    .query(mediaId)
                            }
                            order = withContext(default) {
                                runtimeDatabase.playbackDao()
                                    .queryOrder(mediaId)
                            }

                            // Update state
                            playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                                .setState(STATE_BUFFERING, 0L, PLAYBACK_SPEED)
                                .build()

                            // Prepare

                            // Start play
                            onPlay()
                        }
                    }

                    override fun onSkipToQueueItem(id: Long) {
                        super.onSkipToQueueItem(id)
                    }

                    override fun onSeekTo(pos: Long) {
                        // Update player pos

                        // Update state
                        playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                            .setState(playbackStateCompat.state, pos, PLAYBACK_SPEED)
                            .build()
                    }
                }
            )

            setPlaybackState(playbackStateCompat)

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
                }
            }
            START_COMMAND_PLAYBACK_PAUSE -> {
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
            else -> { MediaButtonReceiver.handleIntent(mediaSessionCompat, intent) }
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
        super.onDestroy()

        mediaSessionCompat.release()
        _mediaSessionCompat = null
    }

}