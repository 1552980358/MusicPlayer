package app.github1552980358.android.musicplayer.service

import android.app.Notification
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_PAUSE
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SEEK_TO
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_NEXT
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM
import android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.media.MediaBrowserServiceCompat
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.base.AudioData.Companion.audioDataMap
import app.github1552980358.android.musicplayer.base.Constant.Companion.AlbumNormal
import app.github1552980358.android.musicplayer.base.Constant.Companion.RootId
import app.github1552980358.android.musicplayer.service.CycleMode.LIST_CYCLE
import app.github1552980358.android.musicplayer.service.CycleMode.RANDOM_ACCESS
import app.github1552980358.android.musicplayer.service.CycleMode.SINGLE_CYCLE
import java.io.File

/**
 * @file    : [PlayService]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/8
 * @time    : 21:22
 **/

class PlayService : MediaBrowserServiceCompat(),
    MediaPlayerUtil, NotificationUtil, MediaPlayer.OnCompletionListener {
    
    companion object {
        
        private const val START_FLAG = "START_FLAG"
        private const val STOP_FOREGROUND = "STOP_FOREGROUND"
        private const val START_FOREGROUND = "START_FOREGROUND"
        
        private const val playbackStateActions = (
            ACTION_PLAY_PAUSE
                or ACTION_SEEK_TO
                or ACTION_PLAY_FROM_MEDIA_ID
                or ACTION_SKIP_TO_NEXT
                or ACTION_SKIP_TO_PREVIOUS
                or ACTION_SKIP_TO_QUEUE_ITEM)
        
    }
    
    private lateinit var playbackStateCompat: PlaybackStateCompat
    private lateinit var mediaMetadataCompat: MediaMetadataCompat
    
    private var cycleMode = SINGLE_CYCLE
    
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private lateinit var mediaSessionCompatCallback: MediaSessionCompat.Callback
    private var mediaItemList = ArrayList<MediaBrowserCompat.MediaItem>()
    
    private var mediaPlayer = MediaPlayer()
    
    private var startTime = 0L
    private var pauseTime = 0L
    
    private lateinit var notificationManagerCompat: NotificationManagerCompat
    
    override fun onCreate() {
        super.onCreate()
        Log.e("PlayService", "onCreate")
        
        mediaSessionCompatCallback = object : MediaSessionCompat.Callback() {
            
            @Suppress("DuplicatedCode")
            override fun onPlay() {
                
                if (playbackStateCompat.state == STATE_PAUSED) {
                    Log.e("playStateCompat.state", "STATE_PAUSED")
                    playbackStateCompat = PlaybackStateCompat.Builder()
                        .setActions(playbackStateActions)
                        .setState(STATE_PLAYING, pauseTime - startTime, 1F)
                        .build()
                    @Suppress("DuplicatedCode")
                    startTime = System.currentTimeMillis()
                    onPlay(mediaPlayer)
                    mediaSessionCompat.setPlaybackState(playbackStateCompat)
                    
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(
                            Intent(this@PlayService, PlayService::class.java)
                                .putExtra(START_FLAG, START_FOREGROUND)
                        )
                    } else {
                        startService(
                            Intent(this@PlayService, PlayService::class.java)
                                .putExtra(START_FLAG, START_FOREGROUND)
                        )
                    }
                    return
                }
    
                if (playbackStateCompat.state == STATE_PLAYING) {
                    Log.e("playStateCompat.state", "STATE_PLAYING")
                    playbackStateCompat = PlaybackStateCompat.Builder()
                        .setActions(playbackStateActions)
                        .setState(STATE_PLAYING, 0L, 1F)
                        .build()
                    startTime = System.currentTimeMillis()
                    onPlay(mediaPlayer)
                    mediaSessionCompat.setPlaybackState(playbackStateCompat)
        
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(
                            Intent(this@PlayService, PlayService::class.java)
                                .putExtra(START_FLAG, START_FOREGROUND)
                        )
                    } else {
                        startService(
                            Intent(this@PlayService, PlayService::class.java)
                                .putExtra(START_FLAG, START_FOREGROUND)
                        )
                    }
                    return
                }
    
                if (playbackStateCompat.state == STATE_BUFFERING) {
                    Log.e("playStateCompat.state", "STATE_BUFFERING")
                    startTime = System.currentTimeMillis()
                    onPlay(mediaPlayer)
                    playbackStateCompat = PlaybackStateCompat.Builder()
                        .setActions(playbackStateActions)
                        .setState(STATE_PLAYING, 0L, 1F)
                        .build()
                    mediaSessionCompat.setPlaybackState(playbackStateCompat)
    
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(
                            Intent(this@PlayService, PlayService::class.java)
                                .putExtra(START_FLAG, START_FOREGROUND)
                        )
                    } else {
                        startService(
                            Intent(this@PlayService, PlayService::class.java)
                                .putExtra(START_FLAG, START_FOREGROUND)
                        )
                    }
                }
                
            }
            
            override fun onPause() {
                if (playbackStateCompat.state == STATE_PLAYING) {
                    Log.e("playStateCompat.state", "STATE_PLAYING")
                    pauseTime = System.currentTimeMillis()
                    onPause(mediaPlayer)
                    playbackStateCompat = PlaybackStateCompat
                        .Builder()
                        .setActions(playbackStateActions)
                        .setState(STATE_PAUSED, pauseTime - startTime, 1F)
                        .build()
                    mediaSessionCompat.setPlaybackState(playbackStateCompat)
    
                    startService(
                        Intent(this@PlayService, PlayService::class.java)
                            .putExtra(START_FLAG, STOP_FOREGROUND)
                    )
    
                    return
                }
            }
            
            override fun onSkipToNext() {
            
            }
            
            override fun onSkipToPrevious() {
            }
            
            override fun onSeekTo(pos: Long) {
                if (playbackStateCompat.state == STATE_PLAYING) {
                    Log.e("playStateCompat.state", "STATE_PLAYING")
                    startTime -= (pos - System.currentTimeMillis() + startTime)
                    onSeekTo(mediaPlayer, pos)
                    playbackStateCompat = PlaybackStateCompat.Builder()
                        .setState(STATE_PLAYING, pos, 1F)
                        .setActions(playbackStateActions)
                        .build()
                    mediaSessionCompat.setPlaybackState(playbackStateCompat)
                    return
                }
                
                if (playbackStateCompat.state == STATE_PAUSED) {
                    Log.e("playStateCompat.state", "STATE_PAUSED")
                    pauseTime += (pos - pauseTime + startTime)
                    onSeekTo(mediaPlayer, pos)
                    playbackStateCompat = PlaybackStateCompat.Builder()
                        .setState(STATE_PAUSED, pos, 1F)
                        .setActions(playbackStateActions)
                        .build()
                    mediaSessionCompat.setPlaybackState(playbackStateCompat)
                }
            }
            
            override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
                playbackStateCompat = PlaybackStateCompat.Builder()
                    .setActions(playbackStateActions)
                    .setState(STATE_BUFFERING, 0L, 1F)
                    .build()
                mediaSessionCompat.setPlaybackState(playbackStateCompat)
                mediaMetadataCompat = MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, audioDataMap[mediaId]?.title)
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, audioDataMap[mediaId]?.artist)
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, audioDataMap[mediaId]?.album)
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, audioDataMap[mediaId]?.duration!!)
                    .build()
                mediaSessionCompat.setMetadata(mediaMetadataCompat)
                onPlayFromMediaId(this@PlayService, mediaPlayer, this, mediaId!!)
            }
            
        }
        
        mediaSessionCompat = MediaSessionCompat(this, RootId).apply {
            setCallback(mediaSessionCompatCallback)
            setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setActions(playbackStateActions)
                    .setState(STATE_NONE, 0, 1F)
                    .build()
            )
    
            isActive = true
        }
        sessionToken = mediaSessionCompat.sessionToken
        
        initialMediaPlayer(mediaPlayer, this)
        
        notificationManagerCompat = createNotificationManager(this)
    }
    
    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        Log.e("PlayService", "onLoadChildren")
        result.detach()
        result.sendResult(mediaItemList)
    }
    
    /**
     * [onGetRoot]
     * @param clientPackageName [String]
     * @param clientUid [Int]
     * @param rootHints [Bundle]?
     * @return [Int]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return BrowserRoot(RootId, null)//browserRoot
    }
    
    /**
     * [onStartCommand]
     * @param intent [Intent]
     * @param flags [Int]
     * @param startId [Int]
     * @return [Int]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getStringExtra(START_FLAG)) {
            START_FOREGROUND -> startForeground(this, getNotification())
            STOP_FOREGROUND -> stopForeground(false)
        }
        
        return super.onStartCommand(intent, flags, startId)
    }
    
    /**
     * [getNotification]
     * @return [Notification]
     * @author 1552980358
     * @since 0.1
     **/
    private fun getNotification(): Notification {
        return NotificationCompat.Builder(this, NotificationUtil.ChannelId)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSessionCompat.sessionToken))
            .apply {
                File(getExternalFilesDir(AlbumNormal), mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)).apply {
                    if (exists()) {
                        inputStream().use { `is` ->
                            setLargeIcon(BitmapFactory.decodeStream(`is`))
                        }
                    } else {
                        setLargeIcon(ContextCompat.getDrawable(this@PlayService, R.drawable.ic_launcher_foreground)?.toBitmap())
                    }
                }
            }
            .setContentTitle(mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_TITLE))
            .setContentText("${mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)} - ${mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ALBUM)}")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOngoing(false)
            .build()
    }
    
    override fun onCompletion(mp: MediaPlayer?) {
        when (cycleMode) {
            SINGLE_CYCLE -> {
                mediaSessionCompatCallback.onPlay()
            }
            LIST_CYCLE -> {
            
            }
            RANDOM_ACCESS -> {
            
            }
        }
    }
    
}