package projekt.cloud.piece.music.player.service

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM_ART
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ARTIST
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.MediaSessionCompat.Callback
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_PAUSE
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SEEK_TO
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_NEXT
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM
import android.support.v4.media.session.PlaybackStateCompat.ACTION_STOP
import android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.graphics.drawable.toBitmap
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayer.Builder
import com.google.android.exoplayer2.MediaItem.fromUri
import com.google.android.exoplayer2.Player.Listener
import projekt.cloud.piece.music.player.BuildConfig.APPLICATION_ID
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.service.play.Action.START_COMMAND_ACTION
import projekt.cloud.piece.music.player.service.play.Action.START_COMMAND_ACTION_PAUSE
import projekt.cloud.piece.music.player.service.play.Action.START_COMMAND_ACTION_PLAY
import projekt.cloud.piece.music.player.service.play.Config.FOREGROUND_SERVICE
import projekt.cloud.piece.music.player.service.play.Config.getConfig
import projekt.cloud.piece.music.player.service.play.Config.setConfig
import projekt.cloud.piece.music.player.service.play.Extra.EXTRA_INDEX
import projekt.cloud.piece.music.player.service.play.Extra.EXTRA_LIST
import projekt.cloud.piece.music.player.service.play.MediaIdUtil.parseAsUri
import projekt.cloud.piece.music.player.service.play.NotificationUtil.createNotification
import projekt.cloud.piece.music.player.service.play.NotificationUtil.createNotificationManager
import projekt.cloud.piece.music.player.service.play.NotificationUtil.startForeground
import projekt.cloud.piece.music.player.service.play.NotificationUtil.update
import projekt.cloud.piece.music.player.util.ImageUtil.loadAlbumArtRaw
import projekt.cloud.piece.music.player.util.ImageUtil.loadAudioArtRaw
import projekt.cloud.piece.music.player.util.ServiceUtil.startService

class PlayService: MediaBrowserServiceCompat(), Listener {
    
    companion object {
        /**
         *
         * (1 << 9) | (1 << 0) | (1 << 8) | (1 << 10) | (1 << 5) | (1 << 4) | (1 << 12)
         * -> 0b1011100110001
         * -> 5937
         **/
        const val PlaybackStateActions =
            ACTION_PLAY_PAUSE or ACTION_STOP or ACTION_SEEK_TO or ACTION_PLAY_FROM_MEDIA_ID or ACTION_SKIP_TO_NEXT or ACTION_SKIP_TO_PREVIOUS or ACTION_SKIP_TO_QUEUE_ITEM
        
        private const val TAG = "PlayService"
        private const val ROOT_ID = TAG
        
    }
    
    private lateinit var mediaSession: MediaSessionCompat
    private val mediaSessionCallback = object : Callback() {
        override fun onPlay() {
        
        }
    
        override fun onPause() {
        
        }
    
        override fun onStop() {
        
        }
    
        override fun onSkipToPrevious() {
        
        }
        
        override fun onSkipToNext() {
        
        }
    
        override fun onSkipToQueueItem(id: Long) {
        
        }
    
        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            mediaId ?: return
            extras?.let { extrasBundle ->
                if (extrasBundle.containsKey(EXTRA_LIST)) {
                    @Suppress("UNCHECKED_CAST")
                    audioList = extrasBundle.getSerializable(EXTRA_LIST) as List<AudioItem>
                }
                if (extrasBundle.containsKey(EXTRA_INDEX)) {
                    listIndex = extrasBundle.getInt(EXTRA_INDEX)
                }
            }
            
            exoPlayer.stop()
            exoPlayer.seekTo(0L)
            
            playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                .setState(STATE_BUFFERING, 0, 1F)
                .build()
            mediaSession.setPlaybackState(playbackStateCompat)
            val audioItem = audioList[listIndex]
            mediaSession.setMetadata(
                MediaMetadataCompat.Builder()
                    .apply {
                        putString(METADATA_KEY_MEDIA_ID, audioItem.id)
                        putString(METADATA_KEY_ARTIST, audioItem.artistItem.name)
                        putString(METADATA_KEY_ALBUM, audioItem.albumItem.title)
                        putString(METADATA_KEY_ALBUM_ART, audioItem.album)
                        putLong(METADATA_KEY_DURATION, audioItem.duration)
                    }.build()
            )
            
            exoPlayer.setMediaItem(fromUri(audioItem.id.parseAsUri))
            exoPlayer.prepare()
    
            onPlay()
        }
    
        override fun onSeekTo(pos: Long) {
        
        }
        
    }
    
    @Volatile
    private lateinit var playbackStateCompat: PlaybackStateCompat
    
    private lateinit var exoPlayer: ExoPlayer
    
    private lateinit var audioList: List<AudioItem>
    private var listIndex = 0
    
    private var configs = 0
    
    private lateinit var defaultImage: Bitmap
    
    private lateinit var notificationManager: NotificationManagerCompat
    
    override fun onCreate() {
        super.onCreate()
    
        // MediaBrowser Config
        playbackStateCompat = PlaybackStateCompat.Builder()
            .setState(STATE_NONE, 0, 1F)
            .setActions(PlaybackStateActions)
            .build()
    
        mediaSession = MediaSessionCompat(this, ROOT_ID).apply {
            setCallback(mediaSessionCallback)
            setPlaybackState(playbackStateCompat)
            isActive = true
        }
        
        sessionToken = mediaSession.sessionToken
    
        notificationManager = createNotificationManager
    
        // ExoPlayer Config
        exoPlayer = Builder(this).build()
        exoPlayer.addListener(this)
    
        defaultImage = getDrawable(this, R.drawable.ic_music)!!.toBitmap()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getStringExtra(START_COMMAND_ACTION)) {
            START_COMMAND_ACTION_PLAY -> {
                val notification = audioList[listIndex].run {
                    createNotification(this, false, loadAudioArtRaw(id) ?: loadAlbumArtRaw(album) ?: defaultImage)
                }
                when {
                    !configs.getConfig(FOREGROUND_SERVICE) -> {
                        startForeground(notification)
                        configs = configs.setConfig(FOREGROUND_SERVICE, true)
                    }
                    else -> notificationManager.update(notification)
                }
            }
            START_COMMAND_ACTION_PAUSE -> {
                val notification = audioList[listIndex].run {
                    createNotification(this, true, loadAudioArtRaw(id) ?: loadAlbumArtRaw(album) ?: defaultImage)
                }
                startForeground(notification)
                stopForeground(false)
                configs = configs.setConfig(FOREGROUND_SERVICE, false)
            }
        }
        
        return super.onStartCommand(intent, flags, startId)
    }
    
    override fun onPlaybackStateChanged(playbackState: Int) {
    
    }
    
    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?) = when (clientPackageName) {
        APPLICATION_ID -> BrowserRoot(TAG, null)
        else -> null
    }
    
    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.detach()
        result.sendResult(null)
    }
    
}