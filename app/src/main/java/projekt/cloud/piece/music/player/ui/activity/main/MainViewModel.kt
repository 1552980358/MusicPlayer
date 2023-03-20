package projekt.cloud.piece.music.player.ui.activity.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    private val _isMediaBrowserCompatConnected = MutableLiveData(false)
    val isMediaBrowserCompatConnected: LiveData<Boolean>
        get() = _isMediaBrowserCompatConnected
    fun setIsMediaBrowserCompatConnected(isConnected: Boolean) {
        _isMediaBrowserCompatConnected.value = isConnected
    }

}