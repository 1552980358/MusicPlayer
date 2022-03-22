package projekt.cloud.piece.music.player.ui.audioList

import android.content.res.ColorStateList.valueOf
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import com.google.android.material.transition.MaterialContainerTransform
import lib.github1552980358.ktExtension.androidx.fragment.app.getDrawable
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.database.base.BaseTitledItem
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.databinding.FragmentAudioListBinding
import projekt.cloud.piece.music.player.service.play.Extra.EXTRA_INDEX
import projekt.cloud.piece.music.player.service.play.Extra.EXTRA_LIST
import projekt.cloud.piece.music.player.ui.audioList.util.RecyclerViewAdapterUtil
import projekt.cloud.piece.music.player.util.ImageUtil.loadAlbumArtRaw
import projekt.cloud.piece.music.player.util.ImageUtil.loadPlaylistRaw

class AudioListFragment: BaseFragment() {

    companion object {
        const val EXTRA_ITEM = "item"
        const val EXTRA_TYPE = "type"

        const val EXTRA_TYPE_ALBUM = 0
        const val EXTRA_TYPE_ARTIST = 1
        const val EXTRA_TYPE_PLAYLIST = 2
    }

    private var _binding: FragmentAudioListBinding? = null
    private val binding get() = _binding!!
    private val collapsingToolbarLayout get() = binding.collapsingToolbarLayout
    private val floatingActionButton get() = binding.floatingActionButton

    private val transportControls get() = activityViewModel.mediaControllerCompat.transportControls
    private val database get() = activityViewModel.database

    private lateinit var item: BaseTitledItem
    private var type = EXTRA_TYPE_ALBUM

    private lateinit var recyclerViewAdapterUtil: RecyclerViewAdapterUtil

    private lateinit var audioList: List<AudioItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform()

        item = requireArguments().getSerializable(EXTRA_ITEM) as BaseTitledItem
        type = requireArguments().getInt(EXTRA_TYPE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_audio_list, container, false)
        binding.root.transitionName = item.id
        binding.imageView.apply {
            layoutParams = layoutParams.apply { height = resources.displayMetrics.widthPixels }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerViewAdapterUtil = RecyclerViewAdapterUtil(binding.recyclerView) {
            transportControls.playFromMediaId(audioList[it].id, bundleOf(EXTRA_LIST to audioList, EXTRA_INDEX to it))
        }

        binding.title = item.title
        floatingActionButton.setOnClickListener {
            transportControls.playFromMediaId(audioList.first().id, bundleOf(EXTRA_LIST to audioList, EXTRA_INDEX to 0))
        }

        io {
            database.color.query(item.id).apply {
                ui {
                    requireActivity().window.statusBarColor = backgroundColor
                    with(collapsingToolbarLayout) {
                        contentScrim = ColorDrawable(backgroundColor)
                        setCollapsedTitleTextColor(primaryColor)
                        setExpandedTitleColor(primaryColor)
                    }
                    with(floatingActionButton) {
                        backgroundTintList = valueOf(primaryColor)
                        imageTintList = valueOf(backgroundColor)
                    }
                }
            }

            val headerImage: Bitmap
            when (type) {
                EXTRA_TYPE_ALBUM -> {
                    headerImage = requireContext().loadAlbumArtRaw(item.id) ?: getDrawable(R.drawable.ic_default_album)!!.toBitmap()
                    ui { binding.imageView.setImageBitmap(headerImage) }
                    audioList = database.audio.queryAlbum(item.id)
                }
                EXTRA_TYPE_ARTIST -> {
                    headerImage = getDrawable(R.drawable.ic_artist_default)!!.toBitmap()
                    ui { binding.imageView.setImageBitmap(headerImage) }
                    audioList = database.audio.queryArtist(item.title)
                }
                EXTRA_TYPE_PLAYLIST -> {
                    headerImage = requireContext().loadPlaylistRaw(item.id) ?: getDrawable(R.drawable.ic_playlist_default)!!.toBitmap()
                    ui { binding.imageView.setImageBitmap(headerImage) }
                    audioList = database.playlistContent.queryAudio(item.id)
                }
            }

            audioList.forEach { it.artistItem = database.artist.query(it.artist) }
            audioList.forEach { it.albumItem = database.album.query(it.album) }
            ui { recyclerViewAdapterUtil.audioList = audioList }
        }
    }

}