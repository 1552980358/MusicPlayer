package sakuraba.saki.player.music.util

import android.graphics.Bitmap
import sakuraba.saki.player.music.service.util.AudioInfo
import java.io.Serializable

class ActivityFragmentInterface(): Serializable {
    
    private fun interface OnFragmentListItemClickListener: Serializable {
        fun onItemClicked(pos: Int, audioInfo: AudioInfo?, arrayList: ArrayList<AudioInfo>?)
    }
    
    private fun interface OnHomeFragmentPausedListener: Serializable {
        fun onPaused(arrayList: ArrayList<AudioInfo>, mutableMap: MutableMap<Long, Bitmap?>)
    }
    
    private var fragmentListItemClickListener: OnFragmentListItemClickListener? = null
    private var homeFragmentPausedListener: OnHomeFragmentPausedListener? = null
    
    constructor(block: (Int, AudioInfo?, ArrayList<AudioInfo>?) -> Unit): this() {
        fragmentListItemClickListener = OnFragmentListItemClickListener(block)
    }
    
    fun onFragmentListItemClick(pos: Int, audioInfo: AudioInfo?, audioInfoList: ArrayList<AudioInfo>?) =
        fragmentListItemClickListener?.onItemClicked(pos, audioInfo, audioInfoList)
    
    fun setOnHomeFragmentPausedListener(block: (arrayList: ArrayList<AudioInfo>, mutableMap: MutableMap<Long, Bitmap?>) -> Unit) {
        homeFragmentPausedListener = OnHomeFragmentPausedListener(block)
    }
    
    fun removeOnHomeFragmentPausedListener() {
        homeFragmentPausedListener = null
    }
    
    fun onHomeFragmentPaused(arrayList: ArrayList<AudioInfo>, mutableMap: MutableMap<Long, Bitmap?>) =
        homeFragmentPausedListener?.onPaused(arrayList, mutableMap)
    
}