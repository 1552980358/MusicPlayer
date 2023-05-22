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
    fun setValue(value: T): T {
        _value = value
        return value
    }

    /**
     * [NullableHelper.nonnull]
     * @return [T]
     *
     * Getting nonnull [_value]
     **/
    fun nonnull(): T {
        return nullable()!!
    }

    inline fun <R> nonnull(block: (T) -> R): R {
        return block.invoke(nonnull())
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
     * [NullableHelper.safely]
     * @param block [kotlin.jvm.functions.Function1]<[T], [Unit]>
     *
     * Get value
     **/
    inline infix fun safely(block: (T) -> Unit) {
        nullable()?.let(block)
    }

    /**
     * [NullableHelper.valued]
     * @param value [T]
     **/
    infix fun valued(value: T): T {
        return setValue(value)
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