package projekt.cloud.piece.cloudy.util

import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class LifecycleOwnerProperty<T>: ReadOnlyProperty<LifecycleOwner, T> {

    private var value: T? = null

    /**
     * [kotlin.properties.ReadOnlyProperty.getValue]
     * @param thisRef [androidx.lifecycle.LifecycleOwner]
     * @param property [kotlin.reflect.KProperty]
     * @return [T]
     *
     * Get value of the associated property;
     * Create if not created before
     **/
    override fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): T {
        return value ?: syncSetupValue(thisRef)
    }

    /**
     * [LifecycleOwnerProperty.syncSetupValue]
     * @param thisRef [androidx.lifecycle.LifecycleOwner]
     * @return [T]
     *
     * Save instance, or just return non-null instance
     **/
    @Synchronized
    protected fun syncSetupValue(thisRef: LifecycleOwner): T {
        return value ?: syncCreateValue(thisRef)
            .apply(::setValue)
    }

    /**
     * [LifecycleOwnerProperty.syncCreateValue]
     * @param thisRef [androidx.lifecycle.LifecycleOwner]
     * @return [T]
     *
     * Create value with provided [androidx.lifecycle.LifecycleOwner] instance
     **/
    protected abstract fun syncCreateValue(thisRef: LifecycleOwner): T

    /**
     * [LifecycleOwnerProperty.setValue]
     * @param newValue [T]
     *
     * Save instance
     **/
    @Synchronized
    protected fun setValue(newValue: T) {
        value = newValue
    }

}