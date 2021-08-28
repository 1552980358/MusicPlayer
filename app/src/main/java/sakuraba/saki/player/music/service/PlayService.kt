package sakuraba.saki.player.music.service

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_PAUSE
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SEEK_TO
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_NEXT
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
import android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import sakuraba.saki.player.music.BuildConfig
import sakuraba.saki.player.music.service.util.AudioInfo
import sakuraba.saki.player.music.service.util.syncPlayAndPrepareMediaId
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO_LIST
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO_POS

class PlayService: MediaBrowserServiceCompat() {
    
    companion object {
        private const val TAG = "BackgroundPlayService"
        const val ROOT_ID = TAG
        private const val PlaybackStateActions =
            ACTION_PLAY_PAUSE or ACTION_SEEK_TO or ACTION_PLAY_FROM_MEDIA_ID or ACTION_SKIP_TO_NEXT or ACTION_SKIP_TO_PREVIOUS
    }
    
    private lateinit var mediaPlayer: MediaPlayer
    
    private lateinit var audioInfoList: List<AudioInfo>
    private var listPos = 0
    
    
    private lateinit var mediaSession: MediaSessionCompat
    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            Log.e(TAG, "onPlay")
            if (playbackStateCompat.state != STATE_PAUSED) {
                return
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
        override fun onSkipToPrevious() {
            Log.e(TAG, "onSkipToPrevious")
            if (!::audioInfoList.isInitialized || listPos == -1) {
                return
            }
            onPlayFromMediaId(audioInfoList[++listPos].audioId, null)
        }
        override fun onSkipToNext() {
            Log.e(TAG, "onSkipToNext")
            if (!::audioInfoList.isInitialized || listPos == -1) {
                return
            }
            onPlayFromMediaId(audioInfoList[--listPos].audioId, null)
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
            
            mediaPlayer.syncPlayAndPrepareMediaId(this@PlayService, mediaId) {
                playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                    .setState(STATE_PLAYING, 0, 1F)
                    .build()
                mediaSession.setPlaybackState(playbackStateCompat)
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
    
    override fun onCreate() {
        Log.e(TAG, "onCreate")
        super.onCreate()
    
        playbackStateCompat = PlaybackStateCompat.Builder()
            .setActions(PlaybackStateActions)
            .setState(STATE_NONE, 0, 1F)
            .addCustomAction(ACTION_REQUEST_STATUS, ACTION_REQUEST_STATUS, R.drawable.ic_launcher_foreground)
            .build()
        
        mediaSession = MediaSessionCompat(this, ROOT_ID).apply {
            setCallback(mediaSessionCallback)
            setPlaybackState(playbackStateCompat)
            isActive = true
        }
        sessionToken = mediaSession.sessionToken
        
        mediaPlayer = MediaPlayer()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand $flags $startId")
        return super.onStartCommand(intent, flags, startId)
    }
    
    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?) = when (clientPackageName) {
        BuildConfig.APPLICATION_ID -> BrowserRoot(TAG, null)
        else -> null
    }
    
    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        Log.e(TAG, "onLoadChildren $parentId")
        result.sendResult(null)
    }
    
}