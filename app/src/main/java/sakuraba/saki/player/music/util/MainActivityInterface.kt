package sakuraba.saki.player.music.util

import android.graphics.Bitmap
import sakuraba.saki.player.music.service.util.AudioInfo
import java.io.Serializable

class MainActivityInterface(block: (Int, AudioInfo?, ArrayList<AudioInfo>?) -> Unit) : Serializable {
    
    private fun interface OnFragmentListItemClickListener: Serializable {
        fun onItemClicked(pos: Int, audioInfo: AudioInfo?, arrayList: ArrayList<AudioInfo>?)
    }

    private fun interface LoadingStageChangeListener {
        fun onLoading()
    }

    private fun interface CompleteLoadingListener {
        fun onComplete()
    }

    private fun interface RequestRefreshListener {
        fun onRequestRefresh()
    }

    private fun interface ContentChangeRefreshListener {
        fun onContentChange()
    }
    
    private var fragmentListItemClickListener: OnFragmentListItemClickListener? = null

    fun onFragmentListItemClick(pos: Int, audioInfo: AudioInfo?, audioInfoList: ArrayList<AudioInfo>?) =
        fragmentListItemClickListener?.onItemClicked(pos, audioInfo, audioInfoList)

    val audioInfoFullList = arrayListOf<AudioInfo>()
    val bitmapMap = mutableMapOf<Long, Bitmap?>()
    val albumList = arrayListOf<MediaAlbum>()
    val byteArrayMap = mutableMapOf<Long, ByteArray>()
    val audioBitmapMap = mutableMapOf<String, Bitmap?>()

    var audioInfoList: ArrayList<AudioInfo> = audioInfoFullList

    var refreshCompleted = false

    private var loadingStageChangeListener: LoadingStageChangeListener? = null
    private var completeLoadingListener: CompleteLoadingListener? = null
    private var requestRefreshListener: RequestRefreshListener? = null
    private var contentChangeRefreshListener: ContentChangeRefreshListener? = null

    fun setLoadingStageChangeListener(block: () -> Unit) {
        loadingStageChangeListener = LoadingStageChangeListener(block)
    }

    fun setCompleteLoadingListener(block: () -> Unit) {
        completeLoadingListener = CompleteLoadingListener(block)
    }

    fun setRequestRefreshListener(block: () -> Unit) {
        requestRefreshListener = RequestRefreshListener(block)
    }

    fun setContentChangeRefreshListener(block: () -> Unit) {
        contentChangeRefreshListener = ContentChangeRefreshListener(block)
    }

    fun removeContentChangeRefreshListener() {
        completeLoadingListener = null
    }

    fun onLoadStageChange() = loadingStageChangeListener?.onLoading()

    fun onCompleteLoading() = completeLoadingListener?.onComplete()

    fun onRequestRefresh() = requestRefreshListener?.onRequestRefresh()

    fun onContentChange() = contentChangeRefreshListener?.onContentChange()

    fun clearLists() {
        audioInfoFullList.clear()
        audioInfoList.clear()
        bitmapMap.clear()
        albumList.clear()
    }

    var hasAudioInfoListUpdated = false

    init {
        fragmentListItemClickListener = OnFragmentListItemClickListener(block)
    }

}