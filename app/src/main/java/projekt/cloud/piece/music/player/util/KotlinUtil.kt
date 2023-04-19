package projekt.cloud.piece.music.player.util


object KotlinUtil {

    @Suppress("UNCHECKED_CAST")
    fun <T> Any.to(): T = this as T

    inline fun <reified T> Any?.tryTo(): T? =
        this as? T

}