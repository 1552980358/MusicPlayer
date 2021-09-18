package sakuraba.saki.player.music.base

import android.content.ComponentName
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.appcompat.app.AppCompatActivity
import sakuraba.saki.player.music.service.PlayService

abstract class BaseMediaControlActivity: AppCompatActivity() {
    
    protected lateinit var mediaBrowserCompat: MediaBrowserCompat
    private lateinit var connectionCallback: MediaBrowserCompat.ConnectionCallback
    private var subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) =
            onSubscriptionChildrenLoaded(parentId, children)
    }
    
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
        mediaBrowserCompat.unsubscribe(parentId)
        mediaBrowserCompat.subscribe(parentId, subscriptionCallback)
    
        mediaControllerCompat = MediaControllerCompat(this, mediaBrowserCompat.sessionToken)
        MediaControllerCompat.setMediaController(this, mediaControllerCompat)
        mediaControllerCompat.registerCallback(mediaControllerCallback)
    }
    
    override fun onStart() {
        super.onStart()
        if (!mediaBrowserCompat.isConnected) {
            mediaBrowserCompat.connect()
        }
    }
    
    override fun onDestroy() {
        if (mediaBrowserCompat.isConnected) {
            mediaControllerCompat.unregisterCallback(mediaControllerCallback)
            mediaBrowserCompat.unsubscribe(parentId, subscriptionCallback)
            mediaBrowserCompat.disconnect()
        }
        super.onDestroy()
    }
    
    abstract fun onMediaBrowserConnected()
    open fun onMediaBrowserConnectionSuspended() = Unit
    open fun onMediaBrowserConnectionFailed() = Unit
    
    abstract fun onMediaControllerPlaybackStateChanged(state: PlaybackStateCompat?)
    
    abstract fun onMediaControllerMetadataChanged(metadata: MediaMetadataCompat?)
    
    open fun onSubscriptionChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) = Unit
    
    private val parentId get() = getParentID()
    
    abstract fun getParentID(): String
    
}