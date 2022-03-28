package projekt.cloud.piece.music.player.util

import okhttp3.internal.format

object SizeUtil {

    private const val KILO_FORMAT = "%.2f"

    private const val KILO_BYTE = 1024
    val Long.asMegabyte get() = format(KILO_FORMAT, this.toFloat() / KILO_BYTE / KILO_BYTE)

    private const val KILO = 1000
    val Int.asKilo get() = format(KILO_FORMAT, this.toFloat() / KILO)


}