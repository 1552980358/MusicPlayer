package projekt.cloud.piece.music.player.service

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM_ART
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ARTIST
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PAUSE
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_PAUSE
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SEEK_TO
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_NEXT
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM
import android.support.v4.media.session.PlaybackStateCompat.ACTION_STOP
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_ALL
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_NONE
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_ONE
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_ALL
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_NONE
import android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.STATE_ENDED
import kotlinx.coroutines.runBlocking
import projekt.cloud.piece.music.player.BuildConfig.APPLICATION_ID
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.item.AudioMetadata
import projekt.cloud.piece.music.player.service.play.NotificationHelper
import projekt.cloud.piece.music.player.service.play.PlayingQueue
import projekt.cloud.piece.music.player.service.play.ServiceConstants.ACTION_START_COMMAND
import projekt.cloud.piece.music.player.service.play.ServiceConstants.ACTION_START_COMMAND_PAUSE
import projekt.cloud.piece.music.player.service.play.ServiceConstants.ACTION_START_COMMAND_PLAY
import projekt.cloud.piece.music.player.service.play.ServiceConstants.CUSTOM_ACTION_REPEAT_MODE
import projekt.cloud.piece.music.player.service.play.ServiceConstants.CUSTOM_ACTION_SHUFFLE_MODE
import projekt.cloud.piece.music.player.service.play.ServiceConstants.EXTRA_AUDIO_METADATA_LIST
import projekt.cloud.piece.music.player.util.ArtUtil.SUFFIX_LARGE
import projekt.cloud.piece.music.player.util.ArtUtil.TYPE_ALBUM
import projekt.cloud.piece.music.player.util.ArtUtil.fileOf
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.ServiceUtil.startSelf

class PlayService: MediaBrowserServiceCompat(), Player.Listener {
    
    private companion object {
        
        const val TAG = "${APPLICATION_ID}.PlayService"
        
        const val DEFAULT_PLAYBACK_SPEED = 1.0f
    
        const val PLAYBACK_STATE_ACTIONS =
            ACTION_PLAY or ACTION_PAUSE or ACTION_PLAY_PAUSE or ACTION_STOP or ACTION_SEEK_TO or
                ACTION_PLAY_FROM_MEDIA_ID or ACTION_SKIP_TO_NEXT or ACTION_SKIP_TO_PREVIOUS or ACTION_SKIP_TO_QUEUE_ITEM
        
        const val VOLUME_FULL = 1.0f
    }
    
    private lateinit var exoPlayer: ExoPlayer
    private var playbackStateCompat = PlaybackStateCompat.Builder()
        .setState(STATE_NONE, 0, DEFAULT_PLAYBACK_SPEED)
        .setActions(PLAYBACK_STATE_ACTIONS)
        .addCustomAction(CUSTOM_ACTION_REPEAT_MODE, CUSTOM_ACTION_REPEAT_MODE, R.drawable.ic_launcher_foreground)
        .addCustomAction(CUSTOM_ACTION_SHUFFLE_MODE, CUSTOM_ACTION_SHUFFLE_MODE, R.drawable.ic_launcher_foreground)
        .build()
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private val transportControls: MediaControllerCompat.TransportControls
        get() = mediaSessionCompat.controller.transportControls
    
    @PlaybackStateCompat.RepeatMode
    private var repeatMode = REPEAT_MODE_ALL
    
    @PlaybackStateCompat.ShuffleMode
    private var shuffleMode = SHUFFLE_MODE_ALL
    
    private var isForeground = false
    
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var playingQueue: PlayingQueue
    
    private var audioArt: Bitmap? = null
    private lateinit var defaultArt: Bitmap
    
