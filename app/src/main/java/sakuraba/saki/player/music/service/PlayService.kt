package sakuraba.saki.player.music.service

import android.app.Notification
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.Bundle
import android.os.PowerManager
import android.os.PowerManager.PARTIAL_WAKE_LOCK
import android.os.PowerManager.WakeLock
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_PAUSE
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SEEK_TO
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_NEXT
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
import android.support.v4.media.session.PlaybackStateCompat.ACTION_STOP
import android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import android.support.v4.media.session.PlaybackStateCompat.STATE_STOPPED
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.media.MediaBrowserServiceCompat
import lib.github1552980358.ktExtension.android.content.broadcastReceiver
import lib.github1552980358.ktExtension.android.content.register
import lib.github1552980358.ktExtension.android.os.bundle
import sakuraba.saki.player.music.BuildConfig
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.service.util.AudioInfo
import sakuraba.saki.player.music.service.util.MediaMetadataUtil.setMediaMetadata
import sakuraba.saki.player.music.service.util.createNotificationManager
import sakuraba.saki.player.music.service.util.getNotification
import sakuraba.saki.player.music.service.util.startForeground
import sakuraba.saki.player.music.service.util.startService
import sakuraba.saki.player.music.service.util.syncPlayAndPrepareMediaId
import sakuraba.saki.player.music.service.util.update
import sakuraba.saki.player.music.util.Constants.ACTION_REQUEST_STATUS
import sakuraba.saki.player.music.util.Constants.ACTION_EXTRA
import sakuraba.saki.player.music.util.Constants.ACTION_UPDATE_PLAY_MODE
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO_LIST
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO_POS
import sakuraba.saki.player.music.util.Constants.EXTRAS_PLAY_MODE
import sakuraba.saki.player.music.util.Constants.EXTRAS_PLAY
import sakuraba.saki.player.music.util.Constants.EXTRAS_PROGRESS
import sakuraba.saki.player.music.util.Constants.EXTRAS_STATUS
import sakuraba.saki.player.music.util.Constants.PLAY_MODE_LIST
import sakuraba.saki.player.music.util.Constants.PLAY_MODE_RANDOM
import sakuraba.saki.player.music.util.Constants.PLAY_MODE_SINGLE
import sakuraba.saki.player.music.util.Constants.PLAY_MODE_SINGLE_CYCLE
import sakuraba.saki.player.music.util.Constants.EXTRAS_PAUSE
import sakuraba.saki.player.music.util.Constants.FILTER_NOTIFICATION_NEXT
import sakuraba.saki.player.music.util.Constants.FILTER_NOTIFICATION_PAUSE
import sakuraba.saki.player.music.util.Constants.FILTER_NOTIFICATION_PLAY
import sakuraba.saki.player.music.util.Constants.FILTER_NOTIFICATION_PREV

class PlayService: MediaBrowserServiceCompat(), OnCompletionListener {
    
    companion object {
        private const val TAG = "PlayService"
        private const val WAKE_LOCK_tAG = "$TAG::PlayWakeLock"
        const val ROOT_ID = TAG
        private const val PlaybackStateActions =
            ACTION_PLAY_PAUSE or ACTION_STOP or ACTION_SEEK_TO or ACTION_PLAY_FROM_MEDIA_ID or ACTION_SKIP_TO_NEXT or ACTION_SKIP_TO_PREVIOUS
    }
    
    private lateinit var mediaPlayer: MediaPlayer
    
    private lateinit var audioInfoList: List<AudioInfo>
    private var listPos = 0
    
    private var isForegroundService = false
    
    private lateinit var notificationManager: NotificationManagerCompat
    private var notification: Notification? = null
    
