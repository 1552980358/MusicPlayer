package projekt.cloud.piece.music.player.ui.main.base

import projekt.cloud.piece.music.player.base.BaseFragment

open class BaseMainFragment: BaseFragment() {
    
    protected open fun onScrolledToBottom() {
        (parentFragment as? BaseMainFragment)?.onScrolledToBottom()
    }
    
    protected open fun onLeaveBottom() {
        (parentFragment as? BaseMainFragment)?.onLeaveBottom()
    }

}