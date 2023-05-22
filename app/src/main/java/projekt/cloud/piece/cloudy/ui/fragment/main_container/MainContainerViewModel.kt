package projekt.cloud.piece.cloudy.ui.fragment.main_container

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView
import projekt.cloud.piece.cloudy.util.CoroutineUtil.defaultBlocking
import projekt.cloud.piece.cloudy.util.LifecycleOwnerUtil.main

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

    private var _metadata = MutableLiveData<MetadataView>()
    val metadata: LiveData<MetadataView>
        get() = _metadata
    fun setMetadata(fragment: Fragment, mediaItem: MediaItem) {
        fragment.main {
            _metadata.value = getMetadataFromMediaItem(mediaItem)
        }
    }

    private suspend fun getMetadataFromMediaItem(mediaItem: MediaItem): MetadataView {
        return defaultBlocking {
            MetadataView.fromMediaItem(mediaItem)
        }
    }

}