package projekt.cloud.piece.music.player.util

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class LifecycleProperty<T>: ReadWriteProperty<LifecycleOwner, T>, DefaultLifecycleObserver {

    private var _field: T? = null
    private val field: T
        get() = _field!!

    override fun setValue(thisRef: LifecycleOwner, property: KProperty<*>, value: T) {
        _field = value
    }

    override fun getValue(thisRef: LifecycleOwner, property: KProperty<*>) = field

    override fun onDestroy(owner: LifecycleOwner) {
        _field = null
    }

}