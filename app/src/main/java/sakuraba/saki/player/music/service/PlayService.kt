package sakuraba.saki.player.music.service

import android.content.Intent
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
import android.util.Log
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
            Log.e(TAG, "onPlay")
        }
        override fun onPause() {
            Log.e(TAG, "onPause")
        }
        override fun onSkipToPrevious() {
            Log.e(TAG, "onSkipToPrevious")
        }
        override fun onSkipToNext() {
            Log.e(TAG, "onSkipToNext")
        }
        override fun onSkipToQueueItem(id: Long) {
            Log.e(TAG, "onSkipToQueueItem $id")
        }
        override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
            Log.e(TAG, "onPlayFromUri $uri")
        }
        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            Log.e(TAG, "onPlayFromMediaId $mediaId")
        }
        override fun onSeekTo(pos: Long) {
            Log.e(TAG, "onSeekTo $pos")
        }
    }
    
    private lateinit var playbackStateCompat: PlaybackStateCompat
    
    override fun onCreate() {
        Log.e(TAG, "onCreate")
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
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand $flags $startId")
        return super.onStartCommand(intent, flags, startId)
    }
    
    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?) = when (clientPackageName) {
        BuildConfig.APPLICATION_ID -> BrowserRoot(TAG, null)
        else -> null
    }
    
    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        Log.e(TAG, "onLoadChildren $parentId")
        result.sendResult(null)
    }
    
}