package projekt.cloud.piece.music.player.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

/**
 * Object [CoroutineUtil]
 *
 * Getters:
 *   [ui]
 *   [io]
 *
 * Methods:
 *   [ui]
 *   [io]
 * 
 **/
object CoroutineUtil {

    val ui = CoroutineScope(Main)
    val io = CoroutineScope(IO)

    @JvmStatic
    fun ui(block: suspend CoroutineScope.() -> Unit) =
        ui.launch(block = block)

    @JvmStatic
    fun io(block: suspend CoroutineScope.() -> Unit) =
        io.launch(block = block)

    @JvmStatic
    fun CoroutineScope.ui(block: suspend CoroutineScope.() -> Unit) =
        launch(Main, block = block)

    fun CoroutineScope.io(block: suspend CoroutineScope.() -> Unit) =
        launch(IO, block  = block)

}