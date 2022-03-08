package projekt.cloud.piece.music.player.base

import androidx.fragment.app.Fragment
import projekt.cloud.piece.music.player.util.MainActivityInterface

open class BaseMainFragment: Fragment() {

    protected lateinit var activityInterface: MainActivityInterface

    fun setInterface(activityInterface: MainActivityInterface) {
        this.activityInterface = activityInterface
    }

}