package projekt.cloud.piece.music.player.service.play

import android.support.v4.media.MediaMetadataCompat
import projekt.cloud.piece.music.player.database.audio.item.AudioItem

class AudioList {
    
    private val _audioList = ArrayList<AudioItem>()
    fun updateList(currentIndex: Int, audioList: List<AudioItem>, isShuffle: Boolean = false): AudioItem {
        _audioList.clear()
        _audioList.addAll(audioList)
       
        updateIndex()
        
        when {
            isShuffle -> {
                _audioList.shuffle()
                current = _audioList.indexOfFirst { it.index == currentIndex }
                updateIndex()
            }
            else -> current = currentIndex
        }
        return audioItem
    }
    
    private var current = 0
    val currentIndex get() = current
    fun setCurrentIndex(index: Int): AudioItem {
        current = index
        return audioItem
    }
    
    val audioItem get() = _audioList[current]
    
    operator fun get(index: Int) = _audioList[index]
    
    private fun updateIndex() {
        _audioList.forEachIndexed { index, audioItem -> audioItem.index = index }
    }
    
}