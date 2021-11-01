package sakuraba.saki.player.music.ui.home.util

import android.graphics.Bitmap
import sakuraba.saki.player.music.service.util.AudioInfo
import java.io.Serializable
import sakuraba.saki.player.music.util.MediaAlbum

class MainFragmentData: Serializable {

    private fun interface LoadingListener {
        fun onCompleteLoad()
    }

    val audioInfoFullList = arrayListOf<AudioInfo>()
    val bitmapMap = mutableMapOf<Long, Bitmap?>()
    val albumList = arrayListOf<MediaAlbum>()

    var audioInfoList: ArrayList<AudioInfo> = audioInfoFullList

    var hasData = false

    private var listener: LoadingListener? = null

    fun setLoadingListener(block: () -> Unit) {
        listener = LoadingListener(block)
    }

    fun onCompleteLoad() = listener?.onCompleteLoad()

}