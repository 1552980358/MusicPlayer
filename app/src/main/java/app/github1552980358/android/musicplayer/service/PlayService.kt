package app.github1552980358.android.musicplayer.service

import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import app.github1552980358.android.musicplayer.base.AudioData.Companion.audioDataMap
import app.github1552980358.android.musicplayer.base.Constant.Companion.RootId

/**
 * @file    : [PlayService]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/8
 * @time    : 21:22
 **/

class PlayService : MediaBrowserServiceCompat(), MediaPlayerUtil {
    
    private lateinit var playBackStateCompatBuilder: PlaybackStateCompat.Builder
    private lateinit var playStateCompat: PlaybackStateCompat
    
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private lateinit var mediaSessionCompatCallback: MediaSessionCompat.Callback
    
    private var mediaItemList = ArrayList<MediaBrowserCompat.MediaItem>()
    
    private var mediaPlayer = MediaPlayer()
    
    private var startTime = 0L
    private var pauseTime = 0L
    
    override fun onCreate() {
        super.onCreate()
        Log.e("PlayService", "onCreate")
        
        playBackStateCompatBuilder = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY_PAUSE
                    or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                    or PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM
                    or PlaybackStateCompat.ACTION_SEEK_TO
                    or PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
            )
        
        mediaSessionCompatCallback = object : MediaSessionCompat.Callback() {
            
            override fun onPlay() {
                if (playStateCompat.state == PlaybackStateCompat.STATE_PAUSED) {
                    Log.e("playStateCompat.state", "STATE_BUFFERING")
                    playStateCompat = PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PLAYING, pauseTime - startTime, 1F)
                        .build()
                    startTime = System.currentTimeMillis()
                    onPlay(mediaPlayer)
                    mediaSessionCompat.setPlaybackState(playStateCompat)
                    return
                }
    
                if (playStateCompat.state == PlaybackStateCompat.STATE_BUFFERING) {
                    Log.e("playStateCompat.state", "STATE_BUFFERING")
                    startTime = System.currentTimeMillis()
                    onPlay(mediaPlayer)
                    playStateCompat = PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PLAYING, 0L, 1F)
                        .build()
                    mediaSessionCompat.setPlaybackState(playStateCompat)
                }
            }
            
            override fun onPause() {
                if (playStateCompat.state == PlaybackStateCompat.STATE_PLAYING) {
                    Log.e("playStateCompat.state", "STATE_PLAYING")
                    pauseTime = System.currentTimeMillis()
                    onPause(mediaPlayer)
                    playStateCompat = PlaybackStateCompat
                        .Builder()
                        .setState(PlaybackStateCompat.STATE_PAUSED, pauseTime - startTime, 1F)
                        .build()
                    mediaSessionCompat.setPlaybackState(playStateCompat)
                    return
                }
            }
            
            override fun onSkipToNext() {
            
            }
            
            override fun onSkipToPrevious() {
            }
            
            override fun onSeekTo(pos: Long) {
                mediaSessionCompat.setPlaybackState(
                    playBackStateCompatBuilder
                        .setState(playStateCompat.state, pos, 1F)
                        .build()
                        .apply { playStateCompat = this }
                )
                onSeekTo(mediaPlayer, pos)
            }
            
            override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
                playStateCompat = PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_BUFFERING, 0L, 1F)
                    .build()
                mediaSessionCompat.setPlaybackState(playStateCompat)
                mediaSessionCompat.setMetadata(
                    MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, audioDataMap[mediaId]?.title)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, audioDataMap[mediaId]?.artist)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, audioDataMap[mediaId]?.album)
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, audioDataMap[mediaId]?.duration!!)
                        .build()
                )
                onPlayFromMediaId(this@PlayService, mediaPlayer, this, mediaId!!)
            }
        }
        
        mediaSessionCompat = MediaSessionCompat(this, RootId).apply {
            setCallback(mediaSessionCompatCallback)
            setPlaybackState(
                playBackStateCompatBuilder
                    .setState(PlaybackStateCompat.STATE_NONE, 0, 1F)
                    .build()
                    .apply { playStateCompat= this })
            
            isActive = true
        }
        sessionToken = mediaSessionCompat.sessionToken
        
        initialMediaPlayer(mediaPlayer)
    }
    
    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        Log.e("PlayService", "onLoadChildren")
        result.detach()
        result.sendResult(mediaItemList)
    }
    
    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return BrowserRoot(RootId, null)//browserRoot
    }
    
}