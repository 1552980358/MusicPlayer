package sakuraba.saki.player.music.service

import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_PAUSE
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SEEK_TO
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_NEXT
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import androidx.media.MediaBrowserServiceCompat
import sakuraba.saki.player.music.BuildConfig

class PlayService: MediaBrowserServiceCompat() {
    
    companion object {
        private const val TAG = "BackgroundPlayService"
        const val ROOT_ID = TAG
        private const val PlaybackStateActions =
            ACTION_PLAY_PAUSE or ACTION_SEEK_TO or ACTION_PLAY_FROM_MEDIA_ID or ACTION_SKIP_TO_NEXT or ACTION_SKIP_TO_PREVIOUS or ACTION_SKIP_TO_QUEUE_ITEM
    }
    
    private lateinit var mediaSession: MediaSessionCompat
    private var mediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
        }
        override fun onPause() {
        }
        override fun onSkipToPrevious() {
        }
        override fun onSkipToNext() {
        }
        override fun onSkipToQueueItem(id: Long) {
        }
        override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
        }
        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        }
        override fun onSeekTo(pos: Long) {
        }
    }
    
    private lateinit var playbackStateCompat: PlaybackStateCompat
    
    override fun onCreate() {
        super.onCreate()
    
        playbackStateCompat = PlaybackStateCompat.Builder()
            .setActions(PlaybackStateActions)
            .setState(STATE_NONE, 0, 1F)
            .build()
        
        mediaSession = MediaSessionCompat(this, ROOT_ID).apply {
            setCallback(mediaSessionCallback)
            setPlaybackState(playbackStateCompat)
            isActive = true
        }
        sessionToken = mediaSession.sessionToken
    }
    
    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?) = when (clientPackageName) {
        BuildConfig.APPLICATION_ID -> BrowserRoot(TAG, null)
        else -> null
    }
    
    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(null)
    }
    
}