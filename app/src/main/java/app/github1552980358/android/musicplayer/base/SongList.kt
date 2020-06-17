package app.github1552980358.android.musicplayer.base

import lib.github1552980358.ktExtension.android.java.copyAndShuffle
import java.io.Serializable

/**
 * [SongList]
 * @author  : 1552980328
 * @since   : 0.1
 * @date    : 2020/5/20
 * @time    : 11:40
 **/

class SongList: Serializable {
    
    /**
     * [listName]
     * @author 1552980358
     * @since 0.1
     **/
    var listName = ""
    
    /**
     * [audioList]
     * @author 1552980358
     * @since 0.1
     **/
    val audioList = arrayListOf<AudioData>()
    
    /**
     * [audioListCustom]
     * @author 1552980358
     * @since 0.1
     **/
    val audioListCustom = arrayListOf<AudioData>()
    
    /**
     * [audioListRandom]
     * @author 1552980358
     * @since 0.1
     **/
    var audioListRandom = arrayListOf<AudioData>()
    
    /**
     * [add]
     * @param audioData [AudioData]
     * @return [SongList]
     * @author 1552980358
     * @since 0.1
     **/
    fun add(audioData: AudioData) = this.apply {
        audioListCustom.add(audioData)
        audioList.add(audioData)
        audioList.sortBy { it.titlePinYin }
        audioListRandom = audioList.copyAndShuffle()
    }
    
    /**
     * [add]
     * @param list [List]<[AudioData]>
     * @author 1552980358
     * @since 0.1
     **/
    fun add(list: List<AudioData>) = this.apply {
        list.forEach { audioData ->
            audioListCustom.add(audioData)
            audioList.add(audioData)
        }
        audioList.sortBy { it.titlePinYin }
        audioListRandom = audioList.copyAndShuffle()
    }
    
}