    private lateinit var mediaSession: MediaSessionCompat
    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            Log.e(TAG, "onPlay")
            if (playbackStateCompat.state != STATE_PAUSED && playbackStateCompat.state != STATE_PLAYING) {
                return
            }
            playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                .setState(STATE_PLAYING, playbackStateCompat.position, 1F)
                .build()
            mediaSession.setPlaybackState(playbackStateCompat)
            mediaPlayer.start()
            startService(PlayService::class.java) {
                putExtra(ACTION_EXTRA, EXTRAS_PLAY)
            }
        }
        override fun onPause() {
            Log.e(TAG, "onPause")
            if (playbackStateCompat.state != STATE_PLAYING) {
                return
            }
            playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                .setState(STATE_PAUSED, mediaPlayer.currentPosition.toLong(), 1F)
                .build()
            mediaSession.setPlaybackState(playbackStateCompat)
            mediaPlayer.pause()
            startService(PlayService::class.java) {
                putExtra(ACTION_EXTRA, EXTRAS_PAUSE)
            }
        }
        override fun onStop() {
            Log.e(TAG, "onStop")
            if (playbackStateCompat.state != STATE_PAUSED && playbackStateCompat.state != STATE_PLAYING) {
                return
            }
            playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                .setState(STATE_STOPPED, 0, 1F)
                .build()
            mediaSession.setPlaybackState(playbackStateCompat)
            mediaPlayer.pause()
            mediaPlayer.seekTo(0)
        }
        override fun onSkipToPrevious() {
            Log.e(TAG, "onSkipToPrevious")
            if (!::audioInfoList.isInitialized || listPos == -1) {
                return
            }
            when (listPos) {
                0 -> listPos = audioInfoList.lastIndex
                else -> listPos--
            }
            onPlayFromMediaId(audioInfoList[listPos].audioId, null)
        }
        override fun onSkipToNext() {
            Log.e(TAG, "onSkipToNext")
            if (!::audioInfoList.isInitialized || listPos == -1) {
                return
            }
            when (listPos) {
                audioInfoList.lastIndex -> listPos = 0
                else -> listPos++
            }
            onPlayFromMediaId(audioInfoList[listPos].audioId, null)
        }
        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            Log.e(TAG, "onPlayFromMediaId $mediaId")
            mediaId?:return
            if (extras != null) {
                @Suppress("UNCHECKED_CAST")
                audioInfoList = (extras.getSerializable(EXTRAS_AUDIO_INFO_LIST) as ArrayList<AudioInfo>?) ?: return
                listPos = extras.getInt(EXTRAS_AUDIO_INFO_POS)
            }
            
            playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                .setState(STATE_BUFFERING, 0, 1F)
                .build()
            mediaSession.setPlaybackState(playbackStateCompat)
            mediaMetadataCompat = MediaMetadataCompat.Builder()
                .setMediaMetadata(audioInfoList[listPos])
                .build()
            mediaSession.setMetadata(mediaMetadataCompat)
            
            mediaPlayer.syncPlayAndPrepareMediaId(this@PlayService, mediaId) {
                playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                    .setState(STATE_PLAYING, 0, 1F)
                    .build()
                mediaSession.setPlaybackState(playbackStateCompat)
            }
            startService(PlayService::class.java) {
                putExtra(ACTION_EXTRA, EXTRAS_PLAY)
            }
        }
        override fun onSeekTo(pos: Long) {
            Log.e(TAG, "onSeekTo $pos")
            playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                .setState(playbackStateCompat.state, pos, 1F)
                .build()
            mediaSession.setPlaybackState(playbackStateCompat)
            mediaPlayer.seekTo(pos.toInt())
        }
    }
    
    @Volatile
    private lateinit var playbackStateCompat: PlaybackStateCompat
    
    private lateinit var mediaMetadataCompat: MediaMetadataCompat
    
    private val broadcastReceiver = broadcastReceiver { _, intent, _ ->
        when (intent?.action) {
            FILTER_NOTIFICATION_PREV -> mediaSession.controller.transportControls.skipToPrevious()
            FILTER_NOTIFICATION_PLAY -> mediaSession.controller.transportControls.play()
            FILTER_NOTIFICATION_PAUSE -> mediaSession.controller.transportControls.pause()
            FILTER_NOTIFICATION_NEXT -> mediaSession.controller.transportControls.skipToNext()
        }
    }
    
    private lateinit var wakeLock: WakeLock
    
    override fun onCreate() {
        Log.e(TAG, "onCreate")
        super.onCreate()
    
        playbackStateCompat = PlaybackStateCompat.Builder()
            .setActions(PlaybackStateActions)
            .setState(STATE_NONE, 0, 1F)
            .addCustomAction(ACTION_REQUEST_STATUS, ACTION_REQUEST_STATUS, R.drawable.ic_launcher_foreground)
            .addCustomAction(ACTION_UPDATE_PLAY_MODE, ACTION_UPDATE_PLAY_MODE, R.drawable.ic_launcher_foreground)
            .setExtras(bundle { putInt(EXTRAS_PLAY_MODE, PLAY_MODE_LIST) })
            .build()
        
        mediaSession = MediaSessionCompat(this, ROOT_ID).apply {
            setCallback(mediaSessionCallback)
            setPlaybackState(playbackStateCompat)
            isActive = true
        }
        sessionToken = mediaSession.sessionToken
        
        mediaPlayer = MediaPlayer()
        mediaPlayer.setOnCompletionListener(this)
        
        notificationManager = createNotificationManager
        
        broadcastReceiver.register(this, arrayOf(FILTER_NOTIFICATION_PREV, FILTER_NOTIFICATION_PLAY, FILTER_NOTIFICATION_PAUSE, FILTER_NOTIFICATION_NEXT))
        
        
        wakeLock = (getSystemService(POWER_SERVICE) as PowerManager).newWakeLock(PARTIAL_WAKE_LOCK, WAKE_LOCK_tAG)
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand $flags $startId")
        notification = getNotification(audioInfoList[listPos], playbackStateCompat.state == STATE_PAUSED)
        when (intent?.getStringExtra(ACTION_EXTRA)) {
            EXTRAS_PLAY -> {
                Log.e(TAG, "EXTRAS_PLAY")
                if (!isForegroundService) {
                    startForeground(notification!!)
                    isForegroundService = true
                    if (!wakeLock.isHeld) {
                        @Suppress("WakelockTimeout")
                        wakeLock.acquire()
                    }
                } else {
                    notificationManager.update(notification!!)
                }
            }
            EXTRAS_PAUSE -> {
                Log.e(TAG, "EXTRAS_PAUSE")
                startForeground(notification!!)
                stopForeground(false)
                isForegroundService = false
                if (wakeLock.isHeld) {
                    wakeLock.release()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
    
    override fun onCustomAction(action: String, extras: Bundle?, result: Result<Bundle>) {
        Log.e(TAG, "onCustomAction $action")
        when (action) {
            ACTION_REQUEST_STATUS -> {
                if (playbackStateCompat.state == STATE_NONE) {
                    result.sendResult(null)
                    return
                }
                result.sendResult(bundle {
                    putInt(EXTRAS_STATUS, playbackStateCompat.state)
                    putInt(EXTRAS_PROGRESS, mediaPlayer.currentPosition)
                    putInt(EXTRAS_PLAY_MODE, playbackStateCompat.extras?.getInt(EXTRAS_PLAY_MODE) ?: PLAY_MODE_LIST)
                    playbackStateCompat.extras?.let { extras ->
                        putInt(EXTRAS_PLAY_MODE, extras.getInt(EXTRAS_PLAY_MODE, PLAY_MODE_LIST))
                    }
                    if (listPos != -1 && ::audioInfoList.isInitialized) {
                        putSerializable(EXTRAS_AUDIO_INFO, audioInfoList[listPos])
                    }
                })
            }
            ACTION_UPDATE_PLAY_MODE -> {
                extras?:return
                playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                    .setState(playbackStateCompat.state, mediaPlayer.currentPosition.toLong(), 1F)
                    .setExtras(bundle { putInt(EXTRAS_PLAY_MODE, extras.getInt(EXTRAS_PLAY_MODE, PLAY_MODE_LIST)) })
                    .build()
                mediaSession.setPlaybackState(playbackStateCompat)
                result.sendResult(null)
            }
            else -> super.onCustomAction(action, extras, result)
        }
    }
    
    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?) = when (clientPackageName) {
        BuildConfig.APPLICATION_ID -> BrowserRoot(TAG, null)
        else -> null
    }
    
    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        Log.e(TAG, "onLoadChildren $parentId")
        result.sendResult(null)
    }
    
    override fun onCompletion(mediaPlayer: MediaPlayer?) {
        val playMode = playbackStateCompat.extras?.getInt(EXTRAS_PLAY_MODE, PLAY_MODE_LIST)
        if (playMode == null) {
            mediaSession.controller.transportControls.skipToNext()
            return
        }
        when (playMode) {
            PLAY_MODE_RANDOM, PLAY_MODE_LIST -> {
                mediaSession.controller.transportControls.skipToNext()
            }
            PLAY_MODE_SINGLE_CYCLE -> {
                mediaSession.controller.transportControls.play()
            }
            PLAY_MODE_SINGLE -> {
                mediaSession.controller.transportControls.stop()
            }
            else -> {
                mediaSession.controller.transportControls.skipToNext()
            }
        }
    }
    
}