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
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.media.MediaBrowserServiceCompat
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayer.Builder
import com.google.android.exoplayer2.MediaItem.fromUri
import com.google.android.exoplayer2.Player.Listener
import com.google.android.exoplayer2.Player.STATE_ENDED
import projekt.cloud.piece.music.player.BuildConfig.APPLICATION_ID
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.service.play.Action.ACTION_PLAY_CONFIG_CHANGED
import projekt.cloud.piece.music.player.service.play.Action.ACTION_REQUEST_LIST
import projekt.cloud.piece.music.player.service.play.Action.ACTION_SYNC_SERVICE
import projekt.cloud.piece.music.player.service.play.Action.START_COMMAND_ACTION
import projekt.cloud.piece.music.player.service.play.Action.START_COMMAND_ACTION_PAUSE
import projekt.cloud.piece.music.player.service.play.Action.START_COMMAND_ACTION_PLAY
import projekt.cloud.piece.music.player.service.play.Config.FOREGROUND_SERVICE
import projekt.cloud.piece.music.player.service.play.Config.PLAY_CONFIG_REPEAT
import projekt.cloud.piece.music.player.service.play.Config.PLAY_CONFIG_REPEAT_ONE
import projekt.cloud.piece.music.player.service.play.Config.PLAY_CONFIG_SHUFFLE
import projekt.cloud.piece.music.player.service.play.Config.getConfig
import projekt.cloud.piece.music.player.service.play.Config.setConfig
import projekt.cloud.piece.music.player.service.play.Config.shl
import projekt.cloud.piece.music.player.service.play.Extra.EXTRA_INDEX
import projekt.cloud.piece.music.player.service.play.Extra.EXTRA_LIST
import projekt.cloud.piece.music.player.service.play.Extra.EXTRA_PLAY_CONFIG
import projekt.cloud.piece.music.player.service.play.MediaIdUtil.parseAsUri
import projekt.cloud.piece.music.player.service.play.NotificationUtil.createNotification
import projekt.cloud.piece.music.player.service.play.NotificationUtil.createNotificationManager
import projekt.cloud.piece.music.player.service.play.NotificationUtil.startForeground
import projekt.cloud.piece.music.player.service.play.NotificationUtil.update
import projekt.cloud.piece.music.player.service.play.SharedPreferences.SP_PLAY_CONFIG
import projekt.cloud.piece.music.player.util.ImageUtil.loadAlbumArtRaw
import projekt.cloud.piece.music.player.util.ImageUtil.loadAudioArtRaw
import projekt.cloud.piece.music.player.util.ServiceUtil.startService

class PlayService: MediaBrowserServiceCompat(), Listener {
    
    companion object {
        
        const val PlaybackStateActions =
            ACTION_PLAY_PAUSE or ACTION_STOP or ACTION_SEEK_TO or ACTION_PLAY_FROM_MEDIA_ID or ACTION_SKIP_TO_NEXT or ACTION_SKIP_TO_PREVIOUS or ACTION_SKIP_TO_QUEUE_ITEM
        
        private const val TAG = "PlayService"
        private const val ROOT_ID = TAG
        
    }
    
