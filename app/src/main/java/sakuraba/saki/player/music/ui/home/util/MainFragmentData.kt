package sakuraba.saki.player.music.ui.home.util

import android.graphics.Bitmap
import sakuraba.saki.player.music.service.util.AudioInfo
import java.io.Serializable
import sakuraba.saki.player.music.util.MediaAlbum

class MainFragmentData: Serializable {

    private fun interface LoadingStageChangeListener {
        fun onLoading()
    }

    private fun interface CompleteLoadingListener {
        fun onComplete()
    }

    val audioInfoFullList = arrayListOf<AudioInfo>()
    val bitmapMap = mutableMapOf<Long, Bitmap?>()
    val albumList = arrayListOf<MediaAlbum>()

    var audioInfoList: ArrayList<AudioInfo> = audioInfoFullList

    var hasData = false

    private var loadingStageChangeListener: LoadingStageChangeListener? = null
    private var completeLoadingListener: CompleteLoadingListener? = null

    fun setLoadingStageChangeListener(block: () -> Unit) {
        loadingStageChangeListener = LoadingStageChangeListener(block)
    }

    fun setCompleteLoadingListener(block: () -> Unit) {
        completeLoadingListener = CompleteLoadingListener(block)
    }

    fun onLoadStageChange() = loadingStageChangeListener?.onLoading()

    fun onCompleteLoading() = completeLoadingListener?.onComplete()

}