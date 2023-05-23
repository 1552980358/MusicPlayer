package projekt.cloud.piece.cloudy.util

object CastUtil {

    fun <T> Any?.safeCast(): T? {
        @Suppress("UNCHECKED_CAST")
        return this as? T
    }

    fun <T> Any?.cast(): T {
        @Suppress("UNCHECKED_CAST")
        return this as T
    }

    fun <T> Any?.casting(block: (T) -> Unit) {
        safeCast<T>()?.let(block)
    }

}