    private lateinit var mediaSession: MediaSessionCompat
    private val mediaSessionCallback = object : Callback() {
        override fun onPlay() {
            if (playbackStateCompat.state !in (STATE_PAUSED .. STATE_PLAYING) && playbackStateCompat.state != STATE_BUFFERING) {
                return
            }
    
            // Play
            exoPlayer.play()
            
            // Update state
            playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                .setState(STATE_PLAYING, playbackStateCompat.position, 1F)
                .build()
            mediaSession.setPlaybackState(playbackStateCompat)
            
            // Set service launch foreground keep playing
            startService { putExtra(START_COMMAND_ACTION, START_COMMAND_ACTION_PLAY) }
        }
    
        override fun onPause() {
            if (playbackStateCompat.state != STATE_PLAYING) {
                return
            }
            
            exoPlayer.pause()
    
            playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                .setState(STATE_PAUSED, exoPlayer.currentPosition, 1F)
                .build()
            mediaSession.setPlaybackState(playbackStateCompat)
            
            startService { putExtra(START_COMMAND_ACTION, START_COMMAND_ACTION_PAUSE) }
        }
    
        override fun onSkipToPrevious() {
            if (listIndex == 0) {
                if (!playConfig.getConfig(PLAY_CONFIG_REPEAT)
                    && !playConfig.getConfig(PLAY_CONFIG_REPEAT_ONE)) {
                    // just seek to pos 0
                    return onSeekTo(0L)
                }
                
                // Move to the last index
                listIndex = audioList.lastIndex
                return onPlayFromMediaId(audioList.last().id, null)
            }
            // Move to previous
            onPlayFromMediaId(audioList[--listIndex].id, null)
        }
        
        override fun onSkipToNext() {
            if (listIndex == audioList.lastIndex) {
                if (!playConfig.getConfig(PLAY_CONFIG_REPEAT)
                    && !playConfig.getConfig(PLAY_CONFIG_REPEAT_ONE)) {
                    // just seek to pos 0
                    return onSeekTo(0L)
                }
                // Move to the first index
                listIndex = 0
                return onPlayFromMediaId(audioList.last().id, null)
            }
            // Move to next
            onPlayFromMediaId(audioList[++listIndex].id, null)
        }
    
        override fun onSkipToQueueItem(id: Long) {
            listIndex = id.toInt()
            onPlayFromMediaId(audioList[listIndex].id, null)
        }
    
        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            mediaId ?: return
            extras?.let { extrasBundle ->
                if (extrasBundle.containsKey(EXTRA_LIST) && extrasBundle.containsKey(EXTRA_INDEX)) {
                    @Suppress("UNCHECKED_CAST")
                    audioList = extrasBundle.getSerializable(EXTRA_LIST) as List<AudioItem>
                    listIndex = extrasBundle.getInt(EXTRA_INDEX)
                    if (configs.getConfig(PLAY_CONFIG_SHUFFLE)) {
                        audioList = audioList.shuffled()
                        listIndex = audioList.indexOfFirst { it.index == listIndex }
                    }
                }
            }
            
            exoPlayer.stop()
            
            playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                .setState(STATE_BUFFERING, 0, 1F)
                .build()
            mediaSession.setPlaybackState(playbackStateCompat)
            val audioItem = audioList[listIndex]
            mediaSession.setMetadata(
                MediaMetadataCompat.Builder()
                    .putString(METADATA_KEY_MEDIA_ID, audioItem.id)
                    .putString(METADATA_KEY_TITLE, audioItem.title)
                    .putString(METADATA_KEY_ALBUM, audioItem.albumItem.title)
                    .putString(METADATA_KEY_ALBUM_ART_URI, audioItem.album)
                    .putLong(METADATA_KEY_DURATION, audioItem.duration)
                    .build()
            )
            
            exoPlayer.setMediaItem(fromUri(audioItem.id.parseAsUri))
            exoPlayer.prepare()
    
            onPlay()
        }
    
