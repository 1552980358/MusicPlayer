package projekt.cloud.piece.music.player.ui.main.base

import projekt.cloud.piece.music.player.base.BaseFragment

open class BaseMainFragment: BaseFragment() {

    private lateinit var recyclerViewScrollHandler: RecyclerViewScrollHandler
    fun setRecyclerViewScrollHandler(recyclerViewScrollHandler: RecyclerViewScrollHandler) {
        this.recyclerViewScrollHandler = recyclerViewScrollHandler
    }
    
    protected fun onScrolledToBottom() = recyclerViewScrollHandler.onScrolledToBottom()
    
    protected fun onLeaveBottom() = recyclerViewScrollHandler.onLeaveBottom()

}