package projekt.cloud.piece.cloudy.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

typealias SuspendScopedBlock = suspend CoroutineScope.() -> Unit
typealias SuspendScopedReturnBlock<R> = suspend CoroutineScope.() -> R

object CoroutineUtil {

    private inline val main: CoroutineDispatcher
        get() = Dispatchers.Main
    private inline val default: CoroutineDispatcher
        get() = Dispatchers.Default
    private inline val io: CoroutineDispatcher
        get() = Dispatchers.IO

    private fun CoroutineScope.launchOnDispatcher(
        dispatcher: CoroutineDispatcher, block: SuspendScopedBlock
    ): Job = launch(context = dispatcher, block = block)

    private suspend inline fun <R> blocking(
        context: CoroutineContext,
        noinline block: SuspendScopedReturnBlock<R>
    ): R = withContext(context, block)

    fun CoroutineScope.main(block: SuspendScopedBlock): Job {
        return launchOnDispatcher(main, block)
    }
    fun CoroutineScope.default(block: SuspendScopedBlock): Job {
        return launchOnDispatcher(default, block)
    }
    fun CoroutineScope.io(block: SuspendScopedBlock): Job {
        return launchOnDispatcher(io, block)
    }

    suspend fun <R> mainBlock(block: SuspendScopedReturnBlock<R>): R {
        return blocking(main, block)
    }
    suspend fun <R> defaultBlocking(block: SuspendScopedReturnBlock<R>): R {
        return blocking(default, block)
    }
    suspend fun <R> ioBlocking(block: SuspendScopedReturnBlock<R>): R {
        return blocking(io, block)
    }

}