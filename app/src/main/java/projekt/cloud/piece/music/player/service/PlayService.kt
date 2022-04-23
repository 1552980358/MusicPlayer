package projekt.cloud.piece.music.player.service

import android.content.Intent
import android.graphics.Bitmap
import android.media.metrics.PlaybackStateEvent.STATE_ENDED
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
import androidx.media.session.MediaButtonReceiver.handleIntent
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.runBlocking
import projekt.cloud.piece.music.player.BuildConfig.APPLICATION_ID
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.dataStore.DataStore.readConfigs
import projekt.cloud.piece.music.player.dataStore.DataStore.writeConfig
import projekt.cloud.piece.music.player.database.audio.item.AudioItem
import projekt.cloud.piece.music.player.service.play.Actions.ACTION_BROADCAST_NEXT
import projekt.cloud.piece.music.player.service.play.Actions.ACTION_BROADCAST_PAUSE
import projekt.cloud.piece.music.player.service.play.Actions.ACTION_BROADCAST_PLAY
import projekt.cloud.piece.music.player.service.play.Actions.ACTION_BROADCAST_PREV
import projekt.cloud.piece.music.player.service.play.Actions.ACTION_START_COMMAND
import projekt.cloud.piece.music.player.service.play.Actions.ACTION_START_COMMAND_PAUSE
import projekt.cloud.piece.music.player.service.play.Actions.ACTION_START_COMMAND_PLAY
import projekt.cloud.piece.music.player.service.play.Actions.ACTION_UPDATE_REPEAT_MODE
import projekt.cloud.piece.music.player.service.play.Actions.ACTION_UPDATE_SHUFFLE_MODE
import projekt.cloud.piece.music.player.service.play.AudioList
import projekt.cloud.piece.music.player.service.play.AudioUtil.formUri
import projekt.cloud.piece.music.player.service.play.AudioUtil.parseUri
import projekt.cloud.piece.music.player.service.play.Config.CONFIG_PLAY_REPEAT
import projekt.cloud.piece.music.player.service.play.Config.CONFIG_PLAY_REPEAT_ONE
import projekt.cloud.piece.music.player.service.play.Config.CONFIG_PLAY_SHUFFLE
import projekt.cloud.piece.music.player.service.play.Config.CONFIG_SERVICE_FOREGROUND
import projekt.cloud.piece.music.player.service.play.Configs
import projekt.cloud.piece.music.player.service.play.Extras.EXTRA_AUDIO_ITEM
import projekt.cloud.piece.music.player.service.play.Extras.EXTRA_AUDIO_LIST
import projekt.cloud.piece.music.player.service.play.Extras.EXTRA_CONFIGS
import projekt.cloud.piece.music.player.service.play.Extras.EXTRA_REPEAT_MODE
import projekt.cloud.piece.music.player.service.play.Extras.EXTRA_SHUFFLE_MODE
import projekt.cloud.piece.music.player.service.play.ServiceNotification
import projekt.cloud.piece.music.player.util.BroadcastReceiverImpl.Companion.broadcastReceiver
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui
import projekt.cloud.piece.music.player.util.ImageUtil.readAlbumArtLarge
import projekt.cloud.piece.music.player.util.ServiceUtil.startSelf

/**
 * Class [PlayService]
 *  inherits to [MediaBrowserServiceCompat]
 *
 **/
class PlayService: MediaBrowserServiceCompat(), Player.Listener {