        override fun onSeekTo(pos: Long) {
    
            exoPlayer.seekTo(pos)
            
            playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                .setState(playbackStateCompat.state, pos, 1F)
                .build()
            mediaSession.setPlaybackState(playbackStateCompat)
            
        }
        
    }
    
    @Volatile
    private lateinit var playbackStateCompat: PlaybackStateCompat
    
    private val transportControls get() = mediaSession.controller.transportControls
    
    private lateinit var exoPlayer: ExoPlayer
    
    private lateinit var audioList: List<AudioItem>
    private var listIndex = 0
    
    private lateinit var sharedPreferences: SharedPreferences
    
    private var configs = 0
    
    private var playConfig = PLAY_CONFIG_REPEAT.shl
    
    private lateinit var defaultImage: Bitmap
    
    private lateinit var notificationManager: NotificationManagerCompat
    
    override fun onCreate() {
        super.onCreate()
    
        sharedPreferences = getDefaultSharedPreferences(this)
        if (sharedPreferences.contains(SP_PLAY_CONFIG)) {
            playConfig = sharedPreferences.getInt(SP_PLAY_CONFIG, playConfig)
        }
    
        // MediaBrowser Config
        playbackStateCompat = PlaybackStateCompat.Builder()
            .setState(STATE_NONE, 0, 1F)
            .setActions(PlaybackStateActions)
            .addCustomAction(ACTION_SYNC_SERVICE, ACTION_SYNC_SERVICE, R.drawable.ic_launcher_foreground)
            .addCustomAction(ACTION_PLAY_CONFIG_CHANGED, ACTION_PLAY_CONFIG_CHANGED, R.drawable.ic_launcher_foreground)
            .addCustomAction(ACTION_REQUEST_LIST, ACTION_REQUEST_LIST, R.drawable.ic_launcher_foreground)
            .setExtras(bundleOf(EXTRA_PLAY_CONFIG to playConfig))
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
    
        defaultImage = getDrawable(this, R.drawable.ic_music_big)!!.toBitmap()
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
        if (playbackState == STATE_ENDED) {
            when {
                playConfig.getConfig(PLAY_CONFIG_REPEAT) ->
                    mediaSession.controller.transportControls.skipToNext()
                
                playConfig.getConfig(PLAY_CONFIG_REPEAT_ONE) -> {
                    playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                        .setState(STATE_BUFFERING, 0, 1F)
                        .build()
                    exoPlayer.prepare()
                    transportControls.play()
                }
                
                else -> if (listIndex < audioList.lastIndex) {
                    transportControls.skipToNext()
                }
            }
        }
    }
    
    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?) = when (clientPackageName) {
        APPLICATION_ID -> BrowserRoot(TAG, null)
        else -> null
    }
    
    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.detach()
        result.sendResult(null)
    }
    
    override fun onCustomAction(action: String, extras: Bundle?, result: Result<Bundle>) {
        when (action) {
            ACTION_SYNC_SERVICE -> {
                result.sendResult(null)
                
                playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                    .setState(playbackStateCompat.state, exoPlayer.currentPosition, 1F)
                    .build()
                mediaSession.setPlaybackState(playbackStateCompat)
    
                if (::audioList.isInitialized) {
                    val audioItem = audioList[listIndex]
                    mediaSession.setMetadata(
                        MediaMetadataCompat.Builder()
                            .putString(METADATA_KEY_MEDIA_ID, audioItem.id)
                            .putString(METADATA_KEY_TITLE, audioItem.title)
                            .putString(METADATA_KEY_ALBUM, audioItem.albumItem.title)
                            .putString(METADATA_KEY_ALBUM_ART_URI, audioItem.album)
                            .putLong(METADATA_KEY_DURATION, audioItem.duration)
                            .build()
                    )
                }
            }
            ACTION_PLAY_CONFIG_CHANGED -> {
                extras?.let {
                    if (it.containsKey(EXTRA_PLAY_CONFIG)) {
                        playConfig = it.getInt(EXTRA_PLAY_CONFIG)
                        val currentAudioItem = audioList[listIndex]
                        when {
                            playConfig.getConfig(PLAY_CONFIG_SHUFFLE) -> audioList = audioList.shuffled()
                            else -> audioList = audioList.sortedBy { audioItem -> audioItem.index }
                        }
                        listIndex = audioList.indexOfFirst { it == currentAudioItem }
                        
                        playbackStateCompat = PlaybackStateCompat.Builder(playbackStateCompat)
                            .setState(playbackStateCompat.state, exoPlayer.currentPosition, 1F)
                            .setExtras(bundleOf(EXTRA_PLAY_CONFIG to playConfig))
                            .build()
                        
                        mediaSession.setPlaybackState(playbackStateCompat)
                        
                        sharedPreferences.edit()
                            .putInt(SP_PLAY_CONFIG, playConfig)
                            .apply()
    
                        result.sendResult(bundleOf(EXTRA_LIST to  currentPlaylist()))
                    }
                }
            }
            ACTION_REQUEST_LIST -> result.sendResult(bundleOf(EXTRA_LIST to currentPlaylist()))
        }
    }
    
    private fun currentPlaylist(): List<AudioItem> {
        if (listIndex == audioList.lastIndex) {
            return audioList
        }
        return arrayListOf<AudioItem>().apply {
            for (i in listIndex + 1 .. audioList.lastIndex) {
                add(audioList[i])
            }
            for (i in 0 .. listIndex) {
                add(audioList[i])
            }
        }
    }
    
}