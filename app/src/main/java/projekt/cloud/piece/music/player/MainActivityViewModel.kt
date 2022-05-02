package projekt.cloud.piece.music.player

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_ALL
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_NONE
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job
import projekt.cloud.piece.music.player.database.audio.item.AudioItem
import projekt.cloud.piece.music.player.database.audio.item.ColorItem
import projekt.cloud.piece.music.player.util.AudioUtil.initialApplication
import projekt.cloud.piece.music.player.util.AudioUtil.launchApplication

/**
 * Class [MainActivityViewModel], inherit to [ViewModel]
 * Variables:
 *  [requestPermissions]
 *   @type Array<String>
 *  [onPermissionResult]
 *   @type (Map<String, Boolean>) -> Unit)?
 *   @default null
 *  [observers]
 *   @type ArrayList<Observer<*>>
 *  [audioItem]
 *   @type AudioItem
 *  [audioList]
 *   @type List<AudioItem>
 *  [isPlaying]
 *   @type Boolean
 *  [position]
 *   @type Int
 *
 *  Methods:
 *   [initialApplication]
 *   [launchApplication]
 *   [register]
 *   [unregister]
 *   [unregisterAll]
 *
 * Inner Class [Observer]
 *
 **/
class MainActivityViewModel: ViewModel() {

    companion object {
        const val LABEL_AUDIO_ITEM = "LABEL_AUDIO_ITEM"
        const val LABEL_AUDIO_LIST = "LABEL_AUDIO_LIST"
        const val LABEL_BITMAP_ART = "LABEL_BITMAP_ART"
        const val LABEL_COLOR_ITEM = "LABEL_COLOR_ITEM"
        const val LABEL_IS_PLAYING = "LABEL_IS_PLAYING"
        const val LABEL_REPEAT_MODE = "LABEL_REPEAT_MODE"
        const val LABEL_SHUFFLE_MODE = "LABEL_SHUFFLE_MODE"
        const val LABEL_POSITION = "LABEL_POSITION"
    }
    
    lateinit var requestPermissions: ActivityResultLauncher<Array<String>>
    private var onPermissionResult: ((Map<String, Boolean>) -> Unit)? = null
    fun setOnPermissionResult(onPermissionResult: (Map<String, Boolean>) -> Unit) {
        this.onPermissionResult = onPermissionResult
    }
    fun registerForActivityResult(appCompatActivity: AppCompatActivity) {
        requestPermissions = appCompatActivity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            onPermissionResult?.let { it(results) }
        }
    }
    
    private var job: Job? = null
    fun initialApplication(context: Context, callback: (List<AudioItem>?) -> Unit = {}) {
        job?.cancel()
        job = context.initialApplication {
            audioList = it
            callback(it)
            job = null
        }
    }
    fun launchApplication(context: Context, callback: (List<AudioItem>?) -> Unit = {}) {
        job?.cancel()
        job = context.launchApplication {
            audioList = it
            callback(it)
            job = null
        }
    }

    /**
     * Inner Class [Observer]
     * Final Variables:
     *  [owner]
     *   @type String
     *  [label]
     *   @type String
     *  [callback]
     *   @type (T>) -> Unit
     */
    private class Observer<T>(val owner: String, val label: String, val callback: (T?) -> Unit)
    private val observers = ArrayList<Observer<*>>()

    fun <T> register(tag: String, variableTag: String, callback: (T?) -> Unit) =
        observers.add(Observer(tag, variableTag, callback))
    fun unregister(tag: String, varTag: String) = observers.removeAll { it.owner == tag && it.label == varTag }
    fun unregisterAll(tag: String) = observers.removeAll { it.owner == tag }
    private fun <T> onObserved(label: String, value: T?) {
        observers.forEach {
            if (it.label == label) {
                @Suppress("UNCHECKED_CAST")
                (it as Observer<T>).callback(value)
            }
        }
    }
    
    var audioItem: AudioItem? = null
        set(value) {
            if (field != value) {
                field = value
                onObserved(LABEL_AUDIO_ITEM, value)
            }
        }

    var audioList: List<AudioItem>? = null
        set(value) {
            if (field != value) {
                field = value
                onObserved(LABEL_AUDIO_LIST, value)
            }
        }
    
    var bitmapArt: Bitmap? = null
        set(value) {
            if (field != value) {
                field = value
                onObserved(LABEL_BITMAP_ART, value)
            }
        }
    
    var colorItem: ColorItem? = null
        set(value) {
            if (field != value) {
                field = value
                onObserved(LABEL_COLOR_ITEM, value)
            }
        }
    
    var isPlaying = false
        set(value) {
            if (field != value) {
                field = value
                onObserved(LABEL_IS_PLAYING, value)
            }
        }

    var repeatMode = REPEAT_MODE_ALL
        set(value) {
            if (field != value) {
                field = value
                onObserved(LABEL_REPEAT_MODE, value)
            }
        }

    var shuffleMode = SHUFFLE_MODE_NONE
        set(value) {
            if (field != value) {
                field = value
                onObserved(LABEL_SHUFFLE_MODE, value)
            }
        }
    
    var position = 0L
        set(value) {
            if (field != value) {
                field = value
                onObserved(LABEL_POSITION, value)
            }
        }

}