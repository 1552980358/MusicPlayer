package projekt.cloud.piece.music.player.service

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE
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
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.media.MediaBrowserServiceCompat
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayer.Builder
import com.google.android.exoplayer2.ExoPlayer.STATE_ENDED
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player.Listener
import kotlinx.coroutines.Job
import lib.github1552980358.ktExtension.android.content.broadcastReceiver
import lib.github1552980358.ktExtension.android.content.register
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import projekt.cloud.piece.music.player.BuildConfig.APPLICATION_ID
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.service.play.Action.ACTION_PLAY_CONFIG_CHANGED
import projekt.cloud.piece.music.player.service.play.Action.ACTION_REQUEST_LIST
import projekt.cloud.piece.music.player.service.play.Action.ACTION_SYNC_SERVICE
import projekt.cloud.piece.music.player.service.play.Action.BROADCAST_ACTION_NEXT
import projekt.cloud.piece.music.player.service.play.Action.BROADCAST_ACTION_PAUSE
import projekt.cloud.piece.music.player.service.play.Action.BROADCAST_ACTION_PLAY
import projekt.cloud.piece.music.player.service.play.Action.BROADCAST_ACTION_PREV
import projekt.cloud.piece.music.player.service.play.Action.START_COMMAND_ACTION
import projekt.cloud.piece.music.player.service.play.Action.START_COMMAND_ACTION_PAUSE
import projekt.cloud.piece.music.player.service.play.Action.START_COMMAND_ACTION_PLAY
import projekt.cloud.piece.music.player.service.play.Config.PLAY_CONFIG_REPEAT
import projekt.cloud.piece.music.player.service.play.Config.PLAY_CONFIG_REPEAT_ONE
import projekt.cloud.piece.music.player.service.play.Config.PLAY_CONFIG_SHUFFLE
import projekt.cloud.piece.music.player.service.play.Config.SERVICE_CONFIG_FOREGROUND_SERVICE
import projekt.cloud.piece.music.player.service.play.Config.getConfig
import projekt.cloud.piece.music.player.service.play.Config.setConfig
import projekt.cloud.piece.music.player.service.play.Extra.EXTRA_CONFIGS
import projekt.cloud.piece.music.player.service.play.Extra.EXTRA_INDEX
import projekt.cloud.piece.music.player.service.play.Extra.EXTRA_LIST
import projekt.cloud.piece.music.player.service.play.MediaIdUtil.parseAsUri
import projekt.cloud.piece.music.player.service.play.NotificationUtil.createNotification
import projekt.cloud.piece.music.player.service.play.NotificationUtil.createNotificationManager
import projekt.cloud.piece.music.player.service.play.NotificationUtil.startForeground
import projekt.cloud.piece.music.player.service.play.NotificationUtil.update
import projekt.cloud.piece.music.player.service.play.SharedPreferencesUtil.DEFAULT_CONFIG
import projekt.cloud.piece.music.player.service.play.SharedPreferencesUtil.hasConfig
import projekt.cloud.piece.music.player.service.play.SharedPreferencesUtil.readConfigs
import projekt.cloud.piece.music.player.service.play.SharedPreferencesUtil.writeConfigs
import projekt.cloud.piece.music.player.util.ImageUtil.loadAlbumArtRaw
import projekt.cloud.piece.music.player.util.ImageUtil.loadAudioArtRaw
import projekt.cloud.piece.music.player.util.ServiceUtil.startService

class PlayService: MediaBrowserServiceCompat(), Listener {
    
    companion object {
        
        const val PLAYBACK_STATE_ACTIONS =
            ACTION_PLAY_PAUSE or ACTION_STOP or ACTION_SEEK_TO or ACTION_PLAY_FROM_MEDIA_ID or ACTION_SKIP_TO_NEXT or ACTION_SKIP_TO_PREVIOUS or ACTION_SKIP_TO_QUEUE_ITEM
        
        private const val TAG = "PlayService"
        private const val ROOT_ID = TAG

        private const val DEFAULT_PLAYBACK_SPEED = 1F
        
    }

