package projekt.cloud.piece.music.player.util

object TryUtil {
    
    class TryWrapper<R>(private val taskBlock: (TryWrapper<R>.() -> R)) {
        
        private var resultBlock: (R?) -> Unit = {}
        
        fun result(resultBlock: (R?) -> Unit) = apply {
            this.resultBlock = resultBlock
        }
        
        private var exceptionBlock: (Exception) -> Unit = {}
        
        fun exception(exceptionBlock: (Exception) -> Unit) {
            this.exceptionBlock = exceptionBlock
        }
        
        fun invoke() {
            val result: R?
            try {
                result = taskBlock.invoke(this)
            } catch (e: Exception) {
                return exceptionBlock.invoke(e)
            }
            resultBlock.invoke(result)
        }
    }
    
    fun <R> Try(block: TryWrapper<R>.() -> R) {
        TryWrapper(block).invoke()
    }
    
}