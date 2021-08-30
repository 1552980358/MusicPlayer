package sakuraba.saki.player.music

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    
    private val _progress = MutableLiveData<Int>()
    fun updateProgress(newProgress: Int) {
        _progress.value = newProgress
    }
    val progress get() = _progress as LiveData<Int>
    
    private val _state = MutableLiveData<Int>()
    fun updateNewState(newState: Int) {
        if (_state.value != newState) {
            _state.value = newState
        }
    }
    val state get() = _state as LiveData<Int>
    val stateValue get() = _state.value
    
}