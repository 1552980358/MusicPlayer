package projekt.cloud.piece.music.player.service.playback

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat.MediaItem
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
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import android.support.v4.media.session.PlaybackStateCompat.ShuffleMode
import androidx.media.session.MediaButtonReceiver
import com.google.android.exoplayer2.ExoPlayer
import projekt.cloud.piece.music.player.base.BaseLifecycleMediaBrowserService

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

    private var _exoPlayer: ExoPlayer? = null
    private val exoPlayer: ExoPlayer
        get() = _exoPlayer!!

    override fun onCreate() {
        super.onCreate()

        _mediaSessionCompat = MediaSessionCompat(this, TAG)
        with(mediaSessionCompat) {
            setCallback(
                object: MediaSessionCompat.Callback() {
                    override fun onPlay() {
                        super.onPlay()
                    }

                    override fun onPause() {
                        super.onPause()
                    }

                    override fun onSkipToPrevious() {
                        super.onSkipToPrevious()
                    }

                    override fun onSkipToNext() {
                        super.onSkipToNext()
                    }

                    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
                        super.onPlayFromMediaId(mediaId, extras)
                    }

                    override fun onSkipToQueueItem(id: Long) {
                        super.onSkipToQueueItem(id)
                    }

                    override fun onSeekTo(pos: Long) {
                        super.onSeekTo(pos)
                    }
                }
            )

            setPlaybackState(playbackStateCompat)

            setShuffleMode(shuffleMode)
            setRepeatMode(repeatMode)

            isActive = true
        }
        sessionToken = mediaSessionCompat.sessionToken
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getStringExtra(START_COMMAND_PLAYBACK)) {
            START_COMMAND_PLAYBACK_START -> {
            }
            START_COMMAND_PLAYBACK_PAUSE -> {
            }
            else -> { MediaButtonReceiver.handleIntent(mediaSessionCompat, intent) }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetRoot(
        clientPackageName: String, clientUid: Int, rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot(packageName, null)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaItem>>) {
        result.detach()
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaSessionCompat.release()
        _mediaSessionCompat = null

        exoPlayer.release()
        _exoPlayer = null
    }

}