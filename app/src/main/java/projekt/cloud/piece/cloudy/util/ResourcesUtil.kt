package projekt.cloud.piece.cloudy.util

import android.content.res.Resources
import androidx.annotation.IntegerRes

object ResourcesUtil {

    /**
     * [ResourcesUtil.getLong]
     * @extends [android.content.res.Resources]
     * @param resId
     * @return [Long]
     **/
    fun Resources.getLong(@IntegerRes resId: Int): Long {
        return getInteger(resId).toLong()
    }

}