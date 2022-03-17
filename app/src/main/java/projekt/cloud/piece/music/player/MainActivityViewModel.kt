package projekt.cloud.piece.music.player

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.CustomActionCallback
import android.support.v4.media.session.MediaControllerCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import projekt.cloud.piece.music.player.database.AudioDatabase
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.service.play.Action.ACTION_PLAY_CONFIG_CHANGED
import projekt.cloud.piece.music.player.service.play.Config.PLAY_CONFIG_REPEAT
import projekt.cloud.piece.music.player.service.play.Config.shl
import projekt.cloud.piece.music.player.service.play.Extra.EXTRA_LIST
import projekt.cloud.piece.music.player.service.play.Extra.EXTRA_PLAY_CONFIG

class MainActivityViewModel: ViewModel() {

    var isLoaded = false

    lateinit var database: AudioDatabase
    lateinit var defaultCoverArt: Bitmap

    lateinit var audioList: List<AudioItem>
    var audioArtMap = mutableMapOf<String, Bitmap>()
    var albumArtMap = mutableMapOf<String, Bitmap>()

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
    fun setPlayStateObserver(tag: String, needValueInstant: Boolean = true, observer: ((Boolean) -> Unit)?) {
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
    fun setProgressObservers(tag: String, needValueInstant: Boolean = true, observer: ((Long) -> Unit)?) {
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
    fun setPlayConfigObserver(tag: String, needValueInstant: Boolean = true, observer: ((Int) -> Unit)?) {
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
    fun setPlaylistObserver(tag: String, needValueInstant: Boolean = true, observer: ((List<AudioItem>) -> Unit)?) {
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

    fun updatePlayConfig(newConfig: Int) {
        mediaBrowserCompat.sendCustomAction(ACTION_PLAY_CONFIG_CHANGED, bundleOf(EXTRA_PLAY_CONFIG to newConfig), object : CustomActionCallback() {
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

    lateinit var mediaBrowserCompat: MediaBrowserCompat
    lateinit var subscriptionCallback: MediaBrowserCompat.SubscriptionCallback
    lateinit var mediaControllerCallback: MediaControllerCompat.Callback
    lateinit var mediaControllerCompat: MediaControllerCompat

}