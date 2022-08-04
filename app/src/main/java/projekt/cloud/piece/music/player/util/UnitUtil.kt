package projekt.cloud.piece.music.player.util

import android.content.res.Resources
import android.util.TypedValue

object UnitUtil {

    val Int.toPx: Float
        get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics)

}