    override fun onCreate() {
        super.onCreate()
    
        mediaSessionCompat = MediaSessionCompat(this, TAG).apply {
            setCallback(object: MediaSessionCompat.Callback() {
                override fun onPlay() {
                    if ((playbackStateCompat.state != STATE_BUFFERING && playbackStateCompat.state != STATE_PAUSED) || exoPlayer.isPlaying) {
                        return
                    }
                    exoPlayer.play()
                    
                    playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                        .setState(STATE_PLAYING, exoPlayer.currentPosition, DEFAULT_PLAYBACK_SPEED)
                        .build()
                    mediaSessionCompat.setPlaybackState(playbackStateCompat)
                    
                    startSelf { putExtra(ACTION_START_COMMAND, ACTION_START_COMMAND_PLAY) }
                }
                override fun onPause() {
                    if (playbackStateCompat.state != STATE_PLAYING || !exoPlayer.isPlaying) {
                        return
                    }
                    exoPlayer.pause()
    
                    playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                        .setState(STATE_PAUSED, exoPlayer.currentPosition, DEFAULT_PLAYBACK_SPEED)
                        .build()
                    mediaSessionCompat.setPlaybackState(playbackStateCompat)
    
                    startSelf { putExtra(ACTION_START_COMMAND, ACTION_START_COMMAND_PAUSE) }
                }
                override fun onPlayFromMediaId(mediaId: String, extras: Bundle) = playAudioMetadata(
                    @Suppress("UNCHECKED_CAST")
                    playingQueue.setAudioMetadataList(
                        mediaId,
                        extras.getSerializable(EXTRA_AUDIO_METADATA_LIST) as ArrayList<AudioMetadata>,
                        false
                    )
                )
                
                override fun onSkipToPrevious() {
                }
                override fun onSkipToNext() {
                }
                override fun onSkipToQueueItem(id: Long) {
                }
                override fun onSeekTo(pos: Long) {
                }
                
                override fun onCustomAction(action: String, extras: Bundle) {
                    when (action) {
                        
                        CUSTOM_ACTION_REPEAT_MODE -> {
                            repeatMode = when (repeatMode) {
                                REPEAT_MODE_ALL -> REPEAT_MODE_ONE
                                REPEAT_MODE_ONE -> REPEAT_MODE_NONE
                                REPEAT_MODE_NONE -> REPEAT_MODE_ALL
                                else -> REPEAT_MODE_ALL
                            }
                            mediaSessionCompat.setRepeatMode(repeatMode)
                        }
                        
                        CUSTOM_ACTION_SHUFFLE_MODE -> {
                            shuffleMode = when (shuffleMode) {
                                SHUFFLE_MODE_ALL -> SHUFFLE_MODE_NONE
                                SHUFFLE_MODE_NONE -> SHUFFLE_MODE_ALL
                                else -> SHUFFLE_MODE_ALL
                            }
                            mediaSessionCompat.setShuffleMode(shuffleMode)
                            mediaSessionCompat.setExtras(
                                bundleOf(EXTRA_AUDIO_METADATA_LIST to playingQueue.setShuffle(shuffleMode == SHUFFLE_MODE_ALL))
                            )
                        }
                        
                    }
                }
                
            })
            setPlaybackState(playbackStateCompat)
            setRepeatMode(repeatMode)
            setShuffleMode(shuffleMode)
            isActive = true
            
            this@PlayService.sessionToken = sessionToken
        }
        
        exoPlayer = ExoPlayer.Builder(this)
            .build()
        with(exoPlayer) {
            addListener(this@PlayService)
            volume = VOLUME_FULL
        }
    
        notificationHelper = NotificationHelper(this)
        playingQueue = PlayingQueue()
    
        defaultArt = ContextCompat.getDrawable(this, R.drawable.ic_round_music_note_200)!!.toBitmap()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getStringExtra(ACTION_START_COMMAND)) {
            ACTION_START_COMMAND_PLAY -> {
                val audioArt = audioArt ?: defaultArt
                when {
                    isForeground -> {
                        notificationHelper.startForeground(this, playingQueue.current, audioArt)
                        isForeground = true
                    }
                    else -> notificationHelper.updateNotification(this, playingQueue.current, audioArt)
                }
            }
            ACTION_START_COMMAND_PAUSE -> {
                notificationHelper.startForeground(this, playingQueue.current, audioArt ?: defaultArt)
                stopForeground(false)
                isForeground = false
            }
            else -> MediaButtonReceiver.handleIntent(mediaSessionCompat, intent)
        }
        return super.onStartCommand(intent, flags, startId)
    }
    
    override fun onDestroy() {
        exoPlayer.stop()
        exoPlayer.release()
        super.onDestroy()
    }
    
    override fun onPlaybackStateChanged(playbackState: Int) {
        if (playbackState == STATE_ENDED) {
            when (repeatMode) {
                REPEAT_MODE_ALL -> playAudioMetadata(playingQueue.next)
                REPEAT_MODE_ONE -> playAudioMetadata(playingQueue.current)
                else -> {
                    if (!playingQueue.isLast) {
                        return playAudioMetadata(playingQueue.next)
                    }
                    playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                        .setState(STATE_PAUSED, 0, DEFAULT_PLAYBACK_SPEED)
                        .build()
                    mediaSessionCompat.setPlaybackState(playbackStateCompat)
                    startSelf { putExtra(ACTION_START_COMMAND, ACTION_START_COMMAND_PAUSE) }
                }
            }
        }
    }
    
    private fun playAudioMetadata(audioMetadata: AudioMetadata) {
        audioArt = null
        runBlocking {
            io {
                fileOf(TYPE_ALBUM, audioMetadata.album.id, SUFFIX_LARGE).also { file ->
                    if (file.exists()) {
                        audioArt = file.inputStream().use { BitmapFactory.decodeStream(it) }
                    }
                }
            }
    
            playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                .setState(STATE_BUFFERING, 0, DEFAULT_PLAYBACK_SPEED)
                .build()
            mediaSessionCompat.setPlaybackState(playbackStateCompat)
            
            exoPlayer.setMediaItem(MediaItem.fromUri(audioMetadata.uri))
            exoPlayer.prepare()
        }
        
        mediaSessionCompat.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(METADATA_KEY_TITLE, audioMetadata.title)
                .putString(METADATA_KEY_ARTIST, audioMetadata.artistName)
                .putString(METADATA_KEY_ALBUM, audioMetadata.albumTitle)
                .putString(METADATA_KEY_MEDIA_ID, audioMetadata.id)
                .putLong(METADATA_KEY_DURATION, audioMetadata.duration)
                .putString(METADATA_KEY_ALBUM_ART_URI, audioMetadata.album.uri.toString().takeIf { audioArt != null })
                .putBitmap(METADATA_KEY_ALBUM_ART, audioArt ?: defaultArt)
                .build()
        )
    
        transportControls.play()
    }
    
    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?) = when (clientPackageName) {
        APPLICATION_ID -> BrowserRoot(TAG, null)
        else -> null
    }
    
    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(null)
    }
    
}