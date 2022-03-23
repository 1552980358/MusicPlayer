package projekt.cloud.piece.music.player.ui.main.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.Hold
import lib.github1552980358.ktExtension.androidx.fragment.app.getDrawable
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.database.base.BaseTitledItem
import projekt.cloud.piece.music.player.databinding.FragmentAlbumBinding
import projekt.cloud.piece.music.player.ui.audioList.AudioListFragment.Companion.EXTRA_TYPE_ALBUM
import projekt.cloud.piece.music.player.ui.main.MainFragment
import projekt.cloud.piece.music.player.ui.main.MainFragmentDirections
import projekt.cloud.piece.music.player.ui.main.MainViewModel
import projekt.cloud.piece.music.player.ui.main.album.util.RecyclerViewAdapterUtil

class AlbumFragment: BaseFragment() {

    companion object {
        private const val TAG = "AlbumFragment"
    }

    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerViewAdapterUtil: RecyclerViewAdapterUtil

    private lateinit var navController: NavController

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(parentFragment as MainFragment)[MainViewModel::class.java]
        exitTransition = Hold()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_album, container, false)
        navController = findNavController()

        when {
            mainViewModel.isAlbumListLoaded -> {
                recyclerViewAdapterUtil = RecyclerViewAdapterUtil(binding.root,
                    activityViewModel.albumArtMap,
                    mainViewModel.defaultAlbumCover,
                    mainViewModel.albumList) { rootView, item ->
                    navigateToAudioList(rootView, item)
                }
            }
            else -> {
                mainViewModel.defaultAlbumCover = getDrawable(R.drawable.ic_default_album)!!.toBitmap()
                recyclerViewAdapterUtil = RecyclerViewAdapterUtil(binding.root,
                    activityViewModel.albumArtMap,
                    mainViewModel.defaultAlbumCover) { rootView, item ->
                    navigateToAudioList(rootView, item)
                }
                mainViewModel.isAlbumListLoaded = true
                loadAlbumList()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activityViewModel.setRefreshObserver(TAG) { isRefreshing, _, albumList, _ ->
            if (!isRefreshing && albumList != null) {
                mainViewModel.albumList = albumList
                ui { recyclerViewAdapterUtil.albumList = albumList }
            }
        }
    }

    override fun onDestroyView() {
        activityViewModel.removeAllObservers(TAG)
        super.onDestroyView()
        _binding = null
    }

    private fun navigateToAudioList(rootView: View, item: BaseTitledItem) {
        navController.navigate(
            MainFragmentDirections.actionNavMainToNavAudioList(item, EXTRA_TYPE_ALBUM),
            FragmentNavigatorExtras(rootView to rootView.transitionName)
        )
    }

    private fun loadAlbumList() = io {
        mainViewModel.albumList = activityViewModel.database.album.query()
        ui { recyclerViewAdapterUtil.albumList = mainViewModel.albumList }
    }

}