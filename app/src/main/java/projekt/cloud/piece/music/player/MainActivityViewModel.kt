package projekt.cloud.piece.music.player

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.CustomActionCallback
import android.support.v4.media.session.MediaControllerCompat
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job
import lib.github1552980358.ktExtension.android.content.isSystemDarkMode
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import projekt.cloud.piece.music.player.database.AudioDatabase
import projekt.cloud.piece.music.player.database.item.AlbumItem
import projekt.cloud.piece.music.player.database.item.ArtistItem
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.database.item.PlaylistItem
import projekt.cloud.piece.music.player.service.play.Action.ACTION_PLAY_CONFIG_CHANGED
import projekt.cloud.piece.music.player.service.play.Action.ACTION_REQUEST_LIST
import projekt.cloud.piece.music.player.service.play.Config.PLAY_CONFIG_REPEAT
import projekt.cloud.piece.music.player.service.play.Config.shl
import projekt.cloud.piece.music.player.service.play.Extra.EXTRA_LIST
import projekt.cloud.piece.music.player.service.play.Extra.EXTRA_CONFIGS
import projekt.cloud.piece.music.player.util.DatabaseUtil.syncAudioList

class MainActivityViewModel: ViewModel() {

    var isLoaded = false

    var isNightMode = false
    fun initialIsNightMode(context: Context) {
        if (!isLoaded) {
            isNightMode = context.isSystemDarkMode
        }
    }

    lateinit var database: AudioDatabase
    lateinit var defaultCoverArt: Bitmap

    lateinit var audioList: List<AudioItem>
    var audioArtMap = mutableMapOf<String, Bitmap>()
    var albumArtMap = mutableMapOf<String, Bitmap>()

    var isRefreshing = false
    private var _refreshObservers = mutableMapOf<String, (Boolean, List<AudioItem>?, List<AlbumItem>?, List<ArtistItem>?) -> Unit>()
    fun setRefreshObserver(tag: String, observer: ((Boolean, List<AudioItem>?, List<AlbumItem>?, List<ArtistItem>?) -> Unit)? = null) {
        when (observer) {
            null -> _refreshObservers.remove(tag)
            else -> _refreshObservers[tag] = observer
        }
    }
    fun requestRefreshDatabase(context: Context) {
        if (!isRefreshing) {
            io {
                isRefreshing = true
                _refreshObservers.forEach { (_, observer) -> observer(isRefreshing, null, null, null) }
                database.syncAudioList(context) { audioList, albumList, artistList ->
                    isRefreshing = false
                    _refreshObservers.forEach { (_, observer) -> observer(isRefreshing, audioList, albumList, artistList) }
                }
            }
        }
    }

    var hasSettingsUpdated = false

    var isPlaylistLoaded = false
    var playlistListUpdated = false
    lateinit var playlistList: ArrayList<PlaylistItem>
    lateinit var defaultPlaylistArt: Bitmap

    var audioItem: AudioItem? = null
        set(value) {
            field = value
            value?.let { _audioItemObservers.forEach { (_, observer) -> observer(it) } }
        }
    private var _audioItemObservers = mutableMapOf<String, (AudioItem) -> Unit>()
    fun setAudioItemObserver(tag: String, needValueInstant: Boolean = true, observer: ((AudioItem) -> Unit)? = null) {
        when (observer) {
            null -> _audioItemObservers.remove(tag)
            else -> {
                _audioItemObservers[tag] = observer
                if (needValueInstant) {
                    audioItem?.let { observer(it) }
                }
            }
        }
    }

    var isPlaying = false
        set(value) {
            field = value
            _playStateObservers.forEach { (_, observer) -> observer(value) }
        }
    private val _playStateObservers = mutableMapOf<String, (Boolean) -> Unit>()
    fun setPlayStateObserver(tag: String, needValueInstant: Boolean = true, observer: ((Boolean) -> Unit)? = null) {
        when (observer) {
            null -> _playStateObservers.remove(tag)
            else -> {
                _playStateObservers[tag] = observer
                if (needValueInstant) {
                    observer(isPlaying)
                }
            }
        }
    }

    var progress = 0L
        set(value) {
            field = value
            _progressObservers.forEach { (_, observer) -> observer(value) }
        }
    private val _progressObservers = mutableMapOf<String, (Long) -> Unit>()
    fun setProgressObservers(tag: String, needValueInstant: Boolean = true, observer: ((Long) -> Unit)? = null) {
        when (observer) {
            null -> _progressObservers.remove(tag)
            else -> {
                _progressObservers[tag] = observer
                if (needValueInstant) {
                    observer(progress)
                }
            }
        }
    }

