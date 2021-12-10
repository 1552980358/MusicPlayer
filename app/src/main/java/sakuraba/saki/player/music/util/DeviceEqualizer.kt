package sakuraba.saki.player.music.util

import android.media.audiofx.Equalizer
import java.io.Serializable

data class DeviceEqualizer(val bands: Short, val minBandLevel: Int, val maxBandLevel: Int): Serializable {

    var frequencies = arrayListOf<Int>()

    constructor(bands: Short, bandsLevel: ShortArray, equalizer: Equalizer):
        this(bands, bandsLevel.first().toInt() / 100, bandsLevel.last().toInt() / 100) {
        (0 until bands).forEach {
            frequencies.add(equalizer.getCenterFreq(it.toShort()) / 1000)
        }
        frequencies.sort()
    }

    constructor(equalizer: Equalizer): this(equalizer.numberOfBands, equalizer.bandLevelRange, equalizer)

}