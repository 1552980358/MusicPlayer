package projekt.cloud.piece.music.player.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private typealias CoroutineBlock = suspend CoroutineScope.() -> Unit

object CoroutineUtil {

    val main = Dispatchers.Main
    val io = Dispatchers.IO
    val default = Dispatchers.Default

    fun main(block: CoroutineBlock) = CoroutineScope(main).launch(block = block)
    fun CoroutineScope.main(block: CoroutineBlock) = launch(main, block = block)

    fun io(block: CoroutineBlock) = CoroutineScope(io).launch(block = block)
    fun CoroutineScope.io(block: CoroutineBlock) = launch(io, block = block)

    fun default(block: CoroutineBlock) = CoroutineScope(default).launch(block = block)
    fun CoroutineScope.default(block: CoroutineBlock) = launch(default, block = block)

}