    companion object {
        private const val ROOT_ID = "PlayService"
    
        private const val PLAYBACK_STATE_ACTIONS =
            ACTION_PLAY or ACTION_PAUSE or ACTION_PLAY_PAUSE or ACTION_STOP or ACTION_SEEK_TO or
                ACTION_PLAY_FROM_MEDIA_ID or ACTION_SKIP_TO_NEXT or ACTION_SKIP_TO_PREVIOUS or ACTION_SKIP_TO_QUEUE_ITEM
    
        private const val DEFAULT_PLAYBACK_SPEED = 1F
        
    }
    
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {

        override fun onPlay() {
            if (playbackStateCompat.state == STATE_PLAYING || exoPlayer.isPlaying) {
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
            if (playbackStateCompat.state == STATE_PAUSED || !exoPlayer.isPlaying) {
                return
            }
            
            exoPlayer.pause()
            playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                .setState(STATE_PAUSED, exoPlayer.currentPosition, DEFAULT_PLAYBACK_SPEED)
                .build()
            mediaSessionCompat.setPlaybackState(playbackStateCompat)
    
            startSelf { putExtra(ACTION_START_COMMAND, ACTION_START_COMMAND_PAUSE) }
        }
        
        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            extras?.let {
                if (!it.containsKey(EXTRA_AUDIO_ITEM) && !it.containsKey(EXTRA_AUDIO_LIST)) {
                    return
                }
                playAudioItem(
                    @Suppress("UNCHECKED_CAST")
                    audioList.updateList(
                        it.getSerializable(EXTRA_AUDIO_ITEM) as AudioItem,
                        it.getSerializable(EXTRA_AUDIO_LIST) as List<AudioItem>
                    )
                )
            }
        }

        override fun onSkipToPrevious() {
            if (audioList.isHead) {
                if (configs.nAnd(CONFIG_PLAY_REPEAT, CONFIG_PLAY_REPEAT_ONE)) {
                    return onSeekTo(0)
                }
                return playAudioItem(audioList.last)
            }
            playAudioItem(audioList.prev)
        }

        override fun onSkipToNext() {
            if (audioList.isLast) {
                if (configs.nAnd(CONFIG_PLAY_REPEAT, CONFIG_PLAY_REPEAT_ONE)) {
                    return onSeekTo(0)
                }
                return playAudioItem(audioList.head)
            }
            playAudioItem(audioList.next)
        }
    
        override fun onSkipToQueueItem(index: Long)  =
            playAudioItem(audioList.setIndex(index.toInt()))

        override fun onCustomAction(action: String?, extras: Bundle?) {
            when (action) {
                ACTION_UPDATE_REPEAT_MODE -> {
                    extras?.getInt(EXTRA_REPEAT_MODE)?.let { repeatMode ->
                        mediaSessionCompat.setRepeatMode(repeatMode)
                        configs[CONFIG_PLAY_REPEAT] = repeatMode == REPEAT_MODE_ALL
                        configs[CONFIG_PLAY_REPEAT_ONE] = repeatMode == REPEAT_MODE_ONE
                        writeConfig(configs)
                    }
                }
                ACTION_UPDATE_SHUFFLE_MODE -> {
                    extras?.getInt(EXTRA_SHUFFLE_MODE)?.let { shuffleMode ->
                        mediaSessionCompat.setShuffleMode(shuffleMode)
                        configs[CONFIG_PLAY_SHUFFLE] = shuffleMode == SHUFFLE_MODE_ALL
                        writeConfig(configs)
                    }
                }
            }
        }
    }
    
    private val transportControls get() = mediaSessionCompat.controller.transportControls
    
    private val broadcastReceiver = broadcastReceiver {
        setActions(ACTION_BROADCAST_PREV, ACTION_BROADCAST_PLAY, ACTION_BROADCAST_PAUSE, ACTION_BROADCAST_NEXT)
        setOnReceive { _, intent ->
            when (intent?.action) {
                ACTION_BROADCAST_PREV -> transportControls.skipToPrevious()
                ACTION_BROADCAST_PLAY -> transportControls.play()
                ACTION_BROADCAST_PAUSE -> transportControls.pause()
                ACTION_BROADCAST_NEXT -> transportControls.skipToNext()
            }
        }
    }
    
    private lateinit var mediaMetadataCompat: MediaMetadataCompat
    private lateinit var playbackStateCompat: PlaybackStateCompat
    
    private val exoPlayer by lazy { ExoPlayer.Builder(this).build() }

    private val audioList = AudioList()

    private val configs = Configs()
    
    private val serviceNotification by lazy { ServiceNotification(this) }
    
    private lateinit var defaultAudioArt: Bitmap
    
    private var audioArt: Bitmap? = null
    