    var playConfig = PLAY_CONFIG_REPEAT.shl
        set(value) {
            field = value
            _playConfigObservers.forEach { (_, observer) -> observer(value) }
        }
    private val _playConfigObservers = mutableMapOf<String, (Int) -> Unit>()
    fun setPlayConfigObserver(tag: String, needValueInstant: Boolean = true, observer: ((Int) -> Unit)? = null) {
        when (observer) {
            null -> _playConfigObservers.remove(tag)
            else -> {
                _playConfigObservers[tag] = observer
                if (needValueInstant) {
                    observer(playConfig)
                }
            }
        }
    }

    var playList: List<AudioItem>? = null
        set(value) {
            field = value
            value?.let { _playlistObservers.forEach { (_, observer) -> observer(it) } }
        }
    private val _playlistObservers = mutableMapOf<String, (List<AudioItem>) -> Unit>()
    fun setPlaylistObserver(tag: String, needValueInstant: Boolean = true, observer: ((List<AudioItem>) -> Unit)? = null) {
        when (observer) {
            null -> _playlistObservers.remove(tag)
            else -> {
                _playlistObservers[tag] = observer
                if (needValueInstant) {
                    playList?.let { observer(it) }
                }
            }
        }
    }

    var coverArtBitmap: Bitmap? = null
        set(value) {
            field = value
            value?.let { _coverArtBimapObservers.forEach { (_, observer) -> observer(it) } }
        }
    private val _coverArtBimapObservers = mutableMapOf<String, (Bitmap) -> Unit>()
    fun setCoverArtBitmapObserver(tag: String, needValueInstant: Boolean = true, observer: ((Bitmap) -> Unit)? = null) {
        when (observer) {
            null -> _coverArtBimapObservers.remove(tag)
            else -> {
                _coverArtBimapObservers[tag] = observer
                if (needValueInstant) {
                    coverArtBitmap?.let { observer(it) }
                }
            }
        }
    }

    fun removeAllObservers(tag: String) {
        setRefreshObserver(tag)
        setAudioItemObserver(tag)
        setPlayStateObserver(tag)
        setProgressObservers(tag)
        setPlayConfigObserver(tag)
        setPlaylistObserver(tag)
        setCoverArtBitmapObserver(tag)
    }

    fun updatePlayConfig(newConfig: Int) {
        mediaBrowserCompat.sendCustomAction(ACTION_PLAY_CONFIG_CHANGED, bundleOf(EXTRA_CONFIGS to newConfig), object : CustomActionCallback() {
            override fun onResult(action: String?, extras: Bundle?, resultData: Bundle?) {
                resultData?.let { resultExtra ->
                    @Suppress("UNCHECKED_CAST")
                    (resultExtra.getSerializable(EXTRA_LIST) as List<AudioItem>?)?.let { list ->
                        playList = list
                    }
                }
            }
        })
    }

    var requireSyncPlaylist = false
    fun getPlaylistSync() {
        if (mediaBrowserCompat.isConnected) {
            return mediaBrowserCompat.sendCustomAction(ACTION_REQUEST_LIST, null, object : CustomActionCallback() {
                override fun onResult(action: String?, extras: Bundle?, resultData: Bundle?) {
                    resultData?.let { resultExtra ->
                        @Suppress("UNCHECKED_CAST")
                        (resultExtra.getSerializable(EXTRA_LIST) as List<AudioItem>?)?.let { list ->
                            playList = list
                        }
                    }
                }
            })
        }
        requireSyncPlaylist = true
    }

    private var getContentCallback: ((Uri) -> Unit)? = null
    lateinit var getContent: ActivityResultLauncher<String>
    fun registerGetContent(activity: AppCompatActivity) {
        getContent = activity.registerForActivityResult(GetContent()) { uri ->
            uri?.let { dataUri -> getContentCallback?.let { it(dataUri) } }
        }
    }
    fun setGetContentCallback(callback: ((Uri) -> Unit)? = null) {
        this.getContentCallback = callback
    }

    lateinit var mediaBrowserCompat: MediaBrowserCompat
    lateinit var subscriptionCallback: MediaBrowserCompat.SubscriptionCallback
    lateinit var mediaControllerCallback: MediaControllerCompat.Callback
    lateinit var mediaControllerCompat: MediaControllerCompat

}