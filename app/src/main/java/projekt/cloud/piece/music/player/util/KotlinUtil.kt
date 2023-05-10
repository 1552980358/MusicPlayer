package projekt.cloud.piece.music.player.util

object KotlinUtil {

    @Suppress("UNCHECKED_CAST")
    fun <T> Any?.to(): T = this as T

    inline fun <reified T> Any?.tryTo(): T? =
        this as? T

    inline fun <reified T> Any?.tryTo(block: (T) -> Unit): T? {
        return tryTo<T>()?.also(block)
    }

    inline val <T> T?.isNull: Boolean
        get() = this == null

    inline fun <T> T?.ifNull(block: () -> Unit) {
        if (this.isNull) {
            block.invoke()
        }
    }

    inline fun <T> T?.ifNotNull(block: (T) -> Unit) {
        this?.let { block.invoke(it) }
    }

    inline val Boolean?.isTrue: Boolean
        get() = this == true

    inline val Boolean?.ifFalse: Boolean
        get() = this == false

    fun Boolean?.ifFalse(block: () -> Unit) {
        if (this.ifFalse) {
            block.invoke()
        }
    }

}