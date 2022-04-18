package projekt.cloud.piece.music.player

import androidx.lifecycle.ViewModel
import projekt.cloud.piece.music.player.database.audio.item.AudioItem
import kotlin.reflect.KClass

/**
 * Class [MainActivityViewModel], inherit to [ViewModel]
 * Variables:
 *  [observers]
 *   @type ArrayList<Observer<*>>
 *  [audioList]
 *   @type List<AudioItem>
 *
 *  Methods:
 *   [register]
 *   [unregister]
 *   [unregisterAll]
 *
 * Inner Class [Observer]
 *
 **/
class MainActivityViewModel: ViewModel() {

    companion object {
        const val TAG_AUDIO_LIST = "TAG_AUDIO_LIST"
    }

    /**
     * Inner Class [Observer]
     * Final Variables:
     *  [tag]
     *   @type String
     *  [varTag]
     *   @type String
     *  [callback]
     *   @type (T>) -> Unit
     */
    private class Observer<T>(val tag: String, val varTag: String, val callback: (T?) -> Unit)
    private val observers = ArrayList<Observer<*>>()

    fun <T> register(tag: String, variableTag: String, callback: (T?) -> Unit) =
        observers.add(Observer(tag, variableTag, callback))
    fun unregister(tag: String, varTag: String) = observers.removeAll { it.tag == tag && it.varTag == varTag }
    fun unregisterAll(tag: String) = observers.removeAll { it.tag == tag }

    var audioList: List<AudioItem>? = null
        set(value) {
            field = value
            observers.forEach {
                if (it.varTag == TAG_AUDIO_LIST) {
                    @Suppress("UNCHECKED_CAST")
                    (it as Observer<List<AudioItem>?>).callback(value)
                }
            }
        }

}