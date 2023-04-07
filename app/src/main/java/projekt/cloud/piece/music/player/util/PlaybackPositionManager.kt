package projekt.cloud.piece.music.player.util

import java.io.Closeable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import projekt.cloud.piece.music.player.util.CoroutineUtil.default
import projekt.cloud.piece.music.player.util.CoroutineUtil.main

class PlaybackPositionManager @JvmOverloads constructor(
    coroutineScope: CoroutineScope,
    position: Long = DEFAULT_POSITION,
    doOnUpdate: (Long) -> Unit
): Closeable {

    private companion object {
        const val DEFAULT_POSITION = 0L
        const val DELAY_SEPARATION = 200L
        const val ROUNDING_SEPARATION = DELAY_SEPARATION
        const val DURATION_UNKNOWN = -1L
    }

    var position = position
        @Synchronized
        private set

    var duration = DURATION_UNKNOWN
        @Synchronized
        set

    @Volatile
    private var isContinue = true

    private var job: Job? = null

    init {
        startPlaybackLoop(coroutineScope, position, doOnUpdate)
    }

    private fun startPlaybackLoop(coroutineScope: CoroutineScope, initPosition: Long, doOnUpdate: (Long) -> Unit) {
        job = coroutineScope.default {
            main { doOnUpdate.invoke(initPosition) }
            roundUpPosition(initPosition % ROUNDING_SEPARATION)

            while (isContinue && canContinue) {
                main { doOnUpdate.invoke(position) }
                delay(DELAY_SEPARATION)
                position += DELAY_SEPARATION
            }
        }
    }

    private suspend fun roundUpPosition(separation: Long) {
        if (separation != 0L) {
            position += separation
            delay(ROUNDING_SEPARATION - separation)
        }
    }

    private val canContinue: Boolean
        get() = duration == DURATION_UNKNOWN || position <= duration

    override fun close() {
        isContinue = false
        job?.cancel()
        job = null
    }

}