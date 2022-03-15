package projekt.cloud.piece.music.player.util

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.CustomActionCallback
import android.support.v4.media.session.MediaControllerCompat.TransportControls
import androidx.core.os.bundleOf
import projekt.cloud.piece.music.player.database.AudioDatabase
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.database.item.PlaylistItem
import projekt.cloud.piece.music.player.service.play.Action.ACTION_PLAY_CONFIG_CHANGED
import projekt.cloud.piece.music.player.service.play.Action.ACTION_REQUEST_LIST
import projekt.cloud.piece.music.player.service.play.Config
import projekt.cloud.piece.music.player.service.play.Config.PLAY_CONFIG_REPEAT
import projekt.cloud.piece.music.player.service.play.Config.shl
import projekt.cloud.piece.music.player.service.play.Extra.EXTRA_PLAY_CONFIG

class MainActivityInterface {

    lateinit var audioDatabase: AudioDatabase

    lateinit var itemClick: (Int) -> Unit
    lateinit var requestRefresh: () -> Unit
    lateinit var requestAudioItem: () -> AudioItem?
    fun setFragmentCallRequest(itemClick: (Int) -> Unit, requestRefresh: () -> Unit, requestAudioItem: () -> AudioItem?) {
        this.itemClick = itemClick
        this.requestRefresh = requestRefresh
        this.requestAudioItem = requestAudioItem
    }

    var isInitialized = false

    lateinit var audioList: List<AudioItem>
    lateinit var playlistList: List<PlaylistItem>
    lateinit var playlist: List<AudioItem>

    private lateinit var updateAudioItemMain: (AudioItem) -> Unit
    private var updateAudioItemPlay: ((AudioItem) -> Unit)? = null
    var audioItem: AudioItem? = null
        set(value) {
            field = value
            value?.let { audioItem ->
                updateAudioItemMain(audioItem)
                updateAudioItemPlay?.let { it(audioItem) }
            }
        }

    fun setUpMain(updateAudioItem: (AudioItem) -> Unit) {
        updateAudioItemMain = updateAudioItem
    }

    val albumBitmap40DpMap = mutableMapOf<String, Bitmap>()
    val audioBitmap40DpMap = mutableMapOf<String, Bitmap>()
    val playlistBitmap40DpMap = mutableMapOf<String, Bitmap>()

    lateinit var refreshStageChanged: () -> Unit
    lateinit var refreshCompleted: () -> Unit

    fun setRefreshListener(refreshStageChanged: () -> Unit,
                                refreshCompleted: () -> Unit) {
        this.refreshStageChanged = refreshStageChanged
        this.refreshCompleted = refreshCompleted
    }

    private lateinit var mediaBrowserCompat: MediaBrowserCompat
    private lateinit var transportControls: TransportControls
    fun setController(mediaBrowserCompat: MediaBrowserCompat, transportControls: TransportControls) {
        this.mediaBrowserCompat = mediaBrowserCompat
        this.transportControls = transportControls
    }

    fun play() = transportControls.play()
    fun pause() = transportControls.pause()
    fun skipToPrevious() = transportControls.skipToPrevious()
    fun skipToNext() = transportControls.skipToNext()
    fun skipToQueueItem(item: Int) = transportControls.skipToQueueItem(item.toLong())

    fun updateConfig(newConfig: Int, callback: (Bundle) -> Unit) = mediaBrowserCompat.sendCustomAction(
        ACTION_PLAY_CONFIG_CHANGED,
        bundleOf(EXTRA_PLAY_CONFIG to newConfig),
        object : CustomActionCallback() {
            override fun onResult(action: String?, extras: Bundle?, resultData: Bundle?) {
                resultData?.let(callback)
            }
        }
    )

    fun requestPlaylist(callback: (Bundle) -> Unit) = mediaBrowserCompat.sendCustomAction(
        ACTION_REQUEST_LIST,
        null,
        object : CustomActionCallback() {
            override fun onResult(action: String?, extras: Bundle?, resultData: Bundle?) {
                resultData?.let(callback)
            }
        }
    )
    
    lateinit var defaultAudioImage: Bitmap

    private var updatePlayConfigPlay: ((Int) -> Unit)? = null
    var playConfig = PLAY_CONFIG_REPEAT.shl
        set(value) {
            field = value
            updatePlayConfigPlay?.let { it(value) }
        }

    private var updatePlayStateMain: ((Boolean) -> Unit)? = null
    private var updatePlayStatePlay: ((Boolean) -> Unit)? = null
    var isPlaying = false
        set(value) {
            field = value
            updatePlayStateMain?.let { it(value) }
            updatePlayStatePlay?.let { it(value) }
        }

    private var updateProgressMain: ((Long) -> Unit)? = null
    private var updateProgressPlay: ((Long) -> Unit)? = null
    var progress = 0L
        set(value) {
            field = value
            updateProgressMain?.let { it(value) }
            updateProgressPlay?.let { it(value) }
        }

    fun setUpPlay(updateAudioItem: (AudioItem) -> Unit, updateProgress: (Long) -> Unit, updatePlayState: (Boolean) -> Unit, updatePlayConfig: (Int) -> Unit) {
        updateAudioItemPlay = updateAudioItem
        updateProgressPlay = updateProgress
        updatePlayStatePlay = updatePlayState
        updatePlayConfigPlay = updatePlayConfig
    }

    fun onDestroyPlay() {
        updateAudioItemPlay = null
        updateProgressPlay = null
        updatePlayStatePlay = null
    }

}