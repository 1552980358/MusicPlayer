package projekt.cloud.piece.music.player.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private typealias ScopedSuspendUnitBlock = suspend CoroutineScope.() -> Unit

private typealias ScopedSuspendResultBlock<R> = suspend CoroutineScope.() -> R

object CoroutineUtil {

    inline val io: CoroutineDispatcher
        get() = Dispatchers.IO
    inline val main: CoroutineDispatcher
        get() = Dispatchers.Main
    inline val default: CoroutineDispatcher
        get() = Dispatchers.Default

    /** CoroutineScope related **/
    fun CoroutineScope.main(scopedSuspendUnitBlock: ScopedSuspendUnitBlock): Job {
        return launch(main, block = scopedSuspendUnitBlock)
    }
    fun CoroutineScope.default(scopedSuspendUnitBlock: ScopedSuspendUnitBlock): Job {
        return launch(default, block = scopedSuspendUnitBlock)
    }
    fun CoroutineScope.io(scopedSuspendUnitBlock: ScopedSuspendUnitBlock): Job {
        return launch(io, block = scopedSuspendUnitBlock)
    }

    /** LifecycleOwner related **/
    fun LifecycleOwner.main(scopedSuspendUnitBlock: ScopedSuspendUnitBlock): Job {
        return lifecycleScope.main(scopedSuspendUnitBlock)
    }
    fun LifecycleOwner.default(scopedSuspendUnitBlock: ScopedSuspendUnitBlock): Job {
        return lifecycleScope.default(scopedSuspendUnitBlock)
    }
    fun LifecycleOwner.io(block: ScopedSuspendUnitBlock): Job {
        return lifecycleScope.io(block)
    }

    suspend fun <R> blocking(
        coroutineDispatcher: CoroutineDispatcher,
        block: ScopedSuspendResultBlock<R>
    ): R = withContext(coroutineDispatcher, block)
    suspend inline fun <R> mainBlocking(
        noinline block: ScopedSuspendResultBlock<R>
    ) = blocking(main, block)
    suspend inline fun <R> defaultBlocking(
        noinline block: ScopedSuspendResultBlock<R>
    ) = blocking(default, block)
    suspend inline fun <R> ioBlocking(
        noinline block: ScopedSuspendResultBlock<R>
    ) = blocking(io, block)

}