    private lateinit var playbackStateCompat: PlaybackStateCompat
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private val mediaSessionCallback = object : Callback() {

        override fun onPlay() {
            Log.e(TAG, "onPlay")

            if (playbackStateCompat.state != STATE_BUFFERING && playbackStateCompat.state != STATE_PAUSED) {
                return
            }

            exoPlayer.play()

            playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                .setState(STATE_PLAYING, exoPlayer.currentPosition, DEFAULT_PLAYBACK_SPEED)
                .build()
            mediaSessionCompat.setPlaybackState(playbackStateCompat)

            startService { putExtra(START_COMMAND_ACTION, START_COMMAND_ACTION_PLAY) }
        }

        override fun onPause() {
            Log.e(TAG, "onPause")

            if (playbackStateCompat.state != STATE_BUFFERING && playbackStateCompat.state != STATE_PLAYING) {
                return
            }

            exoPlayer.pause()

            playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                .setState(STATE_PAUSED, exoPlayer.currentPosition, DEFAULT_PLAYBACK_SPEED)
                .build()
            mediaSessionCompat.setPlaybackState(playbackStateCompat)

            startService { putExtra(START_COMMAND_ACTION, START_COMMAND_ACTION_PAUSE) }
        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            Log.e(TAG, "onPlayFromMediaId")

            extras?.let { bundle ->
                if (bundle.containsKey(EXTRA_LIST) && bundle.containsKey(EXTRA_INDEX)) {
                    @Suppress("UNCHECKED_CAST")
                    playlist = bundle.getSerializable(EXTRA_LIST) as List<AudioItem>
                    current = bundle.getInt(EXTRA_INDEX)
                    if (configs.getConfig(PLAY_CONFIG_SHUFFLE)) {
                        playlist = playlist.shuffled()
                        current = playlist.indexOfFirst { it.index == current }
                        playlist.forEachIndexed { index, audioItem -> audioItem.index = index }
                    }
                }
            }
            if (::playlist.isInitialized && current != -1) {
                onPlayAudioItem(playlist[current])
            }
        }

        override fun onSkipToPrevious() {
            Log.e(TAG, "onSkipToPrevious")

            if (!::playlist.isInitialized || current == -1) {
                return
            }
            if (current == 0) {
                if (!configs.getConfig(PLAY_CONFIG_REPEAT) && !configs.getConfig(PLAY_CONFIG_REPEAT_ONE)) {
                    return onSeekTo(0)
                }
                current = playlist.lastIndex
                return onPlayAudioItem(playlist[current])
            }
            onPlayAudioItem(playlist[--current])
        }

        override fun onSkipToNext() {
            Log.e(TAG, "onSkipToNext")

            if (!::playlist.isInitialized || current == -1) {
                return
            }
            if (current == playlist.lastIndex) {
                if (!configs.getConfig(PLAY_CONFIG_REPEAT) && !configs.getConfig(PLAY_CONFIG_REPEAT_ONE)) {
                    return onSeekTo(0)
                }
                current = 0
                return onPlayAudioItem(playlist[current])
            }
            onPlayAudioItem(playlist[++current])
        }

        override fun onSkipToQueueItem(id: Long) {
            current = id.toInt()
            onPlayAudioItem(playlist[current])
        }

        override fun onSeekTo(pos: Long) {
            Log.e(TAG, "onSeekTo $pos")

            exoPlayer.seekTo(pos)
            playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                .setState(playbackStateCompat.state, pos, DEFAULT_PLAYBACK_SPEED)
                .build()
            mediaSessionCompat.setPlaybackState(playbackStateCompat)
        }

        private fun onPlayAudioItem(audioItem: AudioItem) {
            Log.e(TAG, "onPlayAudioItem ${audioItem.id} ${audioItem.title}")

            exoPlayer.stop()

            playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                .setState(STATE_BUFFERING, 0, DEFAULT_PLAYBACK_SPEED)
                .build()
            mediaSessionCompat.setPlaybackState(playbackStateCompat)

            mediaSessionCompat.setMetadata(
                MediaMetadataCompat.Builder()
                    .putString(METADATA_KEY_MEDIA_ID, audioItem.id)
                    .putString(METADATA_KEY_TITLE, audioItem.title)
                    .putString(METADATA_KEY_ALBUM, audioItem.albumItem.title)
                    .putString(METADATA_KEY_ALBUM_ART_URI, audioItem.album)
                    .putLong(METADATA_KEY_DURATION, audioItem.duration)
                    .build()
            )

            exoPlayer.setMediaItem(MediaItem.fromUri(audioItem.id.parseAsUri))
            exoPlayer.prepare()

            onPlay()
        }

    }

