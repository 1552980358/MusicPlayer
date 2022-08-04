package projekt.cloud.piece.music.player.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    private var _isInitialized = MutableLiveData(false)
    fun setInitialized(value: Boolean) {
        _isInitialized.value = value
    }
    val isInitialized: LiveData<Boolean>
        get() = _isInitialized
    
}