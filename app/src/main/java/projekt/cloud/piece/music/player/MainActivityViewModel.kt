package projekt.cloud.piece.music.player

import android.graphics.Bitmap
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import projekt.cloud.piece.music.player.database.AudioDatabase
import projekt.cloud.piece.music.player.database.item.AudioItem

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

    private val _isPlaying = MutableLiveData(false)
    var isPlaying = false
        set(value) {
            field = value
            _playStateObservers.forEach { (_, observer) -> observer(value) }
        }
    private var _playStateObservers = mutableMapOf<String, (Boolean) -> Unit>()
    fun setPlayStateObserver(tag: String, observer: ((Boolean) -> Unit)?) {
        when (observer) {
            null -> _playStateObservers.remove(tag)
            else -> _playStateObservers[tag] = observer
        }
    }

    lateinit var mediaBrowserCompat: MediaBrowserCompat
    lateinit var subscriptionCallback: MediaBrowserCompat.SubscriptionCallback
    lateinit var mediaControllerCallback: MediaControllerCompat.Callback
    lateinit var mediaControllerCompat: MediaControllerCompat

}