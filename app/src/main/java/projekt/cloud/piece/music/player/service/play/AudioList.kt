package projekt.cloud.piece.music.player.service.play

import projekt.cloud.piece.music.player.database.audio.item.AudioItem
import java.io.Serializable

/**
 * Class [AudioList]
 *  implements [Serializable]
 *
 * Final Variables:
 *  [_audioList]
 *   @type [ArrayList]<[AudioItem]>
 *
 * Variables:
 *  [current]
 *
 * Getters:
 *  [prev]
 *  [next]
 *  [head]
 *  [last]
 *  [isHead]
 *  [isLast]
 *  [audioItem]
 *
 * Methods:
 *  [updateList]
 *  [setIndex]
 *  [get]
 *  [updateIndex]
 *
 **/
class AudioList: Serializable {
    
    private val _audioList = ArrayList<AudioItem>()
    fun updateList(audioItem: AudioItem, audioList: List<AudioItem>, isShuffle: Boolean = false): AudioItem {
        _audioList.clear()
        _audioList.addAll(audioList)
        
        if (isShuffle) {
            _audioList.shuffle()
        }
        updateIndex()
        return setIndex(_audioList.indexOfFirst { it.id == audioItem.id })
    }
    
    private var current = 0
    fun setIndex(index: Int): AudioItem {
        current = index
        return audioItem
    }

    val prev get() = setIndex(--current)
    val next get() = setIndex(++current)

    val head get() = setIndex(0)
    val last get() = setIndex(_audioList.lastIndex)

    val isLast get() = current == _audioList.lastIndex
    val isHead get() = current == 0
    
    val audioItem get() = _audioList[current]
    
    operator fun get(index: Int) = _audioList[index]
    
    private fun updateIndex() {
        _audioList.forEachIndexed { index, audioItem -> audioItem.index = index }
    }
    
}