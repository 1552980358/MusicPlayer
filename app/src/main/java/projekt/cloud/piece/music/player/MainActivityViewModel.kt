package projekt.cloud.piece.music.player

import android.graphics.Bitmap
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import projekt.cloud.piece.music.player.item.AudioMetadata

class MainActivityViewModel: ViewModel() {

    private var _title = MutableLiveData<String>()
    fun setTitle(title: String) = _title.postValue(title)
    val title: LiveData<String>
        get() = _title
    
    private var _artist = MutableLiveData<String>()
    fun setArtist(artist: String) = _artist.postValue(artist)
    val artist: LiveData<String>
        get() = _artist
    
    private var _album = MutableLiveData<String>()
    fun setAlbum(album: String) = _album.postValue(album)
    val album: LiveData<String>
        get() = _album
    
    private val _artBitmap = MutableLiveData<Bitmap>()
    fun setArtBitmap(bitmap: Bitmap) = _artBitmap.postValue(bitmap)
    val artBitmap: LiveData<Bitmap?>
        get() = _artBitmap
    
    private val _duration = MutableLiveData<Long>()
    fun setDuration(duration: Long) = _duration.postValue(duration)
    val duration: LiveData<Long>
        get() = _duration
    
    private val _playbackState = MutableLiveData<Int>()
    fun setPlaybackState(playbackState: Int) {
        _playbackState.value = playbackState
    }
    val playbackState: LiveData<Int>
        get() = _playbackState
    
    private val _repeatMode = MutableLiveData<Int>()
    fun setRepeatMode(@PlaybackStateCompat.RepeatMode repeatMode: Int) {
        _repeatMode.value = repeatMode
    }
    val repeatMode: LiveData<Int>
        get() = _repeatMode
    
    private val _shuffleMode = MutableLiveData<Int>()
    fun setShuffleMode(@PlaybackStateCompat.ShuffleMode shuffleMode: Int) {
        _shuffleMode.value = shuffleMode
    }
    val shuffleMode: LiveData<Int>
        get() = _shuffleMode
    
    private val _position = MutableLiveData<Long>(0)
    private var positionCountUpJob: Job? = null
    fun setPosition(position: Long) {
        _position.value = position
        if (playbackState.value == STATE_PLAYING) {
            positionCountUpJob?.cancel()
            positionCountUpJob = startPositionCountUp(position)
        }
    }
    val position: LiveData<Long>
        get() = _position
    
    private fun startPositionCountUp(position: Long) = viewModelScope.launch {
        val remain = position % 1000L
        if (remain  != 0L) {
            delay(remain)
        }
        var current = position + remain
        while (playbackState.value == STATE_PLAYING) {
            _position.postValue(current)
            delay(1000L)
            current += 1000L
        }
    }
    
    private val _playingPosition = MutableLiveData<Int>()
    fun setPlayingPosition(position: Int) {
        _playingPosition.value = position
    }
    val playingPosition: LiveData<Int>
        get() = _playingPosition
    
    private val _playingQueue = MutableLiveData<List<AudioMetadata>>()
    fun setPlayingQueue(audioMetadataList: List<AudioMetadata>) {
        _playingQueue.value = audioMetadataList
    }
    val playingQueue: LiveData<List<AudioMetadata>>
        get() = _playingQueue
    
}