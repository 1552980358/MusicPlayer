package app.github1552980358.android.musicplayer.base

import java.io.Serializable

/**
 * [SongList]
 * @author  : 1552980328
 * @since   : 0.1
 * @date    : 2020/5/20
 * @time    : 11:40
 **/

class SongList: Serializable, ArrayListUtil {

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
        audioList.add(audioData)
        audioList.sortBy { it.titlePinYin }
        audioListRandom = copyAndShuffle(audioList)
    }
    
    /**
     * [add]
     * @param list [List]<[AudioData]>
     * @author 1552980358
     * @since 0.1
     **/
    fun add(list: List<AudioData>) = this.apply {
        list.forEach { audioData -> audioList.add(audioData) }
        audioList.sortBy { it.titlePinYin }
        audioListRandom = copyAndShuffle(audioList)
    }
    
    /**
     * [insert]
     * @param index [Int]
     * @param audioData [AudioData]
     * @return [SongList]
     * @author 1552980358
     * @since 0.1
     **/
    fun insert(index: Int, audioData: AudioData) = this.apply {
        audioList.add(index, audioData)
        audioList.sortBy { it.titlePinYin }
        audioListRandom = copyAndShuffle(audioList)
    }
    
    /**
     * [remove]
     * @param index [Int]
     * @return [SongList]
     * @author 1552980358
     * @since 0.1
     **/
    fun remove(index: Int) = this.apply {
        audioList.removeAt(index)
        audioListRandom = copyAndShuffle(audioList)
    }
    
}