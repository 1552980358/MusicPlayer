package projekt.cloud.piece.music.player.util

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * Object [DialogFragmentUtil]
 *
 * Methods
 *  [showNow]
 *
 **/
object DialogFragmentUtil {

    inline fun <reified DF: DialogFragment> DF.showNow(fragmentActivity: FragmentActivity) =
        showNow(fragmentActivity.supportFragmentManager, DF::class.simpleName)

    inline fun <reified DF: DialogFragment> DF.showNow(fragment: Fragment) =
        showNow(fragment.requireActivity())

}