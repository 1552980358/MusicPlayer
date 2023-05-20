package projekt.cloud.piece.cloudy.util

import androidx.fragment.app.Fragment

object FragmentUtil {

    inline fun <reified T: Fragment> Fragment.findParent(): T? {
        var parent = parentFragment
        while (parent != null && parent !is T) {
            parent = parent.parentFragment
        }
        return parent as? T
    }

}