package app.github1552980358.android.musicplayer.service

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import app.github1552980358.android.musicplayer.base.Constant.Companion.RootId
import app.github1552980358.android.musicplayer.base.AudioData

/**
 * @file    : [PlayService]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/8
 * @time    : 21:22
 **/

class PlayService : MediaBrowserServiceCompat(), MediaPlayerUtil {
    
    //companion object {
    //    const val TAG = "PlayService"
    //    const val BrowserID = "PlayServiceID"
    //}
    
    companion object {
    
    }
    
    private lateinit var playBackStateCompatBuilder: PlaybackStateCompat.Builder
    private lateinit var playStateCompat: PlaybackStateCompat
    
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private lateinit var mediaSessionCompatCallback: MediaSessionCompat.Callback
    
    private var mediaItemList = ArrayList<MediaBrowserCompat.MediaItem>()
    
    private var mediaPlayer = MediaPlayer()
    
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
                    onPlay(mediaPlayer)
                    mediaSessionCompat.setPlaybackState(
                            playBackStateCompatBuilder
                                .setState(PlaybackStateCompat.STATE_PLAYING, playStateCompat.position, 1F)
                                .build()
                                .apply { playStateCompat = this }
                    )
                    return
                }
    
                if (playStateCompat.state == PlaybackStateCompat.STATE_BUFFERING) {
                    onPlay(mediaPlayer)
                    mediaSessionCompat.setPlaybackState(
                        playBackStateCompatBuilder
                            .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1F)
                            .build()
                            .apply { playStateCompat = this }
                    )
                }
            }
            
            override fun onPause() {
                if (playStateCompat.state == PlaybackStateCompat.STATE_PLAYING) {
                    onPause(mediaPlayer)
                    mediaSessionCompat.setPlaybackState(
                        playBackStateCompatBuilder
                            .setState(PlaybackStateCompat.STATE_PAUSED, playStateCompat.position, 1F)
                            .build()
                            .apply { playStateCompat = this }
                    )
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
                mediaSessionCompat.setPlaybackState(
                    playBackStateCompatBuilder
                        .setState(PlaybackStateCompat.STATE_BUFFERING, 0, 1F)
                        .build()
                        .apply { playStateCompat = this }
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
        
        mediaItemList.clear()
        for (i in AudioData.audioData) {
            mediaItemList.add(
                MediaBrowserCompat.MediaItem(
                    MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, i.id)
                        //.putString(MediaMetadataCompat.METADATA_KEY_TITLE, i.title + ";" + i.artist)
                        //.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, i.album)
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, i.duration)
                        .build().description,
                    MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                )
            )
        }
        
        result.sendResult(mediaItemList)
    }
    
    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return BrowserRoot(RootId, null)//browserRoot
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
    
}