    override fun onCreate() {
        super.onCreate()

        playbackStateCompat = PlaybackStateCompat.Builder()
            .setState(STATE_NONE, 0, DEFAULT_PLAYBACK_SPEED)
            .setActions(PLAYBACK_STATE_ACTIONS)
            .addCustomAction(ACTION_UPDATE_REPEAT_MODE, ACTION_UPDATE_REPEAT_MODE, R.drawable.ic_launcher_foreground)
            .addCustomAction(ACTION_UPDATE_SHUFFLE_MODE, ACTION_UPDATE_SHUFFLE_MODE, R.drawable.ic_launcher_foreground)
            .setExtras(bundleOf(EXTRA_CONFIGS to configs))
            .build()
    
        mediaSessionCompat = MediaSessionCompat(this, ROOT_ID).apply {
            setCallback(mediaSessionCallback)
            setPlaybackState(playbackStateCompat)
            isActive = true
            
            this@PlayService.sessionToken = sessionToken
        }

        readConfigs(configs) {
            ui {
                mediaSessionCompat.setRepeatMode(
                    when {
                        it.isTrue(CONFIG_PLAY_REPEAT) -> REPEAT_MODE_ALL
                        it.isTrue(CONFIG_PLAY_REPEAT_ONE) -> REPEAT_MODE_ONE
                        else -> REPEAT_MODE_NONE
                    }
                )
                mediaSessionCompat.setShuffleMode(
                    when {
                        it.isTrue(CONFIG_PLAY_SHUFFLE) -> SHUFFLE_MODE_ALL
                        else -> SHUFFLE_MODE_NONE
                    }
                )
            }
        }
        
        defaultAudioArt = ContextCompat.getDrawable(this, R.drawable.ic_round_audiotrack_200)!!.toBitmap()
        
        broadcastReceiver.register(this)
        
        exoPlayer.addListener(this)
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getStringExtra(ACTION_START_COMMAND)) {
            ACTION_START_COMMAND_PLAY -> {
                val audioArt = audioArt ?: defaultAudioArt
                when {
                    configs.isFalse(CONFIG_SERVICE_FOREGROUND) -> {
                        serviceNotification.startForeground(this, audioList.audioItem, true, audioArt)
                        configs[CONFIG_SERVICE_FOREGROUND] = true
                    }
                    
                    else -> serviceNotification.updateNotification(this, audioList.audioItem, true, audioArt)
                }
            }
            
            ACTION_START_COMMAND_PAUSE -> {
                serviceNotification.startForeground(this, audioList.audioItem, false, audioArt ?: defaultAudioArt)
                stopForeground(false)
                configs[CONFIG_SERVICE_FOREGROUND] = false
            }
            
            else -> handleIntent(mediaSessionCompat, intent)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?) = when {
        clientPackageName != APPLICATION_ID -> null
        else -> BrowserRoot(ROOT_ID, null)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.detach()
    }
    
    override fun onPlaybackStateChanged(playbackState: Int) {
        if (playbackState == STATE_ENDED) {
            when {
                configs[CONFIG_PLAY_REPEAT] -> transportControls.skipToNext()
                configs[CONFIG_PLAY_REPEAT_ONE] -> playAudioItem(audioList.audioItem)
                else -> {
                    if (!audioList.isLast) {
                        return transportControls.skipToNext()
                    }

                    playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                        .setState(STATE_BUFFERING, 0, DEFAULT_PLAYBACK_SPEED)
                        .build()
                    mediaSessionCompat.setPlaybackState(playbackStateCompat)

                    exoPlayer.setMediaItem(MediaItem.fromUri(audioList.audioItem.id.parseUri))
                    exoPlayer.prepare()

                    playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                        .setState(STATE_PAUSED, 0, DEFAULT_PLAYBACK_SPEED)
                        .build()
                    mediaSessionCompat.setPlaybackState(playbackStateCompat)

                    startSelf { putExtra(ACTION_START_COMMAND, ACTION_START_COMMAND_PAUSE) }
                }
            }
        }
    }

    private fun playAudioItem(audioItem: AudioItem) {
        exoPlayer.setMediaItem(MediaItem.fromUri(audioItem.id.parseUri))

        playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
            .setState(STATE_BUFFERING, 0, DEFAULT_PLAYBACK_SPEED)
            .build()
        mediaSessionCompat.setPlaybackState(playbackStateCompat)

        prepareAudio(audioItem)

        mediaMetadataCompat = MediaMetadataCompat.Builder()
            .putString(METADATA_KEY_TITLE, audioItem.title)
            .putString(METADATA_KEY_ARTIST, audioItem.artistName)
            .putString(METADATA_KEY_ALBUM, audioItem.albumTitle)
            .putString(METADATA_KEY_MEDIA_ID, audioItem.id)
            .apply { if (audioArt != null) putString(METADATA_KEY_ALBUM_ART_URI, audioItem.album.formUri) }
            .putBitmap(METADATA_KEY_ALBUM_ART, audioArt ?: defaultAudioArt)
            .putLong(METADATA_KEY_DURATION, audioItem.duration)
            .build()
        mediaSessionCompat.setMetadata(mediaMetadataCompat)

        transportControls.play()
    }

    private fun prepareAudio(audioItem: AudioItem) = runBlocking {
        io {
            audioArt = readAlbumArtLarge(audioItem.album)
        }
        exoPlayer.prepare()
    }

}