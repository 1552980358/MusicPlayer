package projekt.cloud.piece.music.player

import android.graphics.Bitmap
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {

    private var _title = MutableLiveData<String>()
    fun setTitle(title: String) = _title.postValue(title)
    val titleLiveData: LiveData<String>
        get() = _title
    
    private var _artist = MutableLiveData<String>()
    fun setArtist(artist: String) = _artist.postValue(artist)
    val artistLiveData: LiveData<String>
        get() = _artist
    
    private var _album = MutableLiveData<String>()
    fun setAlbum(album: String) = _album.postValue(album)
    val albumLiveData: LiveData<String>
        get() = _album
    
    private val _artBitmap = MutableLiveData<Bitmap>()
    fun setArtBitmap(bitmap: Bitmap) = _artBitmap.postValue(bitmap)
    val artBitmapLiveData: LiveData<Bitmap?>
        get() = _artBitmap
    
    private val _duration = MutableLiveData<Long>()
    fun setDuration(duration: Long) = _duration.postValue(duration)
    val durationLiveData: LiveData<Long>
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
    
}