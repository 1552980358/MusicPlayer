@file:Suppress("PrivatePropertyName")

package app.fokkusu.music.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import app.fokkusu.music.R
import app.fokkusu.music.base.Constants
import app.fokkusu.music.base.Constants.Companion.PlayServiceChannelId
import app.fokkusu.music.base.Constants.Companion.PlayServiceId
import app.fokkusu.music.base.Constants.Companion.USER_BROADCAST_PAUSE
import app.fokkusu.music.base.Constants.Companion.BROADCAST_EXTRA_MUSIC_INDEX
import app.fokkusu.music.base.Constants.Companion.BROADCAST_EXTRA_MUSIC_SOURCE
import app.fokkusu.music.base.Constants.Companion.ERROR_CODE_INT
import app.fokkusu.music.base.Constants.Companion.GamePackageName
import app.fokkusu.music.base.Constants.Companion.MUSIC_LIST
import app.fokkusu.music.base.Constants.Companion.PlayService
import app.fokkusu.music.base.Constants.Companion.SERVICE_BROADCAST_CHANGED
import app.fokkusu.music.base.Constants.Companion.SERVICE_BROADCAST_PAUSE
import app.fokkusu.music.base.Constants.Companion.SERVICE_BROADCAST_PLAY
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_CHANGE
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_CHANGE_SOURCE
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_CHANGE_SOURCE_LOC
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_CONTENT
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_INIT
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_LAST
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_NEXT
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_PAUSE
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_PLAY
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_PLAY_FORM
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_PLAY_FORM_CONTENT
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_SEEK_CHANGE
import app.fokkusu.music.base.Constants.Companion.SERVICE_INTENT_SEEK_CHANGE_POSITION
import app.fokkusu.music.base.Constants.Companion.USER_BROADCAST_LAST
import app.fokkusu.music.base.Constants.Companion.USER_BROADCAST_NEXT
import app.fokkusu.music.base.Constants.Companion.USER_BROADCAST_PLAY
import app.fokkusu.music.base.Constants.Companion.USER_BROADCAST_SELECTED
import app.fokkusu.music.base.MusicUtil
import app.fokkusu.music.base.getStack
import app.fokkusu.music.base.interfaces.OnRequestAlbumCoverListener
import app.fokkusu.music.base.makeToast
import app.fokkusu.music.fragment.main.SettingFragment
import java.io.File
import java.io.Serializable

/**
 * @File    : PlayService
 * @Author  : 1552980358
 * @Date    : 4 Oct 2019
 * @TIME    : 6:23 PM
 **/

@SuppressLint("PrivatePropertyName", "NewApi")
class PlayService : Service(), OnRequestAlbumCoverListener {
    
    companion object {
        /* Play state */
        enum class PlayState {
            STOP, PLAY, PAUSE
        }
        
        var playerState = PlayState.STOP
            private set
        
        /* Play form */
        enum class PlayForm : Serializable {
            CYCLE, SINGLE, RANDOM
        }
        
        var playForm = PlayForm.CYCLE
            private set
        
        /* Single Instance */
        private var playService = null as PlayService?
            get() = field!!
        private var init = false
        
        /* Add music to music list */
        @Synchronized
        fun addMusic(music: MusicUtil) = musicList.add(music)
        
        @Synchronized
        fun addMusic(
            path: String, id: String, title: String, artist: String?, album: String?, duration: Int
        ) = addMusic(MusicUtil(path, id, title, artist, album, duration))
        
        /* Music list */
        val musicList = mutableListOf<MusicUtil>()
        
        /* Sorting music arrangement */
        @Synchronized
        fun sortMusic() {
            musicList.sortBy { it.titlePY() }
        }
        
        /* Assign location of music in music list */
        @Synchronized
        fun assignLoc() {
            for (i in 0..musicList.lastIndex) {
                musicList[i].loc = i
            }
        }
        
        /* Get current music playing position */
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
        
        /* Get current music duration */
        fun getMusicDuration(): Int {
            return if (!init || playService!!.musicLoc == -1) {
                0
            } else {
                playService!!.mediaPlayer.duration
            }
        }
        
        /* Get current music information */
        fun getCurrentMusicInfo(): MusicUtil? {
            if (!init || playService!!.musicLoc == -1) return null
            
            return playService!!.playList[playService!!.musicLoc]
        }
        
        /* Get current music index
        fun getCurrentMusic(): Int {
            if (!init) {
                return -1
            }
            return playService!!.musicLoc
        }
        */
        
        /* Get play list */
        fun getPlayList(): MutableList<MusicUtil>? {
            if (!init) return null
            
            return playService!!.playList
        }
        
    }
    
