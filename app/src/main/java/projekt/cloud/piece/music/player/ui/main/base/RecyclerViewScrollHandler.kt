package projekt.cloud.piece.music.player.ui.main.base

class RecyclerViewScrollHandler(private val onScrolledToBottom: () -> Unit,
                                private val onLeaveBottom: () -> Unit) {
    
    private var isBottom = false
    
    fun onScrolledToBottom() {
        if (!isBottom) {
            onScrolledToBottom.invoke()
            isBottom = true
        }
    }
    
    fun onLeaveBottom() {
        if (isBottom) {
            onLeaveBottom.invoke()
            isBottom = false
        }
    }
    
    fun clearState() = onLeaveBottom()
    
}