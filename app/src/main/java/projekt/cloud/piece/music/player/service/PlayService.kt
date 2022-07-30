package projekt.cloud.piece.music.player.service

import android.content.Intent
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
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import projekt.cloud.piece.music.player.BuildConfig
import projekt.cloud.piece.music.player.BuildConfig.APPLICATION_ID

class PlayService: MediaBrowserServiceCompat(), Player.Listener {
    
    private companion object {
        
        const val TAG = "${APPLICATION_ID}.PlayService"
        
        const val DEFAULT_PLAYBACK_SPEED = 1.0f
    
        const val PLAYBACK_STATE_ACTIONS =
            ACTION_PLAY or ACTION_PAUSE or ACTION_PLAY_PAUSE or ACTION_STOP or ACTION_SEEK_TO or
                ACTION_PLAY_FROM_MEDIA_ID or ACTION_SKIP_TO_NEXT or ACTION_SKIP_TO_PREVIOUS or ACTION_SKIP_TO_QUEUE_ITEM
        
        const val VOLUME_FULL = 1.0f
    }
    
    private lateinit var exoPlayer: ExoPlayer
    private var playbackStateCompat = PlaybackStateCompat.Builder()
        .setState(STATE_NONE, 0, DEFAULT_PLAYBACK_SPEED)
        .setActions(PLAYBACK_STATE_ACTIONS)
        .build()
    private lateinit var mediaSessionCompat: MediaSessionCompat
    
    override fun onCreate() {
        super.onCreate()
    
        mediaSessionCompat = MediaSessionCompat(this, TAG).apply {
            setCallback(object: MediaSessionCompat.Callback() {
                override fun onPlay() {
                }
                override fun onPause() {
                }
                override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
                }
                override fun onSkipToPrevious() {
                }
                override fun onSkipToNext() {
                }
                override fun onSkipToQueueItem(id: Long) {
                }
                override fun onSeekTo(pos: Long) {
                }
                override fun onCustomAction(action: String?, extras: Bundle?) {
                }
            })
            setPlaybackState(playbackStateCompat)
            isActive = true
            
            this@PlayService.sessionToken = sessionToken
        }
        
        exoPlayer = ExoPlayer.Builder(this)
            .build()
        with(exoPlayer) {
            addListener(this@PlayService)
            volume = VOLUME_FULL
        }
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
    
    override fun onDestroy() {
        exoPlayer.stop()
        exoPlayer.release()
        super.onDestroy()
    }
    
    override fun onPlaybackStateChanged(playbackState: Int) {
    }
    
    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?) = when (clientPackageName) {
        APPLICATION_ID -> BrowserRoot(TAG, null)
        else -> null
    }
    
    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(null)
    }
    
}