package sakuraba.saki.player.music.ui.playlist

import android.graphics.Color.WHITE
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import lib.github1552980358.ktExtension.androidx.fragment.app.show
import sakuraba.saki.player.music.databinding.FragmentPlaylistBinding
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.base.BaseMainFragment
import sakuraba.saki.player.music.database.AudioDatabaseHelper
import sakuraba.saki.player.music.ui.common.addPlaylist.AddPlaylistDialogFragment
import sakuraba.saki.player.music.ui.playlist.util.RecyclerViewAdapterUtil
import sakuraba.saki.player.music.util.Constants.ANIMATION_DURATION_LONG
import java.util.concurrent.TimeUnit

class PlaylistFragment: BaseMainFragment() {

    private var _fragmentPlaylistBinding: FragmentPlaylistBinding? = null
    private val layout get() = _fragmentPlaylistBinding!!

    private lateinit var recyclerViewAdapterUtil: RecyclerViewAdapterUtil
    private val playlistList get() = recyclerViewAdapterUtil.playlistList
    private val bitmapMap get() = recyclerViewAdapterUtil.bitmapMap

    private lateinit var audioDatabaseHelper: AudioDatabaseHelper

    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _fragmentPlaylistBinding = FragmentPlaylistBinding.inflate(layoutInflater)
        setHasOptionsMenu(true)
        navController = findNavController()
        return layout.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerViewAdapterUtil = RecyclerViewAdapterUtil(activityInterface, layout.recyclerView) { position, extra ->
            navController.navigate(
                PlaylistFragmentDirections.actionNavPlaylistToNavPlaylistContent(playlistList[position]),
                extra
            )
        }
        layout.root.isEnabled = false
        audioDatabaseHelper = AudioDatabaseHelper(requireContext())

        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        postponeEnterTransition(ANIMATION_DURATION_LONG / 2, TimeUnit.MILLISECONDS)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_playlist, menu)
        menu.getItem(0).icon.setTint(WHITE)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add -> AddPlaylistDialogFragment { playlist ->
                if (playlistList.indexOfFirst { it.title == playlist.title } == -1) {
                    audioDatabaseHelper.apply {
                        createPlaylist(playlist)
                        writeComplete()
                    }
                    playlistList.add(playlist)
                    recyclerViewAdapterUtil.notifyDataSetChanged()
                }
            }.show(this)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        audioDatabaseHelper.close()
        _fragmentPlaylistBinding = null
        super.onDestroyView()
    }

}