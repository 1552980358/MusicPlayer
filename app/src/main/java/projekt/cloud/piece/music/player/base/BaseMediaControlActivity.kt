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
import androidx.annotation.MainThread
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.service.PlayService
import projekt.cloud.piece.music.player.service.play.Action
import projekt.cloud.piece.music.player.util.Constant.DELAY_MILS

abstract class BaseMediaControlActivity: BaseThemeActivity() {
    
    protected lateinit var mediaBrowserCompat: MediaBrowserCompat
    private lateinit var subscriptionCallback: SubscriptionCallback
    private lateinit var mediaControllerCallback: Callback
    protected lateinit var mediaControllerCompat: MediaControllerCompat
    
    private var job: Job? = null
    protected var isPlaying = false
        protected set(value) {
            if (field != value) {
                field = value
                if (!value) {
                    job?.cancel()
                }
            }
        }
    
    private suspend fun Long.correctTime() =
        this + (this % DELAY_MILS).apply { delay(this) }
    
    protected fun startPlaying(progress: Long) {
        isPlaying = false
        isPlaying = true
        job = io {
            var currentProgress = progress.correctTime()
            do {
                ui { updateTime(currentProgress) }
                delay(DELAY_MILS)
                currentProgress += DELAY_MILS
            } while (isPlaying)
        }
    }
    
    @MainThread
    abstract fun updateTime(currentProgress: Long)
    
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
                override fun onConnected() {
                    mediaBrowserCompat.unsubscribe(parentId)
                    mediaBrowserCompat.subscribe(parentId, subscriptionCallback)
    
                    mediaControllerCompat = MediaControllerCompat(this@BaseMediaControlActivity, mediaBrowserCompat.sessionToken)
                    MediaControllerCompat.setMediaController(this@BaseMediaControlActivity, mediaControllerCompat)
    
                    mediaControllerCallback = object : Callback() {
                        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) =
                            this@BaseMediaControlActivity.onPlaybackStateChanged(state)
                        override fun onMetadataChanged(metadata: MediaMetadataCompat?) =
                            this@BaseMediaControlActivity.onMetadataChanged(metadata)
                    }
    
                    mediaControllerCompat.registerCallback(mediaControllerCallback)
                    
                    requestSyncService()
    
                    this@BaseMediaControlActivity.onConnected()
                }
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
    
    override fun onResume() {
        super.onResume()
        if (mediaBrowserCompat.isConnected) {
            requestSyncService()
        }
    }
    
    override fun onPause() {
        super.onPause()
        isPlaying = false
    }
    
    override fun onDestroy() {
        if (mediaBrowserCompat.isConnected) {
            mediaControllerCompat.unregisterCallback(mediaControllerCallback)
            mediaBrowserCompat.unsubscribe(parentId, subscriptionCallback)
            mediaBrowserCompat.disconnect()
        }
        super.onDestroy()
    }
    
    protected fun requestSyncService() =
        mediaBrowserCompat.sendCustomAction(Action.ACTION_SYNC_SERVICE, null, null)
    
    open fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) = Unit
    
    abstract fun onMetadataChanged(metadata: MediaMetadataCompat?)
    
    abstract fun onPlaybackStateChanged(state: PlaybackStateCompat?)
    
    open fun onConnected() = Unit
    
    open fun onConnectionSuspended() = Unit
    
    open fun onConnectionFailed() = Unit
    
    private val parentId get() = getParentID()
    
    abstract fun getParentID(): String
    
}