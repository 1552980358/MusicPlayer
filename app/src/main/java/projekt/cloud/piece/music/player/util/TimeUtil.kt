package projekt.cloud.piece.music.player.util

object TimeUtil {
    
    @JvmStatic
    val Long.minToMills get() = minToSec.secToMills
    
    @JvmStatic
    val Long.minToSec get() = this * 60
    
    @JvmStatic
    val Long.secToMills get() = this * 1000
    
}