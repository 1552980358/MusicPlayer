package app.github1552980358.android.musicplayer.base

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
     * [add]
     * @param audioData [AudioData]
     * @return [SongList]
     * @author 1552980358
     * @since 0.1
     **/
    fun add(audioData: AudioData): SongList {
        audioList.add(audioData)
        return this
    }
    
    /**
     * [add]
     * @param list [List]<[AudioData]>
     * @author 1552980358
     * @since 0.1
     **/
    fun add(list: List<AudioData>) = this.apply { list.forEach { audioData -> audioList.add(audioData) } }
    
    /**
     * [insert]
     * @param index [Int]
     * @param audioData [AudioData]
     * @return [SongList]
     * @author 1552980358
     * @since 0.1
     **/
    fun insert(index: Int, audioData: AudioData) = this.apply { audioList.add(index, audioData) }
    
    /**
     * [remove]
     * @param index [Int]
     * @return [SongList]
     * @author 1552980358
     * @since 0.1
     **/
    fun remove(index: Int) = this.apply { audioList.removeAt(index) }
    
}