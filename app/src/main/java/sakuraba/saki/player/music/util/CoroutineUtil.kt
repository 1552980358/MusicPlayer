package sakuraba.saki.player.music.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object CoroutineUtil {
    const val ms_1000_long = 1000L
    const val ms_1000_int = 1000
    suspend fun delay1second() = delay(ms_1000_long)

    const val ms_100_long = 100L
    const val ms_100_int = 100
    suspend fun delay100ms() = delay(ms_100_long)

    val ioDispatcher get() = CoroutineScope(IO)

    val mainDispatcher get() = CoroutineScope(Main)

    fun io(block: suspend CoroutineScope.() -> Unit) =
        ioDispatcher.launch(block = block)

    fun ui(block: suspend CoroutineScope.() -> Unit) =
        mainDispatcher.launch(block = block)

    fun CoroutineScope.io(block: suspend CoroutineScope.() -> Unit) =
        launch(IO, block = block)

    fun CoroutineScope.ui(block: suspend CoroutineScope.() -> Unit) =
        launch(Main, block = block)

}