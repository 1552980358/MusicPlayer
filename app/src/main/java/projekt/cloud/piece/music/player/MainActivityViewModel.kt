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

    /**
     * Inner Class [Observer]
     * Final Variables:
     *  [tag]
     *   @type String
     *  [oneTimeUse]
     *   @type Boolean
     *   @default false
     *  [clazz]
     *   @type KClass
     *  [callback]
     *   @type (T>) -> Unit
     */
    class Observer<T: Any>(val tag: String,
                      val oneTimeUse: Boolean = false,
                      val clazz: KClass<T>,
                      val callback: (T?) -> Unit)

    private val observers = ArrayList<Observer<*>>()

    inline fun <reified T: Any> register(tag: String, oneTimeUse: Boolean, noinline callback: (T?) -> Unit) =
        register(Observer(tag, oneTimeUse, T::class, callback))
    fun <T: Any> register(observer: Observer<T>) =
        observers.add(observer)

    inline fun <reified T: Any> unregister(tag: String) =
        unregister(tag, T::class)
    fun <T: Any> unregister(tag: String, kClass: KClass<T>) =
        observers.removeAll { it.tag == tag && it.clazz == kClass }
    fun unregisterAll(tag: String) = observers.removeAll { it.tag == tag }

    private var audioList: List<AudioItem>? = null
        set(value) {
            field = value
            observers.forEach {
                if (it.clazz == AudioItem::class) {
                    @Suppress("UNCHECKED_CAST")
                    (it.callback as (List<AudioItem>?) -> Unit)(value)
                    if (it.oneTimeUse) {
                        observers.remove(it)
                    }
                }
            }
        }

}