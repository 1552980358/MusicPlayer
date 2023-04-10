package projekt.cloud.piece.music.player.util

import android.content.res.Resources
import androidx.annotation.IntegerRes

object ResourceUtil {

    fun Resources.getLong(@IntegerRes resId: Int) =
        getInteger(resId).toLong()

}