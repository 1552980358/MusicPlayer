package projekt.cloud.piece.music.player.util

import android.content.Context

object UnitUtil {

    fun Int.dp2PxF(context: Context) = this * context.resources.displayMetrics.density
    fun Int.dp2Px(context: Context) = dp2PxF(context).toInt()

}