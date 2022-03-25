package projekt.cloud.piece.music.player.ui.main.playlist

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import lib.github1552980358.ktExtension.androidx.fragment.app.getDrawable
import lib.github1552980358.ktExtension.androidx.fragment.app.show
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.database.item.PlaylistItem
import projekt.cloud.piece.music.player.databinding.FragmentPlaylistBinding
import projekt.cloud.piece.music.player.ui.audioList.AudioListDialogFragment
import projekt.cloud.piece.music.player.ui.audioList.AudioListDialogFragment.Companion.ITEM_TYPE_PLAYLIST
import projekt.cloud.piece.music.player.ui.main.MainFragment
import projekt.cloud.piece.music.player.ui.main.MainViewModel
import projekt.cloud.piece.music.player.ui.main.playlist.dialogFragment.AddPlaylistDialogFragment
import projekt.cloud.piece.music.player.ui.main.playlist.util.RecyclerViewAdapterUtil
import projekt.cloud.piece.music.player.util.ImageUtil.loadPlaylist40Dp

class PlaylistFragment: BaseFragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerViewAdapterUtil: RecyclerViewAdapterUtil
    private lateinit var mainViewModel: MainViewModel

    private var addPlaylistDialogFragment: AddPlaylistDialogFragment? = null

    private val playlistArtMap = mutableMapOf<String, Bitmap>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
         _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainViewModel = ViewModelProvider(parentFragment as MainFragment)[MainViewModel::class.java]
        if (!activityViewModel.isPlaylistLoaded) {
            activityViewModel.defaultPlaylistArt = getDrawable(R.drawable.ic_playlist_default)!!.toBitmap()
        }
        recyclerViewAdapterUtil = RecyclerViewAdapterUtil(binding.root, playlistArtMap, activityViewModel.defaultPlaylistArt) { item ->
            AudioListDialogFragment().showWithArgument(item, ITEM_TYPE_PLAYLIST, requireActivity())
        }
        io {
            if (!activityViewModel.isPlaylistLoaded) {
                activityViewModel.playlistList = activityViewModel.database.playlist.query().toMutableList() as ArrayList<PlaylistItem>
            }
            requireContext().loadPlaylist40Dp(playlistArtMap)
            ui { recyclerViewAdapterUtil.playlistList = activityViewModel.playlistList }
            activityViewModel.isPlaylistLoaded = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_playlist, menu)
    }

    override fun onResume() {
        super.onResume()
        if (activityViewModel.playlistListUpdated) {
            recyclerViewAdapterUtil.playlistList = activityViewModel.playlistList
            activityViewModel.playlistListUpdated = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_playlist -> {
                addPlaylistDialogFragment = AddPlaylistDialogFragment().apply {
                    setCallback { playlistItem, bitmap ->
                        bitmap?.let { playlistArtMap[playlistItem.id] = it }
                        ui {
                            activityViewModel.playlistList.add(playlistItem)
                            recyclerViewAdapterUtil.playlistList = activityViewModel.playlistList
                        }
                    }
                }
                addPlaylistDialogFragment?.show(this)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}