package projekt.cloud.piece.music.player

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job
import projekt.cloud.piece.music.player.database.audio.item.AudioItem
import projekt.cloud.piece.music.player.util.AudioUtil.initialApplication
import projekt.cloud.piece.music.player.util.AudioUtil.launchApplication

/**
 * Class [MainActivityViewModel], inherit to [ViewModel]
 * Variables:
 *  [observers]
 *   @type ArrayList<Observer<*>>
 *  [audioList]
 *   @type List<AudioItem>
 *  [requestPermissions]
 *   @type Array<String>
 *  [onPermissionResult]
 *   @type (Map<String, Boolean>) -> Unit)?
 *   @default null
 *
 *  Methods:
 *   [register]
 *   [unregister]
 *   [unregisterAll]
 *   [initialApplication]
 *   [launchApplication]
 *
 * Inner Class [Observer]
 *
 **/
class MainActivityViewModel: ViewModel() {

    companion object {
        const val LABEL_AUDIO_LIST = "LABEL_AUDIO_LIST"
        const val LABEL_IS_PLAYING = "LABEL_IS_PLAYING"
        const val LABEL_POSITION = "LABEL_POSITION"
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

    private var job: Job? = null
    fun initialApplication(context: Context, callback: (List<AudioItem>?) -> Unit) {
        job?.cancel()
        job = context.initialApplication {
            audioList = it
            callback(it)
            job = null
        }
    }
    fun launchApplication(context: Context, callback: (List<AudioItem>?) -> Unit) {
        job?.cancel()
        job = context.launchApplication {
            audioList = it
            callback(it)
            job = null
        }
    }

    var audioList: List<AudioItem>? = null
        set(value) {
            field = value
            onObserved(LABEL_AUDIO_LIST, value)
        }
    
    var isPlaying = false
        set(value) {
            field = value
            onObserved(LABEL_IS_PLAYING, value)
        }
    
    var position = 0L
        set(value) {
            field = value
            onObserved(LABEL_POSITION, value)
        }

    /***********************************************************/
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

}