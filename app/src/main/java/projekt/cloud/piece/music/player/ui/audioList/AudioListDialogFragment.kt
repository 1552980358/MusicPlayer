package projekt.cloud.piece.music.player.ui.audioList

import android.app.Dialog
import android.content.res.ColorStateList.valueOf
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import lib.github1552980358.ktExtension.androidx.fragment.app.getDrawable
import lib.github1552980358.ktExtension.androidx.fragment.app.show
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseDialogFragment
import projekt.cloud.piece.music.player.database.base.BaseTitledItem
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.databinding.DialogFragmentAudioListBinding
import projekt.cloud.piece.music.player.service.play.Extra
import projekt.cloud.piece.music.player.ui.audioList.util.RecyclerViewAdapterUtil
import projekt.cloud.piece.music.player.util.Constant.PLAYLIST_LIKES
import projekt.cloud.piece.music.player.util.ImageUtil.loadAlbumArtRaw
import projekt.cloud.piece.music.player.util.ImageUtil.loadPlaylistRaw

class AudioListDialogFragment: BaseDialogFragment() {

    companion object {
        const val ITEM_TYPE_ALBUM = 0
        const val ITEM_TYPE_ARTIST = 1
        const val ITEM_TYPE_PLAYLIST = 2
    }

    private var _binding: DialogFragmentAudioListBinding? = null
    private val binding get() = _binding!!
    private val coordinatorLayout get() = binding.coordinatorLayout
    private val floatingActionButton get() = binding.floatingActionButton
    private val collapsingToolbarLayout get() = binding.collapsingToolbarLayout
    private val imageView get() = binding.imageView

    private lateinit var recyclerViewAdapterUtil: RecyclerViewAdapterUtil

    private val transportControls get() = activityViewModel.mediaControllerCompat.transportControls
    private val database get() = activityViewModel.database

    private lateinit var titledItem: BaseTitledItem
    private var itemType = 0

    private lateinit var audioList: List<AudioItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Theme_MusicPlayer_AudioListDialogFragment)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.dialog_fragment_audio_list, container, false)
        with(binding.imageView) {
            layoutParams = layoutParams.apply { height = resources.displayMetrics.widthPixels }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerViewAdapterUtil = RecyclerViewAdapterUtil(binding.recyclerView) {
            transportControls.playFromMediaId(audioList[it].id, bundleOf(Extra.EXTRA_LIST to audioList, Extra.EXTRA_INDEX to it))
        }

        io {
            database.color.query(titledItem.id).apply {
                ui {
                    coordinatorLayout.setStatusBarBackgroundColor(backgroundColor)
                    with(collapsingToolbarLayout) {
                        contentScrim = ColorDrawable(backgroundColor)
                        statusBarScrim = ColorDrawable(backgroundColor)
                        setCollapsedTitleTextColor(primaryColor)
                        setExpandedTitleColor(primaryColor)
                    }
                    with(floatingActionButton) {
                        backgroundTintList = valueOf(primaryColor)
                        imageTintList = valueOf(backgroundColor)
                    }
                }
            }

            var title = titledItem.title
            val headerImage: Bitmap

            when (itemType) {
                ITEM_TYPE_ALBUM -> {
                    when (val image = requireContext().loadAlbumArtRaw(titledItem.id)) {
                        null -> ui { imageView.setImageResource(R.drawable.ic_default_album) }
                        else -> ui { imageView.setImageBitmap(image) }
                    }
                    audioList = database.audio.queryAlbum(titledItem.id)
                }
                ITEM_TYPE_ARTIST -> {
                    headerImage = getDrawable(R.drawable.ic_artist_default)!!.toBitmap()
                    ui { binding.imageView.setImageBitmap(headerImage) }
                    audioList = database.audio.queryArtist(titledItem.title)
                }
                ITEM_TYPE_PLAYLIST -> {
                    when (titledItem.id) {
                        PLAYLIST_LIKES -> {
                            ui { binding.imageView.setImageResource(R.drawable.ic_heart_default) }
                            title = getString(R.string.playlist_likes)
                        }
                        else -> {
                            when (val image = requireContext().loadPlaylistRaw(titledItem.id)) {
                                null -> ui { imageView.setImageResource(R.drawable.ic_playlist_default) }
                                else -> ui { imageView.setImageBitmap(image) }
                            }
                        }
                    }
                    audioList = database.playlistContent.queryAudio(titledItem.id)
                }
            }
            binding.title = title
            audioList = audioList.sortedBy { it.path }
            audioList.forEachIndexed { index, audioItem ->
                audioItem.artistItem = database.artist.query(audioItem.artist)
                audioItem.albumItem = database.album.query(audioItem.album)
                audioItem.index = index
            }
            ui { recyclerViewAdapterUtil.audioList = audioList }
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setLayout(MATCH_PARENT, MATCH_PARENT)
        dialog.window?.attributes?.windowAnimations = R.style.Theme_MusicPlayer_AudioListDialogFragment
        return dialog
    }

    fun showWithArgument(item: BaseTitledItem, type: Int, fragmentActivity: FragmentActivity) {
        titledItem = item
        itemType = type
        show(fragmentActivity)
    }

}