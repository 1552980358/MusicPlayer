package projekt.cloud.piece.music.player.ui.main.base

import projekt.cloud.piece.music.player.base.BaseFragment

/**
 * [BaseMainFragment]
 * inherit to [BaseFragment]
 *
 * Method:
 * [isRecyclerViewBottom]
 **/
open class BaseMainFragment: BaseFragment() {
    
    open val isRecyclerViewBottom get() = false

}