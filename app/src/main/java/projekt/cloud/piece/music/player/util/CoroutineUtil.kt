package projekt.cloud.piece.music.player.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Object [CoroutineUtil]
 *
 * Getters:
 *   [ui]
 *   [io]
 *   [uiScope]
 *   [ioScope]
 *
 * Methods:
 *   [ui]
 *   [io]
 * 
 **/
object CoroutineUtil {

    val ui get() = Main
    val io get() = IO

    private val uiScope = CoroutineScope(Main)
    private val ioScope = CoroutineScope(IO)

    @JvmStatic
    fun ui(block: suspend CoroutineScope.() -> Unit) =
        uiScope.launch(block = block)

    @JvmStatic
    fun io(block: suspend CoroutineScope.() -> Unit) =
        ioScope.launch(block = block)

    @JvmStatic
    fun CoroutineScope.ui(block: suspend CoroutineScope.() -> Unit) =
        launch(Main, block = block)

    @JvmStatic
    fun CoroutineScope.io(block: suspend CoroutineScope.() -> Unit) =
        launch(IO, block  = block)
    
    @JvmStatic
    suspend fun <T> ioContext(block: suspend CoroutineScope.() -> T) =
        withContext(io, block)

}