package projekt.cloud.piece.music.player.util

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

object DialogFragmentUtil {
    
    inline fun <reified DF: DialogFragment> DF.showNow(fragmentManager: FragmentManager) =
        showNow(fragmentManager, DF::class.simpleName)
    
}