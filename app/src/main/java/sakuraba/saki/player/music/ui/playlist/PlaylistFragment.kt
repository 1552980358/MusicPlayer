package sakuraba.saki.player.music.ui.playlist

import android.graphics.Bitmap.createBitmap
import android.graphics.BitmapFactory.decodeResource
import android.graphics.Color.WHITE
import android.graphics.Matrix
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.MenuCompat.setGroupDividerEnabled
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import lib.github1552980358.ktExtension.androidx.fragment.app.show
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import projekt.cloud.piece.c2pinyin.pinyin
import sakuraba.saki.player.music.databinding.FragmentPlaylistBinding
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.base.BaseMainFragment
import sakuraba.saki.player.music.ui.common.addPlaylist.AddPlaylistDialogFragment
import sakuraba.saki.player.music.ui.common.input.PlaylistDetailEditDialogFragment
import sakuraba.saki.player.music.ui.playlist.util.RecyclerViewAdapterUtil
import sakuraba.saki.player.music.util.BitmapUtil.removePlaylist40Dp
import sakuraba.saki.player.music.util.BitmapUtil.removePlaylistRaw
import sakuraba.saki.player.music.util.BitmapUtil.writePlaylist40Dp
import sakuraba.saki.player.music.util.BitmapUtil.writePlaylistRaw
import sakuraba.saki.player.music.util.Constants.ANIMATION_DURATION_LONG
import java.util.concurrent.TimeUnit

class PlaylistFragment: BaseMainFragment() {

    private var _fragmentPlaylistBinding: FragmentPlaylistBinding? = null
    private val layout get() = _fragmentPlaylistBinding!!

    private lateinit var recyclerViewAdapterUtil: RecyclerViewAdapterUtil
    private val playlistList get() = recyclerViewAdapterUtil.playlistList
    private val bitmapMap get() = recyclerViewAdapterUtil.bitmapMap

    private val audioDatabaseHelper get() = activityInterface.audioDatabaseHelper

    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _fragmentPlaylistBinding = FragmentPlaylistBinding.inflate(layoutInflater)
        setHasOptionsMenu(true)
        navController = findNavController()
        return layout.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerViewAdapterUtil = RecyclerViewAdapterUtil(layout.recyclerView,
            activityInterface,
            decodeResource(resources, R.drawable.ic_playlist_bitmap),
            rootClickListener = { playlist, extra ->
                navController.navigate(
                    PlaylistFragmentDirections.actionNavPlaylistToNavPlaylistContent(playlist),
                    extra
                )
            },
            optionButtonClickListener = { playlist, relativeLayout ->
                PopupMenu(requireContext(), relativeLayout).apply {
                    inflate(R.menu.menu_popup_playlist)
                    menu.getItem(0).title = playlist.title
                    setGroupDividerEnabled(menu, true)
                    setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.menu_edit_detail -> {
                                PlaylistDetailEditDialogFragment(playlist) { playlist, title, description, bitmap ->
                                    io {
                                        val originTitle = playlist.title
                                        playlist.title = title
                                        playlist.titlePinyin = title.pinyin
                                        playlist.description = description ?: ""
                                        activityInterface.audioDatabaseHelper.apply {
                                            updatePlaylist(originTitle, playlist)
                                            writeComplete()
                                        }
                                        when (bitmap) {
                                            null -> {
                                                requireContext().removePlaylistRaw(playlist.titlePinyin)
                                                requireContext().removePlaylist40Dp(playlist.titlePinyin)
                                            }
                                            else -> {
                                                requireContext().writePlaylistRaw(playlist.titlePinyin, bitmap)
                                                requireContext().writePlaylist40Dp(playlist.titlePinyin,
                                                    createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, Matrix().apply {
                                                        (resources.getDimension(R.dimen.dp_40) / bitmap.width).apply { setScale(this, this) }
                                                    }, false)
                                                )
                                            }
                                        }
                                    }
                                    bitmapMap[playlist.titlePinyin] = bitmap
                                    recyclerViewAdapterUtil.notifyDataSetChanged()
                                }.show(this@PlaylistFragment)
                            }
                        }
                        return@setOnMenuItemClickListener true
                    }
                }.show()
            }
        )
        layout.root.isEnabled = false

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