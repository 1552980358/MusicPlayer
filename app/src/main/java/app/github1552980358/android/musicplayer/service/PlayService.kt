package app.github1552980358.android.musicplayer.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.PowerManager.PARTIAL_WAKE_LOCK
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
import app.github1552980358.android.musicplayer.base.AudioData.Companion.audioDataList
import app.github1552980358.android.musicplayer.base.AudioData.Companion.audioDataMap
import app.github1552980358.android.musicplayer.base.Constant.Companion.AlbumNormal
import app.github1552980358.android.musicplayer.base.Constant.Companion.RootId
import app.github1552980358.android.musicplayer.service.CycleMode.LIST_CYCLE
import app.github1552980358.android.musicplayer.service.CycleMode.RANDOM_ACCESS
import app.github1552980358.android.musicplayer.service.CycleMode.SINGLE_CYCLE
import app.github1552980358.android.musicplayer.service.NotificationUtil.Companion.ServiceId
import java.io.File

/**
 * @file    : [PlayService]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/8
 * @time    : 21:22
 **/

class PlayService : MediaBrowserServiceCompat(),
    MediaPlayerUtil, NotificationUtil, MediaPlayer.OnCompletionListener, AudioFocusUtil {
    
    companion object {
    
        /**
         * [START_FLAG]
         * @author 1552980358
         * @since 0.1
         **/
        private const val START_FLAG = "START_FLAG"
        /**
         * [STOP_FOREGROUND]
         * @author 1552980358
         * @since 0.1
         **/
        private const val STOP_FOREGROUND = "STOP_FOREGROUND"
        /**
         * [START_FOREGROUND]
         * @author 1552980358
         * @since 0.1
         **/
        private const val START_FOREGROUND = "START_FOREGROUND"
    
        /**
         * [playbackStateActions]
         * @author 1552980358
         * @since 0.1
         **/
        private const val playbackStateActions = (
            ACTION_PLAY_PAUSE
                or ACTION_SEEK_TO
                or ACTION_PLAY_FROM_MEDIA_ID
                or ACTION_SKIP_TO_NEXT
                or ACTION_SKIP_TO_PREVIOUS
                or ACTION_SKIP_TO_QUEUE_ITEM
            )
    
        /**
         * [TAG_WAKE_LOCK]
         * @author 1552980358
         * @since 0.1
         **/
        private const val TAG_WAKE_LOCK = "PlayService:WakeLock"
    
        /**
         * [playHistory]
         * @author 1552980358
         * @since 0.1
         **/
        val playHistory = ArrayList<String>()
    
        /**
         * [currentIndex]
         * @author 1552980358
         * @since 0.1
         **/
        var currentIndex = -1
        
        const val CYCLE_MODE = "CYCLE_MODE"
    }
    
    /**
     * [playbackStateCompat]
     * @author 1552980358
     * @since 0.1
     **/
    private lateinit var playbackStateCompat: PlaybackStateCompat
    /**
     * [mediaMetadataCompat]
     * @author 1552980358
     * @since 0.1
     **/
    private lateinit var mediaMetadataCompat: MediaMetadataCompat
    
    /**
     * [cycleMode]
     * @author 1552980358
     * @since 0.1
     **/
    private var cycleMode = SINGLE_CYCLE
    
    /**
     * [mediaSessionCompat]
     * @author 1552980358
     * @since 0.1
     **/
    private lateinit var mediaSessionCompat: MediaSessionCompat
    /**
     * [mediaSessionCompatCallback]
     * @author 1552980358
     * @since 0.1
     **/
    private lateinit var mediaSessionCompatCallback: MediaSessionCompat.Callback
    /**
     * [mediaItemList]
     * @author 1552980358
     * @since 0.1
     **/
    private var mediaItemList = ArrayList<MediaBrowserCompat.MediaItem>()
    
    /**
     * [mediaPlayer]
     * @author 1552980358
     * @since 0.1
     **/
    private var mediaPlayer = MediaPlayer()
    
    /**
     * [wakeLock]
     * @author 1552980358
     * @since 0.1
     **/
    private var wakeLock: PowerManager.WakeLock? = null
    
    /**
     * [startTime]
     * @author 1552980358
     * @since 0.1
     **/
    private var startTime = 0L
    /**
     * [pauseTime]
     * @author 1552980358
     * @since 0.1
     **/
    private var pauseTime = 0L
    
    /**
     * [notificationManagerCompat]
     * @author 1552980358
     * @since 0.1
     **/
    private lateinit var notificationManagerCompat: NotificationManagerCompat
    
    /**
     * [audioManager]
     * @author 1552980358
     * @since 0.1
     **/
    private lateinit var audioManager: AudioManager
    /**
     * [audioFocusRequest]
     * @author 1552980358
     * @since 0.1
     **/
    private lateinit var audioFocusRequest: AudioFocusRequest
    /**
     * [audioFocusChangeListener]
     * @author 1552980358
     * @since 0.1
     **/
    private lateinit var audioFocusChangeListener: AudioManager.OnAudioFocusChangeListener
    
    private var isForegroundService = false
    
    /**
     * [onCreate]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onCreate() {
        super.onCreate()
        Log.e("PlayService", "onCreate")
        
        // Media Session
        mediaSessionCompatCallback = object : MediaSessionCompat.Callback() {
            
            @Suppress("DuplicatedCode")
            @Synchronized
            override fun onPlay() {
                Log.e("MediaSessionCompat", "onPlay")
                gainAudioFocusRequest(audioManager, audioFocusRequest, audioFocusChangeListener)
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
                    
                    if (isForegroundService) {
                        notificationManagerCompat.notify(ServiceId, getNotification())
                        return
                    }
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
                        .addCustomAction(CYCLE_MODE, cycleMode.name, R.drawable.ic_launcher_foreground)
                        .setState(STATE_PLAYING, 0L, 1F)
                        .build()
                    startTime = System.currentTimeMillis()
                    onPlay(mediaPlayer)
                    mediaSessionCompat.setPlaybackState(playbackStateCompat)
        
                    if (isForegroundService) {
                        notificationManagerCompat.notify(ServiceId, getNotification())
                        return
                    }
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
                        .addCustomAction(CYCLE_MODE, cycleMode.name, R.drawable.ic_launcher_foreground)
                        .setState(STATE_PLAYING, 0L, 1F)
                        .build()
                    mediaSessionCompat.setPlaybackState(playbackStateCompat)
    
                    if (isForegroundService) {
                        notificationManagerCompat.notify(ServiceId, getNotification())
                        return
                    }
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
    
            @Synchronized
            override fun onPause() {
                Log.e("MediaSessionCompat", "onPause")
                abandonAudioFocusRequest(audioManager, audioFocusRequest, audioFocusChangeListener)
                if (playbackStateCompat.state == STATE_PLAYING) {
                    Log.e("playStateCompat.state", "STATE_PLAYING")
                    pauseTime = System.currentTimeMillis()
                    onPause(mediaPlayer)
                    playbackStateCompat = PlaybackStateCompat.Builder()
                        .addCustomAction(CYCLE_MODE, cycleMode.name, R.drawable.ic_launcher_foreground)
                        .setActions(playbackStateActions)
                        .setState(STATE_PAUSED, pauseTime - startTime, 1F)
                        .build()
                    mediaSessionCompat.setPlaybackState(playbackStateCompat)
    
                    if (!isForegroundService) {
                        notificationManagerCompat.notify(ServiceId, getNotification())
                        return
                    }
                    startService(
                        Intent(this@PlayService, PlayService::class.java)
                            .putExtra(START_FLAG, STOP_FOREGROUND)
                    )
    
                    return
                }
            }
    
            @Suppress("DuplicatedCode")
            @Synchronized
            override fun onSkipToPrevious() {
                Log.e("MediaSessionCompat", "onSkipToPrevious")
    
                if (currentIndex == 0) {
                    if (playHistory[0] == audioDataList.first().id) {
                        playHistory.add(0, audioDataList.last().id)
                    } else {
                        Log.e("playHistory", playHistory.first())
                        for ((i, j) in audioDataList.withIndex()) {
                            if (playHistory[0] != j.id) {
                                continue
                            }
                            
                            playHistory.add(0, audioDataList[i - 1].id)
                            break
                        }
                        
                        //playHistory.add(0, audioDataList[audioDataList.indexOf(audioDataMap[playHistory[0]]) - 1].id)
                    }
                    playbackStateCompat = PlaybackStateCompat.Builder()
                        .setActions(playbackStateActions)
                        .setState(STATE_BUFFERING, 0L, 1F)
                        .build()
                    mediaSessionCompat.setPlaybackState(playbackStateCompat)
                    mediaMetadataCompat = MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, playHistory[currentIndex])
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, audioDataMap[playHistory[currentIndex]]?.title)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, audioDataMap[playHistory[currentIndex]]?.artist)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, audioDataMap[playHistory[currentIndex]]?.album)
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, audioDataMap[playHistory[currentIndex]]?.duration!!)
                        .build()
                    mediaSessionCompat.setMetadata(mediaMetadataCompat)
                    onPlayFromMediaId(this@PlayService, mediaPlayer, this, playHistory[currentIndex])
                    return
                }
                
                currentIndex--
                playbackStateCompat = PlaybackStateCompat.Builder()
                    .setActions(playbackStateActions)
                    .addCustomAction(CYCLE_MODE, cycleMode.name, R.drawable.ic_launcher_foreground)
                    .setState(STATE_BUFFERING, 0L, 1F)
                    .build()
                mediaSessionCompat.setPlaybackState(playbackStateCompat)
                mediaMetadataCompat = MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, playHistory[currentIndex])
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, audioDataMap[playHistory[currentIndex]]?.title)
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, audioDataMap[playHistory[currentIndex]]?.artist)
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, audioDataMap[playHistory[currentIndex]]?.album)
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, audioDataMap[playHistory[currentIndex]]?.duration!!)
                    .build()
                mediaSessionCompat.setMetadata(mediaMetadataCompat)
                onPlayFromMediaId(this@PlayService, mediaPlayer, this, playHistory[currentIndex])
            }
            
            @Suppress("DuplicatedCode")
            @Synchronized
            override fun onSkipToNext() {
                Log.e("MediaSessionCompat", "onSkipToNext")
    
                if (playHistory.lastIndex != currentIndex) {
                    currentIndex++
                    playbackStateCompat = PlaybackStateCompat.Builder()
                        .setActions(playbackStateActions)
                        .addCustomAction(CYCLE_MODE, cycleMode.name, R.drawable.ic_launcher_foreground)
                        .setState(STATE_BUFFERING, 0L, 1F)
                        .build()
                    mediaSessionCompat.setPlaybackState(playbackStateCompat)
                    mediaMetadataCompat = MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, playHistory[currentIndex])
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, audioDataMap[playHistory[currentIndex]]?.title)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, audioDataMap[playHistory[currentIndex]]?.artist)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, audioDataMap[playHistory[currentIndex]]?.album)
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, audioDataMap[playHistory[currentIndex]]?.duration!!)
                        .build()
                    mediaSessionCompat.setMetadata(mediaMetadataCompat)
                    onPlayFromMediaId(this@PlayService, mediaPlayer, this, playHistory[currentIndex])
                    return
                }
                
                if (cycleMode == RANDOM_ACCESS) {
                    onPlayFromMediaId(audioDataList[(0 .. audioDataList.lastIndex).random()].id, null)
                    return
                }
    
                for ((i, j) in audioDataList.withIndex()) {
                    if (playHistory[currentIndex] != j.id) {
                        continue
                    }
                    
                    onPlayFromMediaId(audioDataList[if (i == audioDataList.lastIndex) 0 else i + 1].id, null)
                    break
                }
                
            }
    
            @Synchronized
            override fun onSeekTo(pos: Long) {
                Log.e("MediaSessionCompat", "onSeekTo")
                
                if (playbackStateCompat.state == STATE_PLAYING) {
                    Log.e("playStateCompat.state", "STATE_PLAYING")
                    startTime -= (pos - System.currentTimeMillis() + startTime)
                    onSeekTo(mediaPlayer, pos)
                    playbackStateCompat = PlaybackStateCompat.Builder()
                        .setState(STATE_PLAYING, pos, 1F)
                        .addCustomAction(CYCLE_MODE, cycleMode.name, R.drawable.ic_launcher_foreground)
                        .setActions(playbackStateActions)
                        .build()
                    mediaSessionCompat.setPlaybackState(playbackStateCompat)
                    notificationManagerCompat.notify(ServiceId, getNotification())
                    return
                }
                
                if (playbackStateCompat.state == STATE_PAUSED) {
                    Log.e("playStateCompat.state", "STATE_PAUSED")
                    pauseTime += (pos - pauseTime + startTime)
                    onSeekTo(mediaPlayer, pos)
                    playbackStateCompat = PlaybackStateCompat.Builder()
                        .setState(STATE_PAUSED, pos, 1F)
                        .addCustomAction(CYCLE_MODE, cycleMode.name, R.drawable.ic_launcher_foreground)
                        .setActions(playbackStateActions)
                        .build()
                    mediaSessionCompat.setPlaybackState(playbackStateCompat)
                    notificationManagerCompat.notify(ServiceId, getNotification())
                }
            }
    
            @Synchronized
            override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
                Log.e("MediaSessionCompat", "onPlayFromMediaId")
    
                playbackStateCompat = PlaybackStateCompat.Builder()
                    .setActions(playbackStateActions)
                    .addCustomAction(CYCLE_MODE, cycleMode.name, R.drawable.ic_launcher_foreground)
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
                notificationManagerCompat.notify(ServiceId, getNotification())
                onPlayFromMediaId(this@PlayService, mediaPlayer, this, mediaId!!)
                currentIndex++
                playHistory.add(mediaId)
            }
            
        }
        mediaSessionCompat = MediaSessionCompat(this, RootId).apply {
            setCallback(mediaSessionCompatCallback)
            setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setActions(playbackStateActions)
                    .addCustomAction(CYCLE_MODE, cycleMode.name, R.drawable.ic_launcher_foreground)
                    .setState(STATE_NONE, 0, 1F)
                    .build()
            )
    
            isActive = true
        }
        sessionToken = mediaSessionCompat.sessionToken
        
        initialMediaPlayer(mediaPlayer, this)
        
        // Notification
        notificationManagerCompat = createNotificationManager(this)
        
        // Audio Focus
        audioManager = getAudioManager(this)
        audioFocusChangeListener = getOnAudioFocusChangeListener(mediaSessionCompatCallback)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = getAudioFocusRequest(audioFocusChangeListener)
        }
        
    }
    
    /**
     * [onLoadChildren]
     * @param parentId [String]
     * @param result [Result]<[MutableList]<[MediaBrowserCompat.MediaItem]>>
     * @author 1552980358
     * @since 0.1
     **/
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
            START_FOREGROUND -> {
                startForeground(this, getNotification())
                isForegroundService = true
                /**
                 * // Call to create wake lock prevent stop playing music
                 * // 防止停止播放音乐而创建唤醒锁
                 * mediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK)
                 **/
                // Not to create wake lock through setWakeMode() provided by MediaPlayer
                // which would cause restart of MainActivity and mediaControllerCompat uninitialized，
                // and throw an uninitialized exception when released by following method
                // 不使用由MediaPlayer提供创建唤醒锁的setWakeMode()方法, 否则会导致MainActivity重置
                // 使 mediaControllerCompat 未赋值, 从而抛出异常
                if (wakeLock == null) {
                    wakeLock = (getSystemService(Service.POWER_SERVICE) as PowerManager).newWakeLock(PARTIAL_WAKE_LOCK, TAG_WAKE_LOCK)
                }
                if (!wakeLock!!.isHeld) {
                    @Suppress("WakelockTimeout")
                    wakeLock!!.acquire()
                }
            }
            STOP_FOREGROUND -> {
                stopForeground(false)
                isForegroundService = false
                /**
                 * // Reflect to private mWakeLock variable of PowerManager.WakeLock instance
                 * //from MediaPlayer to release wake lock and reset it
                 * // 反射获取MediaPlayer储存在私有变量mWakeLock的PowerManager.WakeLock对象,
                 * // 从而释放唤醒锁, 并且重置
                 * PlayService::class.java.getField("mWakeLock").apply {
                 *     (get(mediaPlayer) as PowerManager.WakeLock).release()
                 *     set(mediaPlayer, null)
                 * }
                 * PlayService::class.java.getField("washeld").setBoolean(mediaPlayer, true)
                 **/
                // Release wake lock
                // 释放唤醒锁
                //wakeLock?.release()
                //wakeLock = null
            }
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
    
    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }
    
    override fun onCompletion(mp: MediaPlayer?) {
        when (cycleMode) {
            SINGLE_CYCLE -> {
                mediaSessionCompatCallback.onPlay()
            }
            LIST_CYCLE, RANDOM_ACCESS -> {
                mediaSessionCompatCallback.onSkipToNext()
            }
        }
    }
    
}