package sakuraba.saki.player.music.util

object UnitUtil {

    private const val BYTES = "Bytes"
    val Long.asMiB get() = "${String.format("%.2f", this / 1048576F)} M$BYTES"

    private const val UNIT_BITS = "bits"
    private const val UNIT_KILO = "K"
    private const val UNIT_Hertz = "Hz"
    private const val UNIT_SEC = "s"
    private const val UNIT_SAMPLE = "sample"
    private const val PER = "/"
    private val String.getAsKilo get() = toInt().getAsKilo
    private val Int.getAsKilo get() = "${(this / 1000)} $UNIT_KILO"
    private val Long.addZero get() = if (this > 9) this.toString() else "0${this}"
    private val Long.toTimeFormat get() = (this / 60000).toString() + ':' + (this / 1000 % 60).addZero

}