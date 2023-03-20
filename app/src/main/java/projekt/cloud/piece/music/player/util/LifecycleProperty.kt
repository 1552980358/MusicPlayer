package projekt.cloud.piece.music.player.util

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class LifecycleProperty<O, T>: ReadWriteProperty<O, T>, DefaultLifecycleObserver {

    companion object LifecyclePropertyUtil {
        fun <O, T> LifecycleOwner.lifecycleProperty(): LifecycleProperty<O, T> {
            return LifecycleProperty(this)
        }
    }

    private var _field: T? = null
    protected val field: T
        get() = _field!!

    constructor(): super()
    constructor(lifecycle: Lifecycle): super() {
        @Suppress("LeakingThis")
        lifecycle.addObserver(this)
    }
    constructor(lifecycleOwner: LifecycleOwner): this(lifecycleOwner.lifecycle)

    override fun setValue(thisRef: O, property: KProperty<*>, value: T) {
        _field = value
    }

    override fun getValue(thisRef: O, property: KProperty<*>) = field

    override fun onDestroy(owner: LifecycleOwner) {
        _field = null
    }

}