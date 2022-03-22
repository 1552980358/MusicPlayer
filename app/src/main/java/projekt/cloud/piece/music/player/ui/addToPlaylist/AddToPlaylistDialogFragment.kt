package projekt.cloud.piece.music.player.ui.addToPlaylist

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog.Builder
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import lib.github1552980358.ktExtension.androidx.fragment.app.getDrawable
import lib.github1552980358.ktExtension.androidx.fragment.app.show
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseDialogFragment
import projekt.cloud.piece.music.player.database.item.PlaylistItem
import projekt.cloud.piece.music.player.databinding.DialogFragmentAddToPlaylistBinding
import projekt.cloud.piece.music.player.ui.addToPlaylist.util.RecyclerViewAdapterUtil
import projekt.cloud.piece.music.player.ui.main.playlist.dialogFragment.AddPlaylistDialogFragment
import projekt.cloud.piece.music.player.util.ImageUtil.loadPlaylist40Dp

class AddToPlaylistDialogFragment: BaseDialogFragment() {

    private var _binding: DialogFragmentAddToPlaylistBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerViewAdapterUtil: RecyclerViewAdapterUtil

    private lateinit var callback: (PlaylistItem) -> Unit

    private val playlistArtCover = mutableMapOf<String, Bitmap>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), R.layout.dialog_fragment_add_to_playlist, null, false)
        recyclerViewAdapterUtil = RecyclerViewAdapterUtil(binding.recyclerView, playlistArtCover, getDrawable(R.drawable.ic_playlist_default)!!.toBitmap()) {
            callback(it)
            dismiss()
        }

        binding.relativeLayout.setOnClickListener {
            AddPlaylistDialogFragment().apply {
                setCallback { playlistItem, _ -> callback(playlistItem) }
            }.show(requireActivity())
            dismiss()
        }

        io {
            val list = activityViewModel.database.playlist.query()
            requireContext().loadPlaylist40Dp(playlistArtCover)
            recyclerViewAdapterUtil.playlistList = list
            recyclerViewAdapterUtil.notifyDataSetChanged()
        }

        return Builder(requireContext())
            .setTitle(R.string.add_to_playlist_title)
            .setView(binding.root)
            .create()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun setCallback(callback: (PlaylistItem) -> Unit) {
        this.callback = callback
    }

}