package projekt.cloud.piece.cloudy.util.helper

import java.io.Closeable
import projekt.cloud.piece.cloudy.util.implementation.Releasable

/**
 * [NullableHelper]
 * @interface [Releasable], [Closeable]
 **/
class NullableHelper<T> private constructor(
    private var _value: T?
): Releasable, Closeable {

    companion object NullableHelperUtil {

        /**
         * [NullableHelper.nullable]
         * @param value [T]
         * @return [NullableHelper]<[T]>
         *
         * Create [NullableHelper] instance
         **/
        fun <T> nullable(value: T? = null): NullableHelper<T> {
            return NullableHelper(value)
        }

    }

    /**
     * [NullableHelper.setValue]
     * @param value [T]
     *
     * Store instance [value] to [_value]
     **/
    fun setValue(value: T) {
        _value = value
    }

    /**
     * [NullableHelper.getValue]
     * @return [T]
     *
     * Getting non-null [_value]
     **/
    fun getValue(): T {
        return nullable()!!
    }

    /**
     * [NullableHelper.nullable]
     * @return [T]
     *
     * Getting nullable [_value]
     **/
    fun nullable(): T? {
        return _value
    }

    /**
     * [NullableHelper.value]
     * @param block [kotlin.jvm.functions.Function1]<[T], [Unit]>
     *
     * Get value
     **/
    inline infix fun value(block: (T) -> Unit) {
        nullable()?.let(block)
    }

    /**
     * [NullableHelper.value]
     * @param value [T]
     **/
    infix fun value(value: T) {
        setValue(value)
    }

    /**
     * [Releasable.release]
     **/
    override fun release() {
        _value = null
    }

    /**
     * [Closeable.close]
     **/
    override fun close() = release()

}