    private var currentBitmap: Bitmap? = null
    
    /* Broadcast receiver */
    private val broadCastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent ?: return
                
                when (intent.action) {
                    USER_BROADCAST_PLAY -> {
                        play()
                    }
                    
                    USER_BROADCAST_PAUSE -> {
                        gradualPause()
                    }
                    
                    USER_BROADCAST_LAST -> {
                        last()
                    }
                    
                    USER_BROADCAST_NEXT -> {
                        next()
                    }
                    
                    USER_BROADCAST_SELECTED -> {
                        setChange(
                            intent.getIntExtra(
                                BROADCAST_EXTRA_MUSIC_SOURCE, ERROR_CODE_INT
                            ), intent.getIntExtra(
                                BROADCAST_EXTRA_MUSIC_INDEX, ERROR_CODE_INT
                            )
                        )
                    }
                }
                updateNotify()
            }
        }
    }
    
    /* MediaPlayer */
    private val mediaPlayer by lazy { MediaPlayer() }
    private var pauseLoc = -1       // Paused Loc
    private var pauseSeek = -1      // Seek change when pause
    private val playList = mutableListOf<MusicUtil>()
    private var musicLoc = -1     // Pointer
    
    /* Notification */
    private lateinit var notificationManagerCompat: NotificationManagerCompat
    private lateinit var notificationCompat: NotificationCompat.Builder
    
    /* playbackStateCompat */
    private val playbackStateCompat by lazy {
        PlaybackStateCompat.Builder()
            //.setBufferedPosition(0)
            .setState(PlaybackStateCompat.STATE_PAUSED, 0, 1F)
            .setActions(PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_PAUSE
                    or PlaybackStateCompat.ACTION_PLAY_PAUSE
                    or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                    or PlaybackStateCompat.ACTION_STOP
                    or PlaybackStateCompat.ACTION_SEEK_TO)
    }
    
    /* mediaMetadataCompat */
    private val mediaMetadataCompat by lazy {
        MediaMetadataCompat.Builder().putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
    }
    
    /* mediaSessionCompat */
    private val mediaSessionCompat by lazy {
        MediaSessionCompat(this, PlayService).apply {
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    play()
                }
                
                override fun onPause() {
                    pause()
                }
                
                override fun onSeekTo(pos: Long) {
                    seek(pos.toInt())
                    updateNotify(currentBitmap, true)
                }
                
                override fun onSkipToNext() {
                    next()
                }
                
                override fun onSkipToPrevious() {
                    last()
                }
            })
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
            isActive = true
            setPlaybackState(playbackStateCompat.build())
            setMetadata(mediaMetadataCompat.build())
        }
    }

    /* Notification style */
    private val notificationStyle by lazy {
        androidx.media.app.NotificationCompat.MediaStyle()
            .setShowActionsInCompactView(0, 1, 2)
            .setShowCancelButton(true)
            .setMediaSession(mediaSessionCompat/*MediaSessionCompat(this@PlayService, PlayService)*/.sessionToken)
    }
    
    /* Notification actions */
    private val notificationAction_last by lazy {
        NotificationCompat.Action(
            R.drawable.ic_noti_last, USER_BROADCAST_LAST, PendingIntent.getBroadcast(
                this@PlayService, 0, Intent(USER_BROADCAST_LAST), 0
            )
        )
    }
    private val notificationAction_next by lazy {
        NotificationCompat.Action(
            R.drawable.ic_noti_next, USER_BROADCAST_NEXT, PendingIntent.getBroadcast(
                this@PlayService, 0, Intent(USER_BROADCAST_NEXT), 0
            )
        )
    }
    private val notificationAction_pause by lazy {
        NotificationCompat.Action(
            R.drawable.ic_noti_pause, USER_BROADCAST_PAUSE, PendingIntent.getBroadcast(
                this@PlayService, 0, Intent(USER_BROADCAST_PAUSE), 0
            )
        )
    }
    private val notificationAction_play by lazy {
        NotificationCompat.Action(
            R.drawable.ic_noti_play, USER_BROADCAST_PLAY, PendingIntent.getBroadcast(
                this@PlayService, 0, Intent(USER_BROADCAST_PLAY), 0
            )
        )
    }
    
    private val audioManager by lazy { getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    
    /* For API 26+ */
    private val audioFocusRequest by lazy {
        AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)                  // Gain Usage
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)     // Content type
                    .build()
            )
            .setWillPauseWhenDucked(true)
            //.setAcceptsDelayedFocusGain(true)                           // Two sounds playing at the same time
            .setOnAudioFocusChangeListener(onAudioFocusChangeListener)  // onAudioFocusChangeListener
            .build()
    }
    
    /* onAudioFocusChangeListener */
    private val onAudioFocusChangeListener by lazy {
        AudioManager.OnAudioFocusChangeListener { status ->
            when (status) {
                /* Stop focus for a while */
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK,
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    pause(false)
                    updateNotify()
                }
                /* Used to stop playing */
                AudioManager.AUDIOFOCUS_LOSS -> {
                    // Check if action required
                    if (SettingFragment.switchSave[Constants.SP_Play_Disrupt] != null
                        && SettingFragment.switchSave[Constants.SP_Play_Disrupt] as Boolean
                    ) {
                        (getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager)
                            .queryUsageStats(
                                UsageStatsManager.INTERVAL_BEST,
                                System.currentTimeMillis() - 2000,
                                System.currentTimeMillis()
                            ).apply {
                                if (isNullOrEmpty()) {
                                    // No Permission
                                    return@apply
                                }
                                for (i in this) {
                                    if (GamePackageName.contains(i.packageName)) {
                                        gainAudioFocusRequest()
                                        return@OnAudioFocusChangeListener
                                    }
                                }
                            }
                    }
                    // Remove focus
                    pause()     // Hard pause
                    updateNotify()
                }
                /* Gain back focus */
                AudioManager.AUDIOFOCUS_GAIN -> {
                    // Not to gain again
                    // just play immediately
                    play(false)
                    updateNotify()
                }
            }
        }
    }
    
    /* abundantAudioFocusRequest */
    private fun abundantAudioFocusRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.abandonAudioFocusRequest(audioFocusRequest)
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(onAudioFocusChangeListener)
        }
    }
    
    /* gainAudioFocusRequest */
    private fun gainAudioFocusRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.requestAudioFocus(audioFocusRequest)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                onAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
    }
    
    /* onCreate */
    override fun onCreate() {
        super.onCreate()
        
        /* Initialize all objects */
        playService = this
        init = true
        mediaPlayer.setOnCompletionListener {
            when (playForm) {
                PlayForm.RANDOM, PlayForm.CYCLE -> {
                    next()
                    updateNotify()
                }
                
                PlayForm.SINGLE -> {
                    if (!mediaPlayer.isLooping) {
                        mediaPlayer.isLooping = true
                        mediaPlayer.start()
                    }
                }
            }
        }
        
        /* Set up notification channel */
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            NotificationChannel(
                PlayServiceChannelId, PlayService, NotificationManager.IMPORTANCE_HIGH
            ).apply {
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                    this
                )
            }
        }
        
        notificationManagerCompat = NotificationManagerCompat.from(this)
        
        registerReceiver(broadCastReceiver, IntentFilter().apply {
            addAction(USER_BROADCAST_PLAY)
            addAction(USER_BROADCAST_PAUSE)
            addAction(USER_BROADCAST_LAST)
            addAction(USER_BROADCAST_NEXT)
            addAction(USER_BROADCAST_SELECTED)
        })
    }
    
    /* onStartCommand */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    
        startForeground(PlayServiceId, getNotification())
        
        if (intent == null) {
            stopForeground(false)
            updateNotify()
            return super.onStartCommand(intent, flags, startId)
        }
        
        when (intent.getStringExtra(SERVICE_INTENT_CONTENT)) {
            SERVICE_INTENT_INIT -> {
                stopForeground(true)
                return super.onStartCommand(intent, START_REDELIVER_INTENT, startId)
            }
            
            SERVICE_INTENT_PLAY -> {
                play()
                updateNotify(currentBitmap, true)
            }
            
            SERVICE_INTENT_PAUSE -> {
                gradualPause()
                updateNotify(currentBitmap, true)
            }
            
            SERVICE_INTENT_LAST -> {
                last()
                updateNotify(null, false)
            }
            
            SERVICE_INTENT_NEXT -> {
                next()
                updateNotify(null, false)
            }
            
            SERVICE_INTENT_CHANGE -> {
                setChange(
                    intent.getIntExtra(SERVICE_INTENT_CHANGE_SOURCE, ERROR_CODE_INT),
                    intent.getIntExtra(SERVICE_INTENT_CHANGE_SOURCE_LOC, ERROR_CODE_INT)
                )
                updateNotify(null, false)
            }
            
            SERVICE_INTENT_SEEK_CHANGE -> {
                seek(intent.getIntExtra(SERVICE_INTENT_SEEK_CHANGE_POSITION, ERROR_CODE_INT))
                updateNotify(currentBitmap, true)
            }
            
            SERVICE_INTENT_PLAY_FORM -> {
                try {
                    playForm =
                        (intent.getSerializableExtra(SERVICE_INTENT_PLAY_FORM_CONTENT) as PlayForm).apply {
                            if (this == PlayForm.SINGLE) {
                                mediaPlayer.isLooping = true
                            }
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            else -> {
                stopForeground(false)
                updateNotify(null, true)
                return super.onStartCommand(intent, flags, startId)
            }
        }
        return super.onStartCommand(intent, START_REDELIVER_INTENT, startId)
    }
    
    /* Start music playing */
    @Synchronized
    private fun play(getFocus: Boolean = true) {
        // Gain focus
        if (getFocus) {
            gainAudioFocusRequest()
        }
        
        try {
            if (playerState == PlayState.PLAY) return
            
            if (playerState == PlayState.STOP) {
                playList.add(musicList.first())
                musicLoc = 0
                updateMusic()
                return
            }
    
            mediaPlayer.start()
            playerState = PlayState.PLAY
            
            if (pauseSeek != -1) {
                mediaPlayer.seekTo(pauseSeek)
                pauseSeek = -1
                pauseLoc = -1
            }
            sendBroadcast(Intent(SERVICE_BROADCAST_PLAY))
        } catch (e: Exception) {
            e.getStack()
        }
    }
    
    @Synchronized
    private fun gradualPause(abundantFocus: Boolean = true) {
        for (i in 5 downTo 0) {
            (i / 5F).apply {
                mediaPlayer.setVolume(this, this)
            }
            
            Thread.sleep(100)
        }
        pause(abundantFocus)
        mediaPlayer.setVolume(1F, 1F)
    }
    
    /* Pause music playing */
    @Synchronized
    private fun pause(abundantFocus: Boolean = true) {
        if (playerState == PlayState.PAUSE) {
            return
        }
        
        if (playerState == PlayState.PLAY) {
            pauseLoc = mediaPlayer.currentPosition
            mediaPlayer.pause()
        }
        
        playerState = PlayState.PAUSE
        sendBroadcast(Intent(SERVICE_BROADCAST_PAUSE))
        
        // abandon focus
        if (abundantFocus) {
            abundantAudioFocusRequest()
        }
    }
    
    /* Seek playing position or music position */
    @Synchronized
    private fun seek(loc: Int) {
        if (loc == ERROR_CODE_INT) {
            return
        }
        
        if (playerState == PlayState.PLAY) {
            mediaPlayer.seekTo(loc)
            return
        }
    
        pauseSeek = loc
    }
    
    /* Call for playing next music */
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
    
    /* Call for playing last music */
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
    
    /* Set switching music value */
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
    
    /* Switch to other music according to global value */
    @Synchronized
    private fun updateMusic() {
        try {
            /*  Remove all */
            if (playerState == PlayState.STOP) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    audioManager.requestAudioFocus(audioFocusRequest)
                } else {
                    @Suppress("DEPRECATION")
                    audioManager.requestAudioFocus(
                        onAudioFocusChangeListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN
                    )
                }
            } else {
                mediaPlayer.reset()
            }
            
            mediaPlayer.stop()
            mediaPlayer.reset()
            
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            contentResolver.openAssetFileDescriptor(
                Uri.parse(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + playList[musicLoc].id()),
                "r"
            )?.apply {
                mediaPlayer.setDataSource(fileDescriptor)
                mediaPlayer.prepare()
                close()
            }
            //} else {
            //    mediaPlayer.setDataSource(playList[musicLoc].path())
            //   mediaPlayer.prepare()
            //}
            
            mediaPlayer.start()
            playerState = PlayState.PLAY
            
            pauseLoc = -1
            pauseSeek = -1
            sendBroadcast(Intent(SERVICE_BROADCAST_CHANGED))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /* Notify updated notification */
    @Synchronized
    private fun updateNotify(bitmap: Bitmap? = null, requested: Boolean = false) {
        currentBitmap = bitmap
        notificationManagerCompat.notify(PlayServiceId, getNotification(bitmap, requested))
    }
    
    /* Creating a notification */
    private fun getNotification(bitmap: Bitmap? = null, requested: Boolean = false): Notification {
        return NotificationCompat.Builder(this, PlayServiceChannelId).apply {
            notificationCompat = this
            
            if (musicLoc != -1) {
                playList[musicLoc].apply {
                    setContentTitle(title())                                    // Title
                    setContentText(artist().plus(" - ").plus(album()))    // Artist + Album
                    
                    setLargeIcon(
                        if (bitmap == null) {
                            // Call for a album cover
                            if (!requested) {
                                Thread {
                                    albumCover(this@PlayService)
                                }.start()
                            }
                            
                            ContextCompat.getDrawable(this@PlayService, R.mipmap.ic_launcher)!!.toBitmap()
                        } else {
                            bitmap
                        }
                    )
                    // update duration
                    mediaMetadataCompat.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration().toLong())
                }
            }
            
            setSmallIcon(R.mipmap.ic_launcher_round)
            
            priority = NotificationCompat.PRIORITY_MAX
            
            addAction(notificationAction_last)
            
            /* Add control Button */
            when (playerState) {
                PlayState.STOP, PlayState.PAUSE -> {
                    setOngoing(false)
                    setAutoCancel(true)
                    addAction(notificationAction_play)
                    playbackStateCompat.setState(PlaybackStateCompat.STATE_PAUSED, getCurrentPosition().toLong(), 1F)
                }
                
                PlayState.PLAY -> {
                    playbackStateCompat.setState(PlaybackStateCompat.STATE_PLAYING, getCurrentPosition().toLong(), 1F)
                    setOngoing(true)
                    setAutoCancel(false)
                    addAction(notificationAction_pause)
                }
            }
            
            addAction(notificationAction_next)
    
            mediaSessionCompat.apply {
                setPlaybackState(playbackStateCompat.build())
                setMetadata(mediaMetadataCompat.build())
            }
    
            setStyle(notificationStyle)
            
        }.build().apply {
            /* Garbage clean */
            System.gc()
        }
    }
    
    /* onDestroy */
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
    
    /* Request for album cover image returning bitmap */
    override fun onResult(bitmap: Bitmap) {
        updateNotify(bitmap, true)
    }
    
    /* Request for album cover image returning null result */
    override fun onNullResult() {
        makeToast(R.string.abc_playService_cover_null)
    }
    
    /* onBind */
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
}