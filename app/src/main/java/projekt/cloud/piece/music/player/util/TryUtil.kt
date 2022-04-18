package projekt.cloud.piece.music.player.util

object TryUtil {

    fun tryOnly(tryBlock: () -> Unit) =
        try { tryBlock() }
        catch (e: Exception) { e.printStackTrace() }

    fun <T> tryRun(tryBlock: () -> T?): T? =
        try { tryBlock() }
        catch (e: Exception) { null }

}