package sakuraba.saki.player.music.util

object UnitUtil {

    private const val BYTES = "Bytes"
    val Long.asMiB get() = "${String.format("%.2f", this / 1048576F)} M$BYTES"

    const val UNIT_BITS = "bits"
    private const val UNIT_KILO = "K"
    const val UNIT_Hertz = "Hz"
    const val UNIT_SEC = "s"
    const val UNIT_SAMPLE = "sample"
    const val PER = "/"
    val String.getAsKilo get() = toInt().getAsKilo
    val Int.getAsKilo get() = "${(this / 1000)} $UNIT_KILO"
    val Long.addZero get() = if (this > 9) this.toString() else "0${this}"
    val Long.toTimeFormat get() = (this / 60000).toString() + ':' + (this / 1000 % 60).addZero

}