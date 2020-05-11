package app.github1552980358.android.musicplayer.base

import android.content.ComponentName
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import app.github1552980358.android.musicplayer.service.PlayService

/**
 * @file    : [BaseAppCompatActivity]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/9
 * @time    : 12:04
 **/

abstract class BaseAppCompatActivity: AppCompatActivity() {
    
    lateinit var mediaBrowserCompat: MediaBrowserCompat
    lateinit var connectionCallback: MediaBrowserCompat.ConnectionCallback
    lateinit var subscriptionCallback: MediaBrowserCompat.SubscriptionCallback
    lateinit var callback: MediaControllerCompat.Callback
    
    // Controller
    lateinit var mediaControllerCompat: MediaControllerCompat
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        callback = object : MediaControllerCompat.Callback() {
            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                this@BaseAppCompatActivity.onMetadataChanged(metadata)
            }
        
            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                this@BaseAppCompatActivity.onPlaybackStateChanged(state)
            }
        }
    
        connectionCallback = object : MediaBrowserCompat.ConnectionCallback() {
        
            override fun onConnectionSuspended() {
                Log.e("connectionCallback", "onConnectionSuspended")
            }
        
            override fun onConnected() {
                Log.e("connectionCallback", "onConnected")
                if (mediaBrowserCompat.isConnected) {
                
                    // Subscription
                    mediaBrowserCompat.unsubscribe("root")
                    mediaBrowserCompat.subscribe("root", subscriptionCallback)
                
                    // Update controller
                    mediaControllerCompat = MediaControllerCompat(this@BaseAppCompatActivity, mediaBrowserCompat.sessionToken)
                    MediaControllerCompat.setMediaController(this@BaseAppCompatActivity, mediaControllerCompat)
                    mediaControllerCompat.registerCallback(callback)
                }
                
            }
        
            override fun onConnectionFailed() {
                Log.e("connectionCallback", "onConnectionFailed")
            }
        
        }
    
        subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
                Log.e("SubscriptionCallback", "onChildrenLoaded")
                this@BaseAppCompatActivity.onChildrenLoaded(parentId, children)
            }
        
            override fun onError(parentId: String) {
                super.onError(parentId)
                Log.e("SubscriptionCallback", "onError")
            }
        
            override fun onError(parentId: String, options: Bundle) {
                super.onError(parentId, options)
                Log.e("SubscriptionCallback", "onError")
            }
        }
    
        mediaBrowserCompat = MediaBrowserCompat(
            this@BaseAppCompatActivity,
            ComponentName(this@BaseAppCompatActivity, PlayService::class.java),
            connectionCallback,
            null
        )
        
    }
    
    override fun onPause() {
        super.onPause()
        mediaBrowserCompat.disconnect()
    }
    
    override fun onResume() {
        super.onResume()
        mediaBrowserCompat.connect()
    }
    
    abstract fun onMetadataChanged(metadata: MediaMetadataCompat?)
    
    abstract fun onPlaybackStateChanged(state: PlaybackStateCompat?)
    
    abstract fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>)
    
}