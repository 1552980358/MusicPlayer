package projekt.cloud.piece.music.player.ui.main.base

class RecyclerViewScrollHandler(private val onScrolledToBottom: () -> Unit,
                                private val onLeaveBottom: () -> Unit) {
    
    private var isBottom = false
    
    fun onScrolledToBottom() {
        if (!isBottom) {
            isBottom = true
            onScrolledToBottom.invoke()
        }
    }
    
    fun onLeaveBottom() {
        if (isBottom) {
            isBottom = false
            onLeaveBottom.invoke()
        }
    }
    
    fun clearState() = onLeaveBottom()
    
}