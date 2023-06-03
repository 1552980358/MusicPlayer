package projekt.cloud.piece.cloudy.util

import androidx.annotation.IntegerRes
import androidx.fragment.app.Fragment
import projekt.cloud.piece.cloudy.util.ResourcesUtil.getLong

object FragmentUtil {

    /**
     * [FragmentUtil.findParent]
     * @typeParam [T] extends [androidx.fragment.app.Fragment]
     * @return [T]
     **/
    inline fun <reified T: Fragment> Fragment.findParent(): T? {
        var parent = parentFragment
        while (parent != null && parent !is T) {
            parent = parent.parentFragment
        }
        return parent as? T
    }

    /**
     * [FragmentUtil.getLong]
     * @extends [androidx.fragment.app.Fragment]
     * @param resId
     * @return [Long]
     **/
    fun Fragment.getLong(@IntegerRes resId: Int): Long {
        return resources.getLong(resId)
    }

}