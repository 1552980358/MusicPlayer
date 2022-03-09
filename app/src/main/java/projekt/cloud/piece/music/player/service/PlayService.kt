package projekt.cloud.piece.music.player.service

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.MediaSessionCompat.Callback
import android.support.v4.media.session.PlaybackStateCompat
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
import com.google.android.exoplayer2.ExoPlayer.Builder
import com.google.android.exoplayer2.Player.Listener
import projekt.cloud.piece.music.player.BuildConfig.APPLICATION_ID

class PlayService: MediaBrowserServiceCompat(), Listener {
    
    companion object {
        /**
         *
         * (1 << 9) | (1 << 0) | (1 << 8) | (1 << 10) | (1 << 5) | (1 << 4) | (1 << 12)
         * -> 0b1011100110001
         * -> 5937
         **/
        const val PlaybackStateActions =
            ACTION_PLAY_PAUSE or ACTION_STOP or ACTION_SEEK_TO or ACTION_PLAY_FROM_MEDIA_ID or ACTION_SKIP_TO_NEXT or ACTION_SKIP_TO_PREVIOUS or ACTION_SKIP_TO_QUEUE_ITEM
        
        private const val TAG = "PlayService"
        private const val ROOT_ID = TAG
        
    }
    
    private lateinit var mediaSession: MediaSessionCompat
    private val mediaSessionCallback = object : Callback() {
        override fun onPlay() {
        
        }
    
        override fun onPause() {
        
        }
    
        override fun onStop() {
        
        }
    
        override fun onSkipToPrevious() {
        
        }
        
        override fun onSkipToNext() {
        
        }
    
        override fun onSkipToQueueItem(id: Long) {
        
        }
    
        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        
        }
    
        override fun onSeekTo(pos: Long) {
        
        }
        
    }
    
    @Volatile
    private lateinit var playbackStateCompat: PlaybackStateCompat
    
    private lateinit var exoPlayer: ExoPlayer
    
    override fun onCreate() {
        super.onCreate()
    
        // MediaBrowser Config
        playbackStateCompat = PlaybackStateCompat.Builder()
            .setState(STATE_NONE, 0, 1F)
            .setActions(PlaybackStateActions)
            .build()
    
        mediaSession = MediaSessionCompat(this, ROOT_ID).apply {
            setCallback(mediaSessionCallback)
            setPlaybackState(playbackStateCompat)
            isActive = true
        }
        
        sessionToken = mediaSession.sessionToken
    
        // ExoPlayer Config
        exoPlayer = Builder(this).build()
        exoPlayer.addListener(this)
        
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
    
    override fun onPlaybackStateChanged(playbackState: Int) {
    
    }
    
    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?) = when (clientPackageName) {
        APPLICATION_ID -> BrowserRoot(TAG, null)
        else -> null
    }
    
    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.detach()
        result.sendResult(null)
    }
    
}