package projekt.cloud.piece.music.player.ui.fragment.mainHost

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainHostViewModel: ViewModel() {

    private val _bottomMargin = MutableLiveData(0)
    val bottomMargin: LiveData<Int>
        get() = _bottomMargin
    fun setBottomMargin(bottomMargin: Int) {
        if (bottomMargin >= 0) {
            _bottomMargin.value = bottomMargin
        }
    }

}