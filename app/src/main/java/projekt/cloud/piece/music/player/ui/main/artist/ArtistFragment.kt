package projekt.cloud.piece.music.player.ui.main.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import lib.github1552980358.ktExtension.androidx.fragment.app.getDrawable
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.databinding.FragmentArtistBinding
import projekt.cloud.piece.music.player.ui.audioList.AudioListDialogFragment
import projekt.cloud.piece.music.player.ui.audioList.AudioListDialogFragment.Companion.ITEM_TYPE_ARTIST
import projekt.cloud.piece.music.player.ui.main.MainFragment
import projekt.cloud.piece.music.player.ui.main.MainViewModel
import projekt.cloud.piece.music.player.ui.main.artist.util.RecyclerViewAdapterUtil

class ArtistFragment: BaseFragment() {

    companion object {
        private const val TAG = "ArtistFragment"
    }

    private var _binding: FragmentArtistBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel

    private lateinit var recyclerViewAdapterUtil: RecyclerViewAdapterUtil

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_artist, container, false)
        mainViewModel = ViewModelProvider(parentFragment as MainFragment)[MainViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        when {
            mainViewModel.isArtistListLoaded -> {
                recyclerViewAdapterUtil = RecyclerViewAdapterUtil(
                    binding.root,
                    mapOf(),
                    mainViewModel.defaultAlbumCover,
                    mainViewModel.artistList) { item ->
                    AudioListDialogFragment().showWithArgument(item, ITEM_TYPE_ARTIST, requireActivity())
                }
            }
            else -> {
                mainViewModel.defaultArtistArt = getDrawable(R.drawable.ic_artist_default)!!.toBitmap()
                recyclerViewAdapterUtil = RecyclerViewAdapterUtil(binding.root, mapOf(), mainViewModel.defaultArtistArt) { item ->
                    AudioListDialogFragment().showWithArgument(item, ITEM_TYPE_ARTIST, requireActivity())
                }
                mainViewModel.isArtistListLoaded = true
                loadArtistList()
            }
        }

        activityViewModel.setRefreshObserver(TAG) { isRefreshing, _, _, artistList ->
            if (!isRefreshing && artistList != null) {
                mainViewModel.artistList = artistList
                ui { recyclerViewAdapterUtil.artistList = artistList }
            }
        }
    }

    override fun onDestroyView() {
        activityViewModel.removeAllObservers(TAG)
        super.onDestroyView()
        _binding = null
    }

    private fun loadArtistList() = io {
        mainViewModel.artistList = activityViewModel.database.artist.query()
        ui { recyclerViewAdapterUtil.artistList = mainViewModel.artistList }
    }

}