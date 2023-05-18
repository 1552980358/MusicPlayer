package projekt.cloud.piece.cloudy.util

object TypeUtil {

    fun <T> Any?.mayType(): T? {
        @Suppress("UNCHECKED_CAST")
        return this as? T
    }

    fun <T> Any?.toType(): T {
        @Suppress("UNCHECKED_CAST")
        return this as T
    }

}