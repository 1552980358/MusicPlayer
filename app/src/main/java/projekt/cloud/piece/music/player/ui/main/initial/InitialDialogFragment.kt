package projekt.cloud.piece.music.player.ui.main.initial

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.Job
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.databinding.DialogFragmentInitialBinding
import projekt.cloud.piece.music.player.item.Album
import projekt.cloud.piece.music.player.item.Artist
import projekt.cloud.piece.music.player.item.Audio
import projekt.cloud.piece.music.player.room.AudioDatabase.Companion.audioDatabase
import projekt.cloud.piece.music.player.util.ArtUtil.SMALL_IMAGE_PIXEL_SIZE
import projekt.cloud.piece.music.player.util.ArtUtil.SUFFIX_LARGE
import projekt.cloud.piece.music.player.util.ArtUtil.SUFFIX_SMALL
import projekt.cloud.piece.music.player.util.ArtUtil.TYPE_ALBUM
import projekt.cloud.piece.music.player.util.ArtUtil.pathOf
import projekt.cloud.piece.music.player.util.BitmapUtil.png
import projekt.cloud.piece.music.player.util.BitmapUtil.resize
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui
import projekt.cloud.piece.music.player.util.FileUtil.writeByteArray
import projekt.cloud.piece.music.player.util.MediaStoreUtil.queryMediaStore
import projekt.cloud.piece.music.player.util.MediaStoreUtil.requestAlbumArt

class InitialDialogFragment: DialogFragment() {
    
    companion object {
        private const val SP_INITIALIZED = "initialized"
        
        fun isInitialized(context: Context): Boolean =
            PreferenceManager.getDefaultSharedPreferences(context)
                .contains(SP_INITIALIZED)
    }
    
    private var _binding: DialogFragmentInitialBinding? = null
    private val binding: DialogFragmentInitialBinding
        get() = _binding!!
    
    private val root: View
        get() = binding.root
    private val materialTextView: MaterialTextView
        get() = binding.materialTextView
    
    private var job: Job? = null
    
    private var callback: (() -> Unit)? = null
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogFragmentInitialBinding.inflate(layoutInflater)
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.initial_title)
            .setView(root)
            .create()
    }
    
    override fun onStart() {
        super.onStart()
        job = io {
            val audioList = arrayListOf<Audio>()
            val artistList = arrayListOf<Artist>()
            val albumList = arrayListOf<Album>()
            requireContext().queryMediaStore(audioList, artistList, albumList)
            ui { materialTextView.setText(R.string.initial_content_loading_image) }
            albumList.forEach { album ->
                requireContext().requestAlbumArt(album)?.let { albumArt ->
                    writeByteArray(requireContext().pathOf(TYPE_ALBUM, album.id, SUFFIX_LARGE), albumArt.png)
                    writeByteArray(requireContext().pathOf(TYPE_ALBUM, album.id, SUFFIX_SMALL), albumArt.resize(SMALL_IMAGE_PIXEL_SIZE).png)
                }
            }
            ui { materialTextView.setText(R.string.initial_content_storing_data) }
            with(audioDatabase) {
                artistDao().insert(*artistList.toTypedArray())
                albumDao().insert(*albumList.toTypedArray())
                audioDao().insert(*audioList.toTypedArray())
            }
            setInitialized()
            ui {
                callback?.invoke()
                dismissNow()
            }
        }
    }
    
    fun setCallback(callback: () -> Unit) = apply {
        this.callback = callback
    }
    
    @Suppress("ApplySharedPref")
    private fun setInitialized() =
        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .edit().putBoolean(SP_INITIALIZED, true).commit()
    
}