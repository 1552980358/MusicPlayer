package app.fokkusu.music.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import app.fokkusu.music.base.Constants
import app.fokkusu.music.base.Constants.Companion.PlayServiceChannelId
import app.fokkusu.music.base.Constants.Companion.PlayServiceId
import app.fokkusu.music.base.Constants.Companion.USER_BROADCAST_MUSIC_LAST
import app.fokkusu.music.base.Constants.Companion.USER_BROADCAST_MUSIC_NEXT
import app.fokkusu.music.base.Constants.Companion.USER_BROADCAST_MUSIC_SELECTED
import app.fokkusu.music.base.Constants.Companion.USER_BROADCAST_PAUSE
import app.fokkusu.music.R
import app.fokkusu.music.base.Constants.Companion.BROADCAST_EXTRA_MUSIC_INDEX
import app.fokkusu.music.base.Constants.Companion.BROADCAST_EXTRA_MUSIC_SOURCE
import app.fokkusu.music.base.Constants.Companion.ERROR_CODE_INT
import app.fokkusu.music.base.Constants.Companion.MUSIC_LIST
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_CHANGE
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_CHANGE_SOURCE
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_CHANGE_SOURCE_LOC
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_CONTENT
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_PLAY
import app.fokkusu.music.base.Constants.Companion.USER_BROADCAST_PLAY
import app.fokkusu.music.base.MusicUtil

/**
 * @File    : PlayService
 * @Author  : 1552980358
 * @Date    : 4 Oct 2019
 * @TIME    : 6:23 PM
 **/

class PlayService : Service() {
    
    companion object {
        enum class PlayState {
            STOP, PLAY, PAUSE
        }
        
        enum class PlayForm {
            CYCLE, SINGLE, RANDOM
        }
        
        var playerState = PlayState.STOP
            private set
        
        /* Single Instance */
        private var playService = null as PlayService?
            get() = field as PlayService
        
        @Synchronized
        fun addMusic(music: MusicUtil) = musicList.add(music)
        @Synchronized
        fun addMusic(path: String, title: String, artist: String?, album: String?, duration: Int) = addMusic(MusicUtil(path, title, artist, album, duration))
        
        @Synchronized
        fun sortMusic() {
            musicList.sortBy { it.titlePY() }
        }
        
        val musicList = mutableListOf<MusicUtil>()
        
        fun getCurrentPosition(): Int {
            /* Playing State */
            if (playerState == PlayState.PLAY) {
                return playService!!.mediaPlayer.currentPosition
            }
            
            /* Pause or Stop */
            playService!!.apply {
                pauseSeek.apply {
                    if (this != -1) return this
                }
                pauseLoc.apply {
                    if (this != -1) return this
                }
                return 0
            }
        }
        
        fun getCurrentMusicInfo() = playService!!.playList[playService!!.musicLoc]
        fun getCurrentMusic() = playService!!.musicLoc
        fun getPlayList() = playService!!.playList
    }
    
    private val broadCastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent ?: return
                
