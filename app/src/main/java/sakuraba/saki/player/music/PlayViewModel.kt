package sakuraba.saki.player.music

import android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlayViewModel: ViewModel() {
    
    private val _progress = MutableLiveData<Long>()
    fun updateProgress(newProgress: Long) {
        _progress.value = newProgress
    }
    val progress get() = _progress as LiveData<Long>
    val progressValue get() = progress.value!!
    
    private val _duration = MutableLiveData<Long>()
    fun updateDuration(newDuration: Long) {
        _duration.value = newDuration
    }
    val duration get() = _duration as LiveData<Long>
    
    private val _state = MutableLiveData<Int>()
    fun updateState(newState: Int) {
        if (newState != STATE_BUFFERING && _state.value != newState) {
            _state.value = newState
        }
    }
    val state get() = _state as LiveData<Int>
    val stateValue get() = _state.value
    
}