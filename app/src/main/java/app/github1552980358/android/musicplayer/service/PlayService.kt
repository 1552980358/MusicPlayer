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
import android.support.v4.media.MediaDescriptionCompat
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
import androidx.media.MediaBrowserServiceCompat
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.base.AudioData
import app.github1552980358.android.musicplayer.base.Constant.Companion.AlbumNormalDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.AudioDataDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.AudioDataListFile
import app.github1552980358.android.musicplayer.base.Constant.Companion.CurrentSongList
import app.github1552980358.android.musicplayer.base.Constant.Companion.FULL_LIST
import app.github1552980358.android.musicplayer.base.Constant.Companion.INITIALIZE
import app.github1552980358.android.musicplayer.base.Constant.Companion.INITIALIZE_EXTRA
import app.github1552980358.android.musicplayer.base.Constant.Companion.RootId
import app.github1552980358.android.musicplayer.base.Constant.Companion.SongListDir
import app.github1552980358.android.musicplayer.base.SongList
import app.github1552980358.android.musicplayer.service.CycleMode.LIST_CYCLE
import app.github1552980358.android.musicplayer.service.CycleMode.RANDOM_ACCESS
import app.github1552980358.android.musicplayer.service.CycleMode.SINGLE_CYCLE
import app.github1552980358.android.musicplayer.service.NotificationUtil.Companion.ServiceId
import java.io.File
import java.io.ObjectInputStream

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
        const val START_FLAG = "START_FLAG"
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
         * [CYCLE_MODE]
         * @author 1552980358
         * @since 0.1
         **/
        const val CYCLE_MODE = "CYCLE_MODE"
        
    }
    
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
    
    /**
     * [isForegroundService]
     * @author 1552980358
     * @since 0.1
     **/
    private var isForegroundService = false
    
    private var currentSongList = FULL_LIST
    
    private var songList = arrayListOf<AudioData>()
    
    private lateinit var audioDataMap: MutableMap<String, AudioData>
    
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
    
            /**
             * [onPlay]
             * @author 1552980358
             * @since 0.1
             **/
            @Suppress("DuplicatedCode")
            @Synchronized
            override fun onPlay() {
                Log.e("MediaSessionCompat", "onPlay")
                gainAudioFocusRequest(audioManager, audioFocusRequest, audioFocusChangeListener)
                if (playbackStateCompat.state == STATE_PAUSED) {
                    Log.e("playStateCompat.state", "STATE_PAUSED")
                    playbackStateCompat = PlaybackStateCompat.Builder()
                        .setActions(playbackStateActions)
                        .addCustomAction(cycleMode.name, CYCLE_MODE, R.drawable.ic_launcher_foreground)
                        .addCustomAction(currentSongList, CurrentSongList, R.drawable.ic_launcher_foreground)
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
                        .addCustomAction(cycleMode.name, CYCLE_MODE, R.drawable.ic_launcher_foreground)
                        .addCustomAction(currentSongList, CurrentSongList, R.drawable.ic_launcher_foreground)
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
                        .addCustomAction(cycleMode.name, CYCLE_MODE, R.drawable.ic_launcher_foreground)
                        .addCustomAction(currentSongList, CurrentSongList, R.drawable.ic_launcher_foreground)
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
    
            /**
             * [onPause]
             * @author 1552980358
             * @since 0.1
             **/
            @Synchronized
            override fun onPause() {
                Log.e("MediaSessionCompat", "onPause")
                abandonAudioFocusRequest(audioManager, audioFocusRequest, audioFocusChangeListener)
                if (playbackStateCompat.state == STATE_PLAYING) {
                    Log.e("playStateCompat.state", "STATE_PLAYING")
                    pauseTime = System.currentTimeMillis()
                    onPause(mediaPlayer)
                    playbackStateCompat = PlaybackStateCompat.Builder()
                        .addCustomAction(cycleMode.name, CYCLE_MODE, R.drawable.ic_launcher_foreground)
                        .addCustomAction(currentSongList, CurrentSongList, R.drawable.ic_launcher_foreground)
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
    
            /**
             * [onSkipToPrevious]
             * @author 1552980358
             * @since 0.1
             **/
            @Suppress("DuplicatedCode")
            @Synchronized
            override fun onSkipToPrevious() {
                Log.e("MediaSessionCompat", "onSkipToPrevious")
    
                if (currentIndex == 0) {
                    if (playHistory[0] == songList.first().id) {
                        playHistory.add(0, songList.last().id)
                    } else {
                        for ((i, j) in songList.withIndex()) {
                            if (playHistory[0] != j.id) {
                                continue
                            }
                            
                            playHistory.add(0, songList[i - 1].id)
                            break
                        }
                        
                    }
                    playbackStateCompat = PlaybackStateCompat.Builder()
                        .setActions(playbackStateActions)
                        .addCustomAction(cycleMode.name, CYCLE_MODE, R.drawable.ic_launcher_foreground)
                        .addCustomAction(currentSongList, CurrentSongList, R.drawable.ic_launcher_foreground)
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
    
            /**
             * [onSkipToNext]
             * @author 1552980358
             * @since 0.1
             **/
            @Suppress("DuplicatedCode")
            @Synchronized
            override fun onSkipToNext() {
                Log.e("MediaSessionCompat", "onSkipToNext")

                if (playHistory.lastIndex != currentIndex) {
                    currentIndex++
                    playbackStateCompat = PlaybackStateCompat.Builder()
                        .setActions(playbackStateActions)
                        .addCustomAction(cycleMode.name, CYCLE_MODE, R.drawable.ic_launcher_foreground)
                        .addCustomAction(currentSongList, CurrentSongList, R.drawable.ic_launcher_foreground)
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
                    onPlayFromMediaId(songList[(0 .. songList.lastIndex).random()].id, null)
                    return
                }

                for ((i, j) in songList.withIndex()) {
                    if (playHistory[currentIndex] != j.id) {
                        continue
                    }

                    onPlayFromMediaId(songList[if (i == songList.lastIndex) 0 else i + 1].id, null)
                    break
                }

            }
    
            /**
             * [onSeekTo]
             * @author 1552980358
             * @since 0.1
             **/
            @Synchronized
            override fun onSeekTo(pos: Long) {
                Log.e("MediaSessionCompat", "onSeekTo")

                if (playbackStateCompat.state == STATE_PLAYING) {
                    Log.e("playStateCompat.state", "STATE_PLAYING")
                    startTime -= (pos - System.currentTimeMillis() + startTime)
                    onSeekTo(mediaPlayer, pos)
                    playbackStateCompat = PlaybackStateCompat.Builder()
                        .setState(STATE_PLAYING, pos, 1F)
                        .addCustomAction(cycleMode.name, CYCLE_MODE, R.drawable.ic_launcher_foreground)
                        .addCustomAction(currentSongList, CurrentSongList, R.drawable.ic_launcher_foreground)
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
    
            var newList = false
            /**
             * [onPlayFromMediaId]
             * @author 1552980358
             * @since 0.1
             **/
            @Synchronized
            override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
                
                Log.e("MediaSessionCompat", "onPlayFromMediaId")
                if (currentSongList != extras?.getString(CurrentSongList)!!) {
                    playHistory.clear()
                    currentIndex = -1
                    currentSongList = extras.getString(CurrentSongList)!!
                    newList = true
                }
                
                playbackStateCompat = PlaybackStateCompat.Builder()
                    .setActions(playbackStateActions)
                    .addCustomAction(cycleMode.name, CYCLE_MODE, R.drawable.ic_launcher_foreground)
                    .addCustomAction(extras.getString(CurrentSongList), CurrentSongList, R.drawable.ic_launcher_foreground)
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
                
                if (newList) {
                    songList.clear()
                    if (currentSongList == FULL_LIST) {
                       File(getExternalFilesDir(AudioDataDir), AudioDataListFile).apply {
                           if (!exists()) {
                               return@apply
                           }
                           
                           inputStream().use { `is` ->
                               ObjectInputStream(`is`).use { ois ->
                                   @Suppress("UNCHECKED_CAST")
                                   songList = (ois.readObject() as ArrayList<AudioData>)
                               }
                           }
                       }
                    } else {
                        File(getExternalFilesDir(SongListDir), currentSongList).apply {
                            inputStream().use { `is` ->
                                ObjectInputStream(`is`).use { ois ->
                                    songList = (ois.readObject() as SongList).audioList
                                }
                            }
                        }
                    }
                    newList = false
                }
                
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
        
        mediaItemList.clear()
        
        songList.forEach { audioData ->
            mediaItemList.add(
                MediaBrowserCompat.MediaItem(
                    MediaDescriptionCompat.Builder()
                        .setTitle(audioData.title)
                        .setSubtitle(audioData.artist)
                        .setMediaId(audioData.id)
                        .build(),
                    MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                )
            )
        }
        
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
    @Suppress("DuplicatedCode")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("PlayService", "onStartCommand")
        when (intent?.getStringExtra(START_FLAG)) {
            
            INITIALIZE -> {
                @Suppress("UNCHECKED_CAST")
                audioDataMap = (intent.getSerializableExtra(INITIALIZE_EXTRA) as MutableMap<String, AudioData>)
            }
            
            START_FOREGROUND -> {
                startForeground(this, getNotification())
                isForegroundService = true
                /**
                 * // Call to create wake lock prevent stop playing music
                 * // 防止停止播放音乐而创建唤醒锁
                 * mediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK)
                 **/
                // Not to use setWakeMode() method, that can't release wake lock individually
                // Reflection is also not applied, which is not so efficient as using PowerManager
                // 不使用setWakeMode()方法, 因为其不能独立释放唤醒锁
                // 反射也不会使用, 因为其效率不如使用PowerManager的唤醒锁
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
                wakeLock?.release()
                wakeLock = null
            }
            
            LIST_CYCLE.name -> {
                cycleMode = LIST_CYCLE
                playbackStateCompat = PlaybackStateCompat.Builder()
                    .setActions(playbackStateActions)
                    .addCustomAction(CYCLE_MODE, cycleMode.name, R.drawable.ic_launcher_foreground)
                    .setState(playbackStateCompat.state, when (playbackStateCompat.state) {
                        STATE_PLAYING -> { System.currentTimeMillis() - startTime }
                        STATE_PAUSED -> { pauseTime - startTime }
                        STATE_BUFFERING -> { 0L }
                        else -> { 0L } }, 1F)
                    .build()
                mediaSessionCompat.setPlaybackState(playbackStateCompat)
            }
            
            RANDOM_ACCESS.name -> {
                cycleMode = RANDOM_ACCESS
                playbackStateCompat = PlaybackStateCompat.Builder()
                    .setActions(playbackStateActions)
                    .addCustomAction(CYCLE_MODE, cycleMode.name, R.drawable.ic_launcher_foreground)
                    .setState(playbackStateCompat.state, when (playbackStateCompat.state) {
                        STATE_PLAYING -> { System.currentTimeMillis() - startTime }
                        STATE_PAUSED -> { pauseTime - startTime }
                        STATE_BUFFERING -> { 0L }
                        else -> { 0L } }, 1F)
                    .build()
                mediaSessionCompat.setPlaybackState(playbackStateCompat)
            }
            
            SINGLE_CYCLE.name -> {
                cycleMode = SINGLE_CYCLE
                playbackStateCompat = PlaybackStateCompat.Builder()
                    .setActions(playbackStateActions)
                    .addCustomAction(CYCLE_MODE, cycleMode.name, R.drawable.ic_launcher_foreground)
                    .setState(playbackStateCompat.state, when (playbackStateCompat.state) {
                        STATE_BUFFERING -> { 0L }
                        STATE_PLAYING -> { System.currentTimeMillis() - startTime }
                        STATE_PAUSED -> { pauseTime - startTime }
                        else -> { 0L } }, 1F)
                    .build()
                mediaSessionCompat.setPlaybackState(playbackStateCompat)
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
                File(getExternalFilesDir(AlbumNormalDir), mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)).apply {
                    if (exists()) {
                        inputStream().use { `is` ->
                            setLargeIcon(BitmapFactory.decodeStream(`is`))
                        }
                    } else {
                        setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_foreground))
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
    
    /**
     * [onDestroy]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onDestroy() {
        mediaPlayer.release()
        try {
            wakeLock?.release()
        } catch (e: Exception) {
            //e.printStackTrace()
        }
        super.onDestroy()
    }
    
    /**
     * [onCompletion]
     * @param mp [MediaPlayer]?
     * @author 1552980358
     * @since 0.1
     **/
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