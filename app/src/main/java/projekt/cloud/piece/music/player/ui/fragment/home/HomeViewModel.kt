package projekt.cloud.piece.music.player.ui.fragment.home

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel

class HomeViewModel: ViewModel() {

    private val _isOnTop = MutableLiveData(true)
    val isOnTop: Boolean
        get() = _isOnTop.value!!
    fun scrollToTop() {
        _isOnTop.value = true
    }
    fun updateTopState(isOnTop: Boolean) {
        _isOnTop.value = isOnTop
    }
    fun observeScrollToTop(lifecycleOwner: LifecycleOwner, observer: Observer<Boolean>) {
        _isOnTop.observe(lifecycleOwner, observer)
    }

}