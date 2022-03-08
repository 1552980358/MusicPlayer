package sakuraba.saki.player.music

import android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    private val _state = MutableLiveData<Int>()
    fun updateNewState(newState: Int) {
        if (newState != STATE_BUFFERING && _state.value != newState) {
            _state.value = newState
        }
    }
    val state get() = _state as LiveData<Int>
    val stateValue get() = _state.value
    
}