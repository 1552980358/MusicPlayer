package projekt.cloud.piece.music.player.service.play

import projekt.cloud.piece.music.player.item.AudioMetadata

class PlayingQueue {
    
    private var _audioMetadataList: ArrayList<AudioMetadata>? = null
    private val audioMetadataList get() = _audioMetadataList!!
    
    private var _currentIndex = 0
    
    fun setAudioMetadataList(audioId: String, audioMetadataList: ArrayList<AudioMetadata>?, shuffle: Boolean): AudioMetadata {
        when (val list = audioMetadataList) {
            null -> _currentIndex = this.audioMetadataList.indexOfFirst { it.id == audioId }
            else -> {
                if (shuffle) {
                    list.shuffle()
                }
                _audioMetadataList = list
                _currentIndex = list.indexOfFirst { it.audio.id == audioId }
            }
        }
        return current
    }
    
    val current: AudioMetadata
        get() = audioMetadataList[_currentIndex]
    
    val next: AudioMetadata get() {
        if (audioMetadataList.size == 1) {
            return audioMetadataList[_currentIndex]
        }
        when (_currentIndex) {
            audioMetadataList.lastIndex -> _currentIndex = 0
            else -> _currentIndex++
        }
        return audioMetadataList[_currentIndex]
    }
    
    val prev: AudioMetadata get() {
        if (audioMetadataList.size == 1) {
            return audioMetadataList[_currentIndex]
        }
        when (_currentIndex) {
            0 -> _currentIndex = audioMetadataList.lastIndex
            else -> _currentIndex--
        }
        return audioMetadataList[_currentIndex]
    }
    
    val isFirst: Boolean
        get() = _currentIndex == 0
    
    val isLast: Boolean
        get() = _currentIndex == audioMetadataList.lastIndex
    
    fun setShuffle(isShuffle: Boolean): ArrayList<AudioMetadata> {
        val current = current
        when {
            isShuffle -> audioMetadataList.shuffle()
            else -> audioMetadataList.sortBy { it.title }
        }
        _currentIndex = audioMetadataList.indexOfFirst { it.id == current.id }
        return audioMetadataList
    }
    
}