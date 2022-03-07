package sakuraba.saki.player.music

import android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlayViewModel: ViewModel() {

    private val _state = MutableLiveData<Int>()
    fun updateState(newState: Int) {
        if (newState != STATE_BUFFERING && _state.value != newState) {
            _state.value = newState
        }
    }
    val state get() = _state as LiveData<Int>
    val stateValue get() = _state.value
    
    private val _isLightBackground = MutableLiveData<Boolean>()
    fun setIsLightBackground(newBoolean: Boolean) {
        if (_isLightBackground.value != newBoolean) {
            _isLightBackground.value = newBoolean
        }
    }
    val isLightBackground get() = _isLightBackground as LiveData<Boolean>
    
    private val _playMode = MutableLiveData<Int>()
    fun updatePlayMode(newPlayMode: Int) {
        if (_playMode.value != newPlayMode) {
            _playMode.value = newPlayMode
        }
    }
    val playMode get() = _playMode as LiveData<Int>
    val playModeValue get() = _playMode.value
    
}