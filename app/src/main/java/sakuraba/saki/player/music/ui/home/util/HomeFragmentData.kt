package sakuraba.saki.player.music.ui.home.util

import android.graphics.Bitmap
import sakuraba.saki.player.music.service.util.AudioInfo
import java.io.Serializable

class HomeFragmentData(var audioInfoList: ArrayList<AudioInfo>? = null, var bitmapMap: MutableMap<Long, Bitmap?>? = null): Serializable {
    var hasData = false
}