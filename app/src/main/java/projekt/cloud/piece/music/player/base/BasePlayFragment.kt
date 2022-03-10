package projekt.cloud.piece.music.player.base

import androidx.fragment.app.Fragment
import projekt.cloud.piece.music.player.util.PlayActivityInterface

open class BasePlayFragment: Fragment() {
    
    protected lateinit var activityInterface: PlayActivityInterface
    fun setInterface(activityInterface: PlayActivityInterface) {
        this.activityInterface = activityInterface
    }
    
}