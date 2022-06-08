package projekt.cloud.piece.music.player.util

import android.app.Service
import android.content.Intent
import androidx.fragment.app.Fragment
import projekt.cloud.piece.music.player.util.ServiceUtil.serviceIntent

object FragmentUtil {
    
    @JvmStatic
    inline fun <reified S: Service> Fragment.startService(intent: Intent.() -> Unit) =
        requireContext().startService(requireContext().serviceIntent<S>(intent))
    
}