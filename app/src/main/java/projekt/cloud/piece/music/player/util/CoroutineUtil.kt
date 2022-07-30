package projekt.cloud.piece.music.player.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object CoroutineUtil {

    val io = Dispatchers.IO
    val ui = Dispatchers.Main
    
    fun io(block: suspend CoroutineScope.() -> Unit) =
        CoroutineScope(io).launch(block = block)
    
    fun ui(block: suspend CoroutineScope.() -> Unit) =
        CoroutineScope(ui).launch(block = block)
    
    fun CoroutineScope.io(block: suspend CoroutineScope.() -> Unit) =
        launch(io, block = block)
    
    fun CoroutineScope.ui(block: suspend CoroutineScope.() -> Unit) =
        launch(ui, block = block)
    
    suspend fun <T> ioContext(block: suspend CoroutineScope.() -> T) =
        withContext(io, block)
    
}