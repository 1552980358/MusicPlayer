package sakuraba.saki.player.music.ui.playlist.playlistContent

import android.graphics.BitmapFactory.decodeResource
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import lib.github1552980358.ktExtension.androidx.fragment.app.pixelSizeOf
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.base.BaseMainFragment
import sakuraba.saki.player.music.databinding.FragmentPlaylistContentBinding
import sakuraba.saki.player.music.ui.playlist.playlistContent.util.RecyclerViewAdapterUtil
import sakuraba.saki.player.music.util.BitmapUtil.loadPlaylistRaw
import sakuraba.saki.player.music.util.Constants.ANIMATION_DURATION_LONG
import sakuraba.saki.player.music.util.Playlist
import java.util.concurrent.TimeUnit

class PlaylistContentFragment: BaseMainFragment() {

    private companion object {
        const val ARGUMENT_PLAYLIST = "playlist"
    }

    private var _fragmentPlaylistContent: FragmentPlaylistContentBinding? = null
    private val layout get() = _fragmentPlaylistContent!!

    private lateinit var recyclerViewAdapterUtil: RecyclerViewAdapterUtil
    private lateinit var playlist: Playlist
    private val audioInfoList get() = playlist.audioInfoList

    private lateinit var behavior: BottomSheetBehavior<LinearLayoutCompat>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _fragmentPlaylistContent = FragmentPlaylistContentBinding.inflate(inflater)

        playlist = requireArguments().getSerializable(ARGUMENT_PLAYLIST) as Playlist
        layout.imageView.apply {
            transitionName = playlist.titlePinyin
            setImageBitmap(requireContext().loadPlaylistRaw(playlist.titlePinyin) ?: decodeResource(resources, R.drawable.ic_playlist_bitmap))
        }
        recyclerViewAdapterUtil = RecyclerViewAdapterUtil(layout.recyclerView, playlist, activityInterface) {
            activityInterface.onFragmentListItemClick(it, audioInfoList[it], audioInfoList)
        }

        layout.textView.setOnClickListener {  }
        layout.textViewTitle.text = playlist.title
        layout.textViewDescription.text = playlist.description

        behavior = BottomSheetBehavior.from(layout.linearLayout)
        behavior.isHideable = false
        ui {
            behavior.peekHeight = layout.root.height - pixelSizeOf(R.dimen.playlist_content_relative_layout_root_height)
            layout.linearLayout.apply { layoutParams = layoutParams.apply { height = layout.root.height } }
        }

        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        postponeEnterTransition(ANIMATION_DURATION_LONG / 2, TimeUnit.MILLISECONDS)

        return layout.root
    }

}