                when (intent.action) {
                    USER_BROADCAST_PAUSE -> {
                        pause()
                    }
                    
                    USER_BROADCAST_MUSIC_LAST -> {
                        last()
                    }
                    
                    USER_BROADCAST_MUSIC_NEXT -> {
                        next()
                    }
                    
                    USER_BROADCAST_MUSIC_SELECTED -> {
                        setChange(
                            intent.getIntExtra(
                                BROADCAST_EXTRA_MUSIC_SOURCE, ERROR_CODE_INT
                            ), intent.getIntExtra(
                                BROADCAST_EXTRA_MUSIC_INDEX, ERROR_CODE_INT
                            )
                        )
                    }
                }
            }
        }
    }
    
    /* MediaPlayer */
    private val mediaPlayer by lazy { MediaPlayer() }
    private var pauseLoc = -1       // Paused Loc
    private var pauseSeek = -1      // Seek change when pause
    private val playList = mutableListOf<MusicUtil>()
    private var musicLoc = -1     // Pointer
    
    private var playForm = PlayForm.CYCLE
    
    /* Notification */
    private lateinit var notificationManagerCompat: NotificationManagerCompat
    private lateinit var notificationCompat: NotificationCompat.Builder
    
    /* onCreate */
    override fun onCreate() {
        super.onCreate()
        
        /* Initialize all objects */
        playService = this
        mediaPlayer
        /* Set up notification channel */
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            NotificationChannel(
                PlayServiceChannelId, Constants.PlayService, NotificationManager.IMPORTANCE_HIGH
            ).apply {
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                    this
                )
            }
        }
        
        notificationManagerCompat = NotificationManagerCompat.from(this)
        
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadCastReceiver, IntentFilter().apply {
                addAction(USER_BROADCAST_PAUSE)
                addAction(USER_BROADCAST_MUSIC_LAST)
                addAction(USER_BROADCAST_MUSIC_NEXT)
                addAction(USER_BROADCAST_MUSIC_SELECTED)
            })
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(PlayServiceId, getNotification())
        
        if (intent == null) {
            stopForeground(false)
            updateNotify()
            return super.onStartCommand(intent, flags, startId)
        }
        
        when (intent.getStringExtra(SERVICE_INTENT_CONTENT)) {
            SERVICE_INTENT_PLAY -> {
                play()
            }
            
            SERVICE_INTENT_CHANGE -> {
                setChange(
                    intent.getIntExtra(SERVICE_INTENT_CHANGE_SOURCE, ERROR_CODE_INT),
                    intent.getIntExtra(SERVICE_INTENT_CHANGE_SOURCE_LOC, ERROR_CODE_INT)
                )
            }
            
            else -> {
                stopForeground(false)
                updateNotify()
                return super.onStartCommand(intent, flags, startId)
            }
        }
        
        updateNotify()
        return super.onStartCommand(intent, flags, startId)
    }
    
    @Synchronized
    private fun play() {
        try {
            if (playerState == PlayState.PLAY) return
            
            if (playerState == PlayState.STOP) {
                if (playList.size == 0) {
                    playList.add(musicList.first())
                    musicLoc = 0
                }
                mediaPlayer.setDataSource(playList.first().path())
                mediaPlayer.prepare()
            }
            
            mediaPlayer.start()
            playerState = PlayState.PLAY
            
            if (pauseSeek != -1) {
                mediaPlayer.seekTo(pauseSeek)
                pauseSeek = -1
                pauseLoc = -1
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    @Synchronized
    private fun pause() {
        if (playerState == PlayState.PAUSE) return
        
        if (playerState == PlayState.PLAY) {
            pauseLoc = mediaPlayer.currentPosition
            mediaPlayer.pause()
        }
        
        playerState = PlayState.PAUSE
    }
    
    @Synchronized
    private fun seek(loc: Int) {
        if (playerState == PlayState.PLAY) {
            mediaPlayer.seekTo(loc)
            return
        }
        pauseSeek = loc
    }
    
    @Synchronized
    private fun next() {
        if (playList.lastIndex == musicLoc) {
            when (playForm) {
                PlayForm.CYCLE, PlayForm.SINGLE -> {
                    /* Do last index of musicList prevent index overflow */
                    if (playList.last().loc == musicList.lastIndex) {
                        playList.add(musicList.first())
                        musicLoc++
                        
                        updateMusic()
                        return
                    }
                    
                    playList.add(musicList[playList.last().loc + 1])
                    musicLoc++
                    updateMusic()
                    return
                }
                
                PlayForm.RANDOM -> {
                    /* With in Limited Range */
                    playList.add(musicList[(0..musicList.lastIndex).random()])
                    musicLoc++
                    
                    updateMusic()
                    return
                }
            }
        }
        
        musicLoc++
        updateMusic()
    }
    
    @Synchronized
    private fun last() {
        /* Prevent overflow */
        if (musicLoc == 0) {
            musicLoc = playList.lastIndex
            updateMusic()
            return
        }
        
        musicLoc--
        updateMusic()
    }
    
    @Synchronized
    private fun setChange(source: Int, loc: Int) {
        if (source == ERROR_CODE_INT || loc == ERROR_CODE_INT) return
        
        if (source == MUSIC_LIST) {
            playList.add(musicList[loc])
            musicLoc++
            updateMusic()
            return
        }
        
        musicLoc = loc
        updateMusic()
    }
    
    @Synchronized
    private fun updateMusic() {
        try {
            /*  Remove all */
            mediaPlayer.reset()
            
            mediaPlayer.setDataSource(musicList[musicLoc].path())
            mediaPlayer.prepare()
            mediaPlayer.start()
            playerState = PlayState.PLAY
            
            pauseLoc = -1
            pauseSeek = -1
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun updateNotify() {
        notificationManagerCompat.notify(PlayServiceId, getNotification())
    }
    
    override fun onDestroy() {
        try {
            unregisterReceiver(broadCastReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            notificationManagerCompat.cancelAll()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }
    
    /* onBind */
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    private fun getNotification(): Notification {
        return NotificationCompat.Builder(this, PlayServiceChannelId).apply {
            notificationCompat = this
            
            musicList[musicLoc].apply {
                setContentTitle(title())                                    // Title
                setContentText(artist().plus(" - ").plus(album()))    // Artist + Album
                albumCover().apply {
                    // Album Cover
                    if (this != null) setLargeIcon(this)
                }
            }
            
            setSmallIcon(R.mipmap.ic_launcher_round)
            
            priority = NotificationCompat.PRIORITY_MAX
            
            addAction(
                R.drawable.ic_noti_last, USER_BROADCAST_MUSIC_LAST, PendingIntent.getBroadcast(
                    this@PlayService, 0, Intent(USER_BROADCAST_MUSIC_LAST), 0
                )
            )
            
            /* Add control Button */
            when (playerState) {
                PlayState.STOP, PlayState.PAUSE -> {
                    addAction(
                        R.drawable.ic_noti_last,
                        USER_BROADCAST_PLAY,
                        PendingIntent.getForegroundService(
                            this@PlayService, 0, Intent(
                                this@PlayService, PlayService::class.java
                            ), 0
                        )
                    )
                }
                
                PlayState.PLAY -> {
                    addAction(
                        R.drawable.ic_noti_next, USER_BROADCAST_PAUSE, PendingIntent.getBroadcast(
                            this@PlayService, 0, Intent(USER_BROADCAST_PAUSE), 0
                        )
                    )
                }
            }
            
            addAction(
                R.drawable.ic_noti_next, USER_BROADCAST_MUSIC_NEXT, PendingIntent.getBroadcast(
                    this@PlayService, 0, Intent(USER_BROADCAST_MUSIC_NEXT), 0
                )
            )
            
        }.build().apply {
            /* Garbage clean */
            System.gc()
        }
    }
}