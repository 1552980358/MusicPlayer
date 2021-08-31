package sakuraba.saki.player.music.base

import android.content.ComponentName
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lib.github1552980358.ktExtension.jvm.keyword.tryOnly
import sakuraba.saki.player.music.service.PlayService
import sakuraba.saki.player.music.service.PlayService.Companion.ROOT_ID

abstract class BaseMediaControlActivity: AppCompatActivity() {
    
    protected lateinit var mediaBrowserCompat: MediaBrowserCompat
    private lateinit var connectionCallback: MediaBrowserCompat.ConnectionCallback
    private var subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() { }
    
    protected lateinit var mediaControllerCompat: MediaControllerCompat
    private lateinit var mediaControllerCallback: MediaControllerCompat.Callback
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionCallback = object : MediaBrowserCompat.ConnectionCallback() {
            override fun onConnected() = onMediaBrowserConnected()
            override fun onConnectionSuspended() = onMediaBrowserConnectionSuspended()
            override fun onConnectionFailed() = onMediaBrowserConnectionFailed()
        }
        mediaControllerCallback = object : MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) = onMediaControllerPlaybackStateChanged(state)
            override fun onMetadataChanged(metadata: MediaMetadataCompat?) = onMediaControllerMetadataChanged(metadata)
        }
        mediaBrowserCompat = MediaBrowserCompat(this, ComponentName(this, PlayService::class.java), connectionCallback, null)
    }
    
    protected fun registerMediaController() {
        mediaBrowserCompat.unsubscribe(ROOT_ID)
        mediaBrowserCompat.subscribe(ROOT_ID, subscriptionCallback)
    
        mediaControllerCompat = MediaControllerCompat(this, mediaBrowserCompat.sessionToken)
        MediaControllerCompat.setMediaController(this, mediaControllerCompat)
        mediaControllerCompat.registerCallback(mediaControllerCallback)
    }
    
    override fun onPause() {
        super.onPause()
        if (mediaBrowserCompat.isConnected) {
            try {
                mediaControllerCompat.unregisterCallback(mediaControllerCallback)
                mediaBrowserCompat.disconnect()
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.IO).launch {
                    delay(2000)
                    if (mediaBrowserCompat.isConnected) {
                        tryOnly {
                            mediaControllerCompat.unregisterCallback(mediaControllerCallback)
                            mediaBrowserCompat.disconnect()
                        }
                    }
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        if (!mediaBrowserCompat.isConnected) {
            try { mediaBrowserCompat.connect() }
            catch (e: Exception) {
                CoroutineScope(Dispatchers.IO).launch {
                    delay(2000)
                    if (!mediaBrowserCompat.isConnected) {
                        tryOnly { mediaBrowserCompat.connect() }
                    }
                }
            }
        }
    }
    
    abstract fun onMediaBrowserConnected()
    open fun onMediaBrowserConnectionSuspended() = Unit
    open fun onMediaBrowserConnectionFailed() = Unit
    
    abstract fun onMediaControllerPlaybackStateChanged(state: PlaybackStateCompat?)
    
    abstract fun onMediaControllerMetadataChanged(metadata: MediaMetadataCompat?)
    
}