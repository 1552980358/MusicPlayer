package projekt.cloud.piece.music.player.ui.play.util

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.TimeUtil.minToMills

/**
 * [SleepTimer]
 * Variables:
 * [onTimerStop]
 * [job]
 * [millis]
 *
 * Methods:
 * [isStarted]
 * [start]
 * [stop]
 * [doJob]
 *
 **/
class SleepTimer(private val onTimerStop: () -> Unit) {

    private var job: Job? = null
    var millis: String? = null
        private set
    
    val isStarted get() = job != null
    
    fun start(millis: String) {
        job?.cancel()
        this.millis = millis
        job = doJob(millis)
    }
    
    fun stop() {
        millis = null
        job?.cancel()
    }
    
    private fun doJob(millis: String) = io {
        delay(millis.toLong().minToMills)
        onTimerStop.invoke()
    }

}