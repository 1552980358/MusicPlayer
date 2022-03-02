package sakuraba.saki.player.music.util

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import sakuraba.saki.player.music.database.AudioDatabaseHelper
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

    val playlistList = arrayListOf<Playlist>()
    val playlistMap = mutableMapOf<String, Bitmap?>()

    val audioInfoList = arrayListOf<AudioInfo>()

    var refreshCompleted = false

    private var loadingStageChangeListener: LoadingStageChangeListener? = null
    private var completeLoadingListener: CompleteLoadingListener? = null
    private var requestRefreshListener: RequestRefreshListener? = null
    private var contentChangeRefreshListener: ContentChangeRefreshListener? = null

    lateinit var audioDatabaseHelper: AudioDatabaseHelper

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

    private lateinit var artUpdate: (AudioInfo) -> Unit
    fun setOnArtUpdate(block: (AudioInfo) -> Unit) {
        artUpdate = block
    }
    fun onArtUpdate(audioInfo: AudioInfo) = artUpdate(audioInfo)

    fun clearLists() {
        audioInfoFullList.clear()
        audioInfoList.clear()
        bitmapMap.clear()
        albumList.clear()
        audioBitmapMap.clear()
    }

    private lateinit var mediaBrowserCompat: MediaBrowserCompat
    fun setMediaBrowserCompat(mediaBrowserCompat: MediaBrowserCompat) {
        this.mediaBrowserCompat = mediaBrowserCompat
    }

    fun sendCustomAction(action: String, bundle: Bundle?,  block: (action: String?, bundle: Bundle?, resultBundle: Bundle?) -> Unit) {
        mediaBrowserCompat.sendCustomAction(action, bundle, object : MediaBrowserCompat.CustomActionCallback() {
            override fun onResult(action: String?, extras: Bundle?, resultData: Bundle?) =
                block(action, extras, resultData)
        })
    }

    var hasAudioInfoListUpdated = false

    init {
        fragmentListItemClickListener = OnFragmentListItemClickListener(block)
    }

}