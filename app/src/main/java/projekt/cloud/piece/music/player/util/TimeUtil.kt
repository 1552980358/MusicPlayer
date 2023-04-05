package projekt.cloud.piece.music.player.util

object TimeUtil {

    private const val SECOND = 1000
    private const val MINUTE = 60

    private val Long.millsToMinute: Long
        get() = this / SECOND / MINUTE

    private val Long.millsToSecond: Long
        get() = this / SECOND % MINUTE

    private val Long.leadingZero: String
        get() = when {
            this < 10 -> "0$this"
            else -> "$this"
        }

    val Long.durationStr: String
        get() = "${millsToMinute}:${millsToSecond.leadingZero}"

    val Long.timeStr: String
        get() = "${millsToMinute.leadingZero}:${millsToSecond.leadingZero}"

}