package sakuraba.saki.player.music.util

import kotlinx.coroutines.delay

object Coroutine {
    const val ms_1000_long = 1000L
    const val ms_1000_int = 1000
    suspend fun delay1second() = delay(ms_1000_long)
}