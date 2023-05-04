package projekt.cloud.piece.music.player.util

object KotlinUtil {

    @Suppress("UNCHECKED_CAST")
    fun <T> Any?.to(): T = this as T

    inline fun <reified T> Any?.tryTo(): T? =
        this as? T

    inline val <T> T?.isNull: Boolean
        get() = this == null

    inline val <T> T?.isNotNull: Boolean
        get() = this != null

    inline fun <T> T?.ifNull(block: () -> Unit) {
        if (this == null) {
            block.invoke()
        }
    }

    inline fun <T> T?.ifNotNull(block: () -> Unit) {
        if (this != null) {
            block.invoke()
        }
    }

}