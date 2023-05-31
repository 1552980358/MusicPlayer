package projekt.cloud.piece.cloudy.ui.fragment.main_container

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.navigation.fragment.NavHostFragment
import projekt.cloud.piece.cloudy.R
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView
import projekt.cloud.piece.cloudy.util.CoroutineUtil.defaultBlocking
import projekt.cloud.piece.cloudy.util.LifecycleOwnerUtil.main

class MainContainerViewModel: ViewModel() {

    companion object MainContainerViewModelUtil {

        /**
         * [MainContainerViewModel.mainContainerViewModel]
         * @extends [MainContainerFragment]
         * @return [Lazy]<[MainContainerViewModel]>
         **/
        fun MainContainerFragment.mainContainerViewModel(): Lazy<MainContainerViewModel> {
            return viewModels(
                ownerProducer = {
                    /**
                     * // DataBindingUtil.bind<FragmentMainContainerBinding>(requireView())!!
                     * //     .fragmentContainerView
                     *
                     * Maybe using [android.view.View.findViewById] is faster?
                     **/
                    requireView().findViewById<FragmentContainerView>(R.id.fragment_container_view)
                        .getFragment<NavHostFragment>()
                        .navController
                        .getViewModelStoreOwner(R.id.nav_graph_main_container)
                }
            )
        }

    }

    /**
     * [MainContainerViewModel.miniPlayerHidingHeight]
     * @wrap [androidx.lifecycle.MutableLiveData]
     * @type [Int]
     **/
    private val _miniPlayerHidingHeight = MutableLiveData(0)
    val miniPlayerHidingHeight: LiveData<Int>
        get() = _miniPlayerHidingHeight

    /**
     * [MainContainerViewModel.updateMiniPlayerHidingHeight]
     * @param newHeight [Int]
     *
     * Update [MainContainerViewModel.miniPlayerHidingHeight]
     **/
    fun updateMiniPlayerHidingHeight(newHeight: Int) {
        _miniPlayerHidingHeight.value = newHeight
    }

    /**
     * [MainContainerViewModel.miniPlayerExpandingHeight]
     * @wrap [androidx.lifecycle.MutableLiveData]
     * @type [Int]
     **/
    private val _miniPlayerExpandingHeight = MutableLiveData(0)
    val miniPlayerExpandingHeight: LiveData<Int>
        get() = _miniPlayerExpandingHeight

    /**
     * [MainContainerViewModel.updateMiniPlayerHidingHeight]
     * @param newHeight [Int]
     *
     * Update [MainContainerViewModel.miniPlayerHidingHeight]
     **/
    fun updateMiniPlayerExpandingHeight(newHeight: Int) {
        _miniPlayerExpandingHeight.value = newHeight
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