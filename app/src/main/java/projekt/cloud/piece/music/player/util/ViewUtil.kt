package projekt.cloud.piece.music.player.util

import android.view.View

object ViewUtil {

    val View.canScrollUp: Boolean
        get() = canScrollVertically(-1)

}