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
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.database.item.PlaylistContentItem
import projekt.cloud.piece.music.player.database.item.PlaylistItem
import projekt.cloud.piece.music.player.databinding.DialogFragmentAddToPlaylistBinding
import projekt.cloud.piece.music.player.ui.addToPlaylist.util.RecyclerViewAdapterUtil
import projekt.cloud.piece.music.player.ui.main.playlist.dialogFragment.AddPlaylistDialogFragment
import projekt.cloud.piece.music.player.util.ImageUtil.loadPlaylist40Dp

class AddToPlaylistDialogFragment: BaseDialogFragment() {

    private var _binding: DialogFragmentAddToPlaylistBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerViewAdapterUtil: RecyclerViewAdapterUtil

    private lateinit var audioItem: AudioItem

    private val playlistArtCover = mutableMapOf<String, Bitmap>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), R.layout.dialog_fragment_add_to_playlist, null, false)
        recyclerViewAdapterUtil = RecyclerViewAdapterUtil(binding.recyclerView, playlistArtCover, getDrawable(R.drawable.ic_playlist_default)!!.toBitmap()) {
            addToPlaylist(audioItem, it)
            dismiss()
        }

        binding.relativeLayout.setOnClickListener {
            AddPlaylistDialogFragment().apply {
                setCallback { playlistItem, _ -> addToPlaylist(audioItem, playlistItem) }
            }.show(requireActivity())
            dismiss()
        }

        io {
            val list = activityViewModel.database.playlist.query()
            requireContext().loadPlaylist40Dp(playlistArtCover)
            recyclerViewAdapterUtil.playlistList = list
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

    fun setAudioItem(audioItem: AudioItem) {
        this.audioItem = audioItem
    }

    private fun addToPlaylist(audioItem: AudioItem, playlistItem: PlaylistItem) = io {
        activityViewModel.database.playlistContent.insert(PlaylistContentItem(audio = audioItem.id, playlist = playlistItem.id))
        activityViewModel.playlistListUpdated = true
    }

}