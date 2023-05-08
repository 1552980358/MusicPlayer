package projekt.cloud.piece.music.player.ui.fragment.album

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.imageview.ShapeableImageView
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseLayoutCompat
import projekt.cloud.piece.music.player.base.BaseMultiDensityFragment
import projekt.cloud.piece.music.player.base.interfaces.BackPressedInterface
import projekt.cloud.piece.music.player.databinding.AlbumCoverBinding
import projekt.cloud.piece.music.player.databinding.FragmentAlbumBinding
import projekt.cloud.piece.music.player.storage.runtime.databaseView.AlbumView
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity
import projekt.cloud.piece.music.player.util.AutoExpandableAppBarLayoutContentUtil.setupAutoExpandableAppBarLayout
import projekt.cloud.piece.music.player.util.ScreenDensity
import projekt.cloud.piece.music.player.util.ScreenDensity.COMPACT
import projekt.cloud.piece.music.player.util.ScreenDensity.EXPANDED
import projekt.cloud.piece.music.player.util.ScreenDensity.MEDIUM
import projekt.cloud.piece.music.player.util.TimeUtil.durationStr
import projekt.cloud.piece.music.player.util.UriUtil.albumArtUri

abstract class AlbumLayoutCompat(binding: FragmentAlbumBinding): BaseLayoutCompat<FragmentAlbumBinding>(binding) {

    companion object AlbumLayoutCompatUtil {
         fun inflate(screenDensity: ScreenDensity, binding: FragmentAlbumBinding): AlbumLayoutCompat {
            return when (screenDensity) {
                COMPACT -> CompatImpl(binding)
                MEDIUM -> W600dpImpl(binding)
                EXPANDED -> W1240dpImpl(binding)
            }
        }
    }

    private val recyclerView: RecyclerView
        get() = binding.recyclerView

    open fun setupAlbumCover(fragment: Fragment, albumId: String) = Unit

    open fun setupCollapsingAppBar(fragment: Fragment) = Unit

    open fun setupNavigation(fragment: Fragment) = Unit

    open fun setupAlbumMetadata(fragment: Fragment, albumView: AlbumView) = Unit

    fun setRecyclerViewAdapter(
        audioList: List<AudioMetadataEntity>, onItemClick: (String) -> Unit
    ) {
        recyclerView.adapter = AlbumRecyclerAdapter(audioList, onItemClick)
    }

    private class CompatImpl(binding: FragmentAlbumBinding): AlbumLayoutCompat(binding), BackPressedInterface {

        private val appBarLayout: AppBarLayout
            get() = binding.appBarLayout
        private val toolbar: MaterialToolbar
            get() = binding.materialToolbar

        private val albumCover: AlbumCoverBinding
            get() = binding.albumCover
        private val cover: ShapeableImageView
            get() = albumCover.shapeableImageViewCover
        private val albumCoverContainer: ConstraintLayout
            get() = albumCover.constraintLayoutAlbumCoverContainer

        private val _isExpandedLiveData = MutableLiveData<Boolean>()
        private val isExpandedLiveData: LiveData<Boolean>
            get() = _isExpandedLiveData

        override fun setupAlbumCover(fragment: Fragment, albumId: String) {
            Glide.with(fragment)
                .load(albumId.albumArtUri)
                .placeholder(R.drawable.ic_round_album_24)
                .into(cover)
        }

        override fun setupCollapsingAppBar(fragment: Fragment) {
            appBarLayout.setupAutoExpandableAppBarLayout(
                fragment,
                constraintLayout = albumCoverContainer,
                isExpandedLiveData = _isExpandedLiveData,
                expandedConstraintSet = ConstraintSet().apply { clone(albumCoverContainer) },
                collapsedConstraintSet = ConstraintSet().apply {
                    clone(fragment.requireContext(), R.layout.album_cover_collapsed)
                },
                /**
                 * Should not directly set as `appBarLayout.totalScrollRange / 2`
                 * because [setupCollapsingAppBar] is called
                 * during [BaseMultiDensityFragment.onSetupLayoutCompat], i.e. [Fragment.onViewCreated],
                 * which does not having [Fragment.onResume] be called, i.e. no height known.
                 * So, appBarLayout.totalScrollRange / 2 will be 0 if not be assigned by `lazy { ... }`
                 **/
                transitionLimit = { appBarLayout.totalScrollRange / 2 }
            )
        }

        override fun setupAlbumMetadata(fragment: Fragment, albumView: AlbumView) {
            val fragmentLabel = fragment.getString(R.string.album_title)

            val artistName = albumView.title.also {
                albumCover.title = it
            }
            val metadataStr = fragment.getString(
                R.string.artist_metadata_str, albumView.songCount, albumView.duration.durationStr
            ).also { albumCover.metadataStr = it }

            isExpandedLiveData.observe(fragment.viewLifecycleOwner) { isAppBarExpanded ->
                when {
                    isAppBarExpanded -> {
                        toolbar.title = artistName
                        toolbar.subtitle = metadataStr
                    }
                    else -> {
                        toolbar.title = fragmentLabel
                        toolbar.subtitle = null
                    }
                }
            }
        }

        override fun setupNavigation(fragment: Fragment) {
            toolbar.setNavigationOnClickListener {
                fragment.requireActivity()
                    .onBackPressedDispatcher
                    .onBackPressed()
            }
        }

        override fun onBackPressed(fragment: Fragment): Boolean {
            fragment.findNavController()
                .navigateUp()
            return true
        }

    }

    private class W600dpImpl(binding: FragmentAlbumBinding): AlbumLayoutCompat(binding)

    private class W1240dpImpl(binding: FragmentAlbumBinding): AlbumLayoutCompat(binding)

}