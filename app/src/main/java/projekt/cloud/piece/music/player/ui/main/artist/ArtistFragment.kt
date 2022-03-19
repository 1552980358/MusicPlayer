package projekt.cloud.piece.music.player.ui.main.artist

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
import lib.github1552980358.ktExtension.androidx.fragment.app.getDrawable
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.database.base.BaseTitledItem
import projekt.cloud.piece.music.player.databinding.FragmentArtistBinding
import projekt.cloud.piece.music.player.ui.audioList.AudioListFragment.Companion.EXTRA_TYPE_ARTIST
import projekt.cloud.piece.music.player.ui.main.MainFragment
import projekt.cloud.piece.music.player.ui.main.MainFragmentDirections
import projekt.cloud.piece.music.player.ui.main.MainViewModel
import projekt.cloud.piece.music.player.ui.main.artist.util.RecyclerViewAdapterUtil

class ArtistFragment: BaseFragment() {

    private var _binding: FragmentArtistBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel

    private lateinit var navController: NavController

    private lateinit var recyclerViewAdapterUtil: RecyclerViewAdapterUtil

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_artist, container, false)
        mainViewModel = ViewModelProvider(parentFragment as MainFragment)[MainViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = findNavController()
        when {
            mainViewModel.isArtistListLoaded -> {
                recyclerViewAdapterUtil = RecyclerViewAdapterUtil(
                    binding.root,
                    mapOf(),
                    mainViewModel.defaultAlbumCover,
                    mainViewModel.artistList) { rootView, artistItem ->
                    navigateToAudioList(rootView, artistItem)
                }
            }
            else -> {
                mainViewModel.defaultArtistArt = getDrawable(R.drawable.ic_artist_default)!!.toBitmap()
                recyclerViewAdapterUtil = RecyclerViewAdapterUtil(binding.root, mapOf(), mainViewModel.defaultArtistArt) { rootView, artistItem ->
                    navigateToAudioList(rootView, artistItem)
                }
                io {
                    mainViewModel.artistList = activityViewModel.database.artist.query()
                    mainViewModel.isArtistListLoaded = true
                    ui { recyclerViewAdapterUtil.artistList = mainViewModel.artistList }
                }
            }
        }
    }

    private fun navigateToAudioList(rootView: View, item: BaseTitledItem) {
        navController.navigate(
            MainFragmentDirections.actionNavMainToNavAudioList(item, EXTRA_TYPE_ARTIST),
            FragmentNavigatorExtras(rootView to rootView.transitionName)
        )
    }

}