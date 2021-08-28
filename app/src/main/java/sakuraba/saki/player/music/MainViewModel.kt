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
    
}