    private val broadcastReceiver = broadcastReceiver { _, intent, _ ->
        when (intent?.action) {
            BROADCAST_ACTION_PLAY -> mediaSessionCompat.controller.transportControls.play()
            BROADCAST_ACTION_PAUSE -> mediaSessionCompat.controller.transportControls.pause()
            BROADCAST_ACTION_PREV -> mediaSessionCompat.controller.transportControls.skipToPrevious()
            BROADCAST_ACTION_NEXT -> mediaSessionCompat.controller.transportControls.skipToNext()
        }
    }

    private lateinit var exoPlayer: ExoPlayer

    private lateinit var playlist: List<AudioItem>
    private var current = -1

    private lateinit var sharedPreferences: SharedPreferences
    private var configs = DEFAULT_CONFIG

    private lateinit var notificationManagerCompat: NotificationManagerCompat

    private lateinit var defaultCoverArt: Bitmap
    private lateinit var currentCoverArt: Bitmap
    private var loadCoverArtJob: Job? = null

    override fun onCreate() {
        super.onCreate()

        sharedPreferences = getDefaultSharedPreferences(this)
        if (sharedPreferences.hasConfig) {
            configs = sharedPreferences.readConfigs
        }

        playbackStateCompat = PlaybackStateCompat.Builder()
            .setState(STATE_NONE, 0, DEFAULT_PLAYBACK_SPEED)
            .setActions(PLAYBACK_STATE_ACTIONS)
            .addCustomAction(ACTION_SYNC_SERVICE, ACTION_SYNC_SERVICE, R.drawable.ic_launcher_foreground)
            .addCustomAction(ACTION_PLAY_CONFIG_CHANGED, ACTION_PLAY_CONFIG_CHANGED, R.drawable.ic_launcher_foreground)
            .addCustomAction(ACTION_REQUEST_LIST, ACTION_REQUEST_LIST, R.drawable.ic_launcher_foreground)
            .setExtras(bundleOf(EXTRA_CONFIGS to configs))
            .build()

        mediaSessionCompat = MediaSessionCompat(this, ROOT_ID).apply {
            setCallback(mediaSessionCallback)
            setPlaybackState(playbackStateCompat)
        }

        sessionToken = mediaSessionCompat.sessionToken

        exoPlayer = Builder(this).build()
        exoPlayer.addListener(this)

        broadcastReceiver.register(this, BROADCAST_ACTION_PLAY, BROADCAST_ACTION_PAUSE, BROADCAST_ACTION_PREV, BROADCAST_ACTION_NEXT)

        notificationManagerCompat = createNotificationManager

        defaultCoverArt = getDrawable(resources, R.drawable.ic_music, null)!!.toBitmap()
        currentCoverArt = defaultCoverArt

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getStringExtra(START_COMMAND_ACTION)) {
            START_COMMAND_ACTION_PLAY -> {
                if (!::playlist.isInitialized || current !in 0 .. playlist.lastIndex) {
                    return super.onStartCommand(intent, flags, startId)
                }
                when {
                    !configs.getConfig(SERVICE_CONFIG_FOREGROUND_SERVICE) -> {
                        configs = configs.setConfig(SERVICE_CONFIG_FOREGROUND_SERVICE, true)
                        startForeground(createNotification(playlist[current], false, currentCoverArt))
                    }
                    else -> notificationManagerCompat.update(createNotification(playlist[current], false, currentCoverArt))
                }
                loadCoverArtJob?.cancel()
                loadCoverArtJob = loadCoverArt(playlist[current], false)
            }
            START_COMMAND_ACTION_PAUSE -> {
                startForeground(createNotification(playlist[current], true, currentCoverArt))
                loadCoverArtJob?.cancel()
                loadCoverArtJob = loadCoverArt(playlist[current], true)
                stopForeground(false)
            }
            else -> Unit
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }

    override fun onCustomAction(action: String, extras: Bundle?, result: Result<Bundle>) {
        when (action) {
            ACTION_PLAY_CONFIG_CHANGED -> {
                if (extras == null || !extras.containsKey(EXTRA_CONFIGS)) {
                    return result.sendResult(null)
                }
                configs = extras.getInt(EXTRA_CONFIGS)

                // Update list and current index
                val currentAudioItem = playlist[current]
                playlist = when {
                    configs.getConfig(PLAY_CONFIG_SHUFFLE) -> playlist.shuffled()
                    else -> playlist.sortedBy { it.pinyin }
                }
                current = playlist.indexOfFirst { it.id == currentAudioItem.id }
                playlist.forEachIndexed { index, audioItem -> audioItem.index = index }

                playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                    .setState(playbackStateCompat.state, exoPlayer.currentPosition, DEFAULT_PLAYBACK_SPEED)
                    .setExtras(bundleOf(EXTRA_CONFIGS to configs))
                    .build()
                mediaSessionCompat.setPlaybackState(playbackStateCompat)

                sharedPreferences.writeConfigs(configs)

                result.sendResult(bundleOf(EXTRA_LIST to arrangedPlaylist))
            }
            ACTION_REQUEST_LIST -> result.sendResult(bundleOf(EXTRA_LIST to arrangedPlaylist))
            else -> result.sendError(null)
        }
    }

    private val arrangedPlaylist: List<AudioItem> get() {
        if (current == playlist.lastIndex) {
            return arrayListOf(*playlist.toTypedArray())
        }
        return arrayListOf<AudioItem>().apply {
            for (i in current + 1 .. playlist.lastIndex) {
                add(playlist[i])
            }
            for (i in 0 .. current) {
                add(playlist[i])
            }
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        if (playbackState == STATE_ENDED) {
            when {

                configs.getConfig(PLAY_CONFIG_REPEAT_ONE) ->
                    mediaSessionCompat.controller.transportControls.playFromMediaId(playlist[current].id, null)

                configs.getConfig(PLAY_CONFIG_REPEAT) ->
                    mediaSessionCompat.controller.transportControls.skipToNext()

                else -> {
                    if (current < playlist.lastIndex) {
                        return mediaSessionCompat.controller.transportControls.skipToNext()
                    }

                    playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                        .setState(STATE_BUFFERING, 0, DEFAULT_PLAYBACK_SPEED)
                        .build()
                    mediaSessionCompat.setPlaybackState(playbackStateCompat)

                    exoPlayer.setMediaItem(MediaItem.fromUri(playlist[current].id.parseAsUri))
                    exoPlayer.prepare()

                    playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                        .setState(STATE_PAUSED, 0, DEFAULT_PLAYBACK_SPEED)
                        .build()
                    mediaSessionCompat.setPlaybackState(playbackStateCompat)

                    startService { putExtra(START_COMMAND_ACTION, START_COMMAND_ACTION_PAUSE) }
                }

            }
        }
    }

    private fun loadCoverArt(audioItem: AudioItem, isPaused: Boolean) = io {
        currentCoverArt = loadAudioArtRaw(audioItem.id) ?: loadAlbumArtRaw(audioItem.album) ?: defaultCoverArt
        notificationManagerCompat.update(createNotification(audioItem, isPaused, currentCoverArt))
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?) = when (clientPackageName) {
        APPLICATION_ID -> BrowserRoot(TAG, null)
        else -> null
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(null)
    }

}