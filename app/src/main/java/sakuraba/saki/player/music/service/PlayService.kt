package sakuraba.saki.player.music.service

import android.app.Notification
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
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
import android.support.v4.media.session.PlaybackStateCompat.ACTION_STOP
import android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import android.support.v4.media.session.PlaybackStateCompat.STATE_STOPPED
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.media.MediaBrowserServiceCompat
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
import sakuraba.saki.player.music.util.Constants.ACTION_REQUEST_STATUS
import sakuraba.saki.player.music.util.Constants.ACTION_START
import sakuraba.saki.player.music.util.Constants.ACTION_UPDATE_PLAY_MODE
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO_LIST
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO_POS
import sakuraba.saki.player.music.util.Constants.EXTRAS_PLAY_MODE
import sakuraba.saki.player.music.util.Constants.START_EXTRAS_PLAY
import sakuraba.saki.player.music.util.Constants.EXTRAS_PROGRESS
import sakuraba.saki.player.music.util.Constants.EXTRAS_STATUS
import sakuraba.saki.player.music.util.Constants.PLAY_MODE_LIST
import sakuraba.saki.player.music.util.Constants.PLAY_MODE_RANDOM
import sakuraba.saki.player.music.util.Constants.PLAY_MODE_SINGLE
import sakuraba.saki.player.music.util.Constants.PLAY_MODE_SINGLE_CYCLE

class PlayService: MediaBrowserServiceCompat(), OnCompletionListener {
    
    companion object {
        private const val TAG = "BackgroundPlayService"
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
            if (!isForegroundService) {
                startService(PlayService::class.java) {
                    putExtra(ACTION_START, START_EXTRAS_PLAY)
                }
            }
            playbackStateCompat = PlaybackStateCompat.Builder()
                .setState(STATE_PLAYING, playbackStateCompat.position, 1F)
                .build()
            mediaSession.setPlaybackState(playbackStateCompat)
            mediaPlayer.start()
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
        }
        override fun onStop() {
            Log.e(TAG, "onStop")
            if (playbackStateCompat.state != STATE_PAUSED && playbackStateCompat.state != STATE_PLAYING) {
                return
            }
            playbackStateCompat = PlaybackStateCompat.Builder()
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
            onPlayFromMediaId(audioInfoList[--listPos].audioId, null)
        }
        override fun onSkipToNext() {
            Log.e(TAG, "onSkipToNext")
            if (!::audioInfoList.isInitialized || listPos == -1) {
                return
            }
            onPlayFromMediaId(audioInfoList[++listPos].audioId, null)
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
            if (!isForegroundService) {
                startService(PlayService::class.java) {
                    putExtra(ACTION_START, START_EXTRAS_PLAY)
                }
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
        
        notificationManager = createNotificationManager
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand $flags $startId")
        notification = notification.getNotification(this, mediaSession.sessionToken, audioInfoList[listPos])
        when (intent?.getStringExtra(ACTION_START)) {
            START_EXTRAS_PLAY -> {
                Log.e(TAG, "START_EXTRA_PLAY")
                startForeground(notification!!)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
    
    override fun onCustomAction(action: String, extras: Bundle?, result: Result<Bundle>) {
        when (action) {
            ACTION_REQUEST_STATUS -> {
                if (playbackStateCompat.state == STATE_NONE) {
                    result.sendResult(null)
                    return
                }
                result.sendResult(bundle {
                    putInt(EXTRAS_STATUS, playbackStateCompat.state)
                    putInt(EXTRAS_PROGRESS, mediaPlayer.currentPosition)
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
                    .setExtras(bundle { putInt(EXTRAS_PLAY_MODE, extras.getInt(EXTRAS_PLAY_MODE, PLAY_MODE_LIST)) })
                    .build()
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