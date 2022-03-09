package projekt.cloud.piece.music.player.base

import android.content.ComponentName
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.ConnectionCallback
import android.support.v4.media.MediaBrowserCompat.SubscriptionCallback
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaControllerCompat.Callback
import android.support.v4.media.session.PlaybackStateCompat
import projekt.cloud.piece.music.player.service.PlayService

abstract class BaseMediaControlActivity: BaseThemeActivity() {
    
    protected lateinit var mediaBrowserCompat: MediaBrowserCompat
    private lateinit var subscriptionCallback: SubscriptionCallback
    private lateinit var mediaControllerCallback: Callback
    protected lateinit var mediaControllerCompat: MediaControllerCompat
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    
        subscriptionCallback = object : SubscriptionCallback() {
            override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) =
                this@BaseMediaControlActivity.onChildrenLoaded(parentId, children)
        }
        
        mediaBrowserCompat = MediaBrowserCompat(
            this,
            ComponentName(this, PlayService::class.java),
            object : ConnectionCallback() {
                override fun onConnected() = this@BaseMediaControlActivity.onConnected()
                override fun onConnectionSuspended() = this@BaseMediaControlActivity.onConnectionSuspended()
                override fun onConnectionFailed() = this@BaseMediaControlActivity.onConnectionFailed()
            }, null
        )
    }
    
    override fun onStart() {
        super.onStart()
        if (!mediaBrowserCompat.isConnected) {
            mediaBrowserCompat.connect()
        }
    }
    
    protected fun registerMediaController() {
        mediaBrowserCompat.unsubscribe(parentId)
        mediaBrowserCompat.subscribe(parentId, subscriptionCallback)
        
        mediaControllerCompat = MediaControllerCompat(this, mediaBrowserCompat.sessionToken)
        MediaControllerCompat.setMediaController(this, mediaControllerCompat)
        
        mediaControllerCallback = object : Callback() {
            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) =
                this@BaseMediaControlActivity.onPlaybackStateChanged(state)
            override fun onMetadataChanged(metadata: MediaMetadataCompat?) =
                this@BaseMediaControlActivity.onMetadataChanged(metadata)
        }
        
        mediaControllerCompat.registerCallback(mediaControllerCallback)
    }
    
    override fun onDestroy() {
        if (mediaBrowserCompat.isConnected) {
            mediaControllerCompat.unregisterCallback(mediaControllerCallback)
            mediaBrowserCompat.unsubscribe(parentId, subscriptionCallback)
            mediaBrowserCompat.disconnect()
        }
        super.onDestroy()
    }
    
    open fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) = Unit
    
    abstract fun onMetadataChanged(metadata: MediaMetadataCompat?)
    
    abstract fun onPlaybackStateChanged(state: PlaybackStateCompat?)
    
    abstract fun onConnected()
    
    open fun onConnectionSuspended() = Unit
    
    open fun onConnectionFailed() = Unit
    
    private val parentId get() = getParentID()
    
    abstract fun getParentID(): String
    
}