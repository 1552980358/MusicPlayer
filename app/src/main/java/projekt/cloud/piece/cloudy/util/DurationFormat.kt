package projekt.cloud.piece.cloudy.util

enum class DurationFormat {
    SHORT,  /** 0:00 **/
    LONG;   /** 00:00 **/

    companion object DurationFormatUtil {

        fun Long.format(format: DurationFormat): String {
            return format.format(this)
        }

        private const val SECOND = 1000
        private const val MINUTE = 60 * SECOND

        private const val ZERO = '0'
        private const val COLON = ':'

        private const val SINGLE_DIGIT_MAX = 9

    }

    private fun format(mills: Long): String {
        return formatter.invoke(
            mills.minute, mills.second.withLeadingZero
        )
    }

    private val formatter: (Long, String) -> String
        get() = when (this) {
            SHORT -> ::short
            LONG -> ::long
        }

    private fun short(minute: Long, second: String): String {
        return minute.toString() + COLON + second
    }

    private fun long(minute: Long, second: String): String {
        return minute.withLeadingZero + COLON + second
    }

    private val Long.withLeadingZero: String
        get() = when {
            this > SINGLE_DIGIT_MAX -> this.toString()
            else -> "$ZERO$this"
        }

    private val Long.minute: Long
        get() = this / MINUTE

    private val Long.second: Long
        get() = this % MINUTE / SECOND

}