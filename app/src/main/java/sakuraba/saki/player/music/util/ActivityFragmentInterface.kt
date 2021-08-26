package sakuraba.saki.player.music.util

import sakuraba.saki.player.music.service.util.AudioInfo
import java.io.Serializable

class ActivityFragmentInterface(): Serializable {
    
    private fun interface OnChangedListener: Serializable {
        fun onChanged(pos: Int, audioInfo: AudioInfo?, arrayList: ArrayList<AudioInfo>?)
    }
    
    private var activityListener: OnChangedListener? = null
    private var fragmentListener: OnChangedListener? = null
    
    constructor(block: (Int, AudioInfo?, ArrayList<AudioInfo>?) -> Unit): this() {
        setOnActivityChangedListener(block)
    }
    
    fun setOnActivityChangedListener(block: (Int, AudioInfo?, ArrayList<AudioInfo>?) -> Unit) {
        activityListener = OnChangedListener(block)
    }
    
    fun setOnFragmentChangeListener(block: (Int, AudioInfo?, ArrayList<AudioInfo>?) -> Unit) {
        fragmentListener = OnChangedListener(block)
    }
    
    fun onActivityChanged(pos: Int, audioInfo: AudioInfo?, audioInfoList: ArrayList<AudioInfo>?) =
        fragmentListener?.onChanged(pos, audioInfo, audioInfoList)
    
    fun onFragmentChanged(pos: Int, audioInfo: AudioInfo?, audioInfoList: ArrayList<AudioInfo>?) =
        activityListener?.onChanged(pos, audioInfo, audioInfoList)
    
}