package projekt.cloud.piece.music.player.util

object StringUtil {
    
    val Long.withZero: String
        get() = when {
            this > 9 -> toString()
            else -> "0$this"
        }
    
}