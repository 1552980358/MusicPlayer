package sakuraba.saki.player.music.util

import android.graphics.Color

object ColorUtil {

    @JvmStatic
    val Int.red get() = Color.red(this)

    @JvmStatic
    val Int.green get() = Color.green(this)

    @JvmStatic
    val Int.blue get() = Color.blue(this)

    @JvmStatic
    private val Int.lightness get() = 0.299 * red + 0.587 * green + 0.114 * blue

    @JvmStatic
    val Int.isLight get() = lightness > 127

}