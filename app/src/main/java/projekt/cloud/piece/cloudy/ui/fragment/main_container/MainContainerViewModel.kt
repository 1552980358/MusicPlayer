package projekt.cloud.piece.cloudy.ui.fragment.main_container

import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainContainerViewModel: ViewModel() {

    companion object MainContainerViewModelUtil {

        fun MainContainerFragment.mainContainerViewModel(): Lazy<MainContainerViewModel> {
            return viewModels()
        }

    }

    private val _miniPlayerHeight = MutableLiveData(0)
    val miniPlayerHeight: LiveData<Int>
        get() = _miniPlayerHeight

    fun updateMiniPlayerHeight(newHeight: Int) {
        _miniPlayerHeight.value = newHeight
    }

}