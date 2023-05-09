package projekt.cloud.piece.music.player.ui.fragment.artist

import androidx.annotation.UiThread
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.BOTTOM
import androidx.constraintlayout.widget.ConstraintSet.END
import androidx.constraintlayout.widget.ConstraintSet.START
import androidx.constraintlayout.widget.ConstraintSet.TOP
import androidx.constraintlayout.widget.ConstraintSet.VISIBLE
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.withContext
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseLayoutCompat
import projekt.cloud.piece.music.player.base.BaseMultiDensityFragment
import projekt.cloud.piece.music.player.base.interfaces.BackPressedInterface
import projekt.cloud.piece.music.player.databinding.ArtistAvatarBinding
import projekt.cloud.piece.music.player.databinding.FragmentArtistBinding
import projekt.cloud.piece.music.player.storage.runtime.databaseView.ArtistView
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity
import projekt.cloud.piece.music.player.ui.fragment.library.LibraryFragment
import projekt.cloud.piece.music.player.util.AutoExpandableAppBarLayoutContentUtil.setupAutoExpandableAppBarLayout
import projekt.cloud.piece.music.player.util.CoroutineUtil.default
import projekt.cloud.piece.music.player.util.CoroutineUtil.main
import projekt.cloud.piece.music.player.util.FragmentUtil.findParent
import projekt.cloud.piece.music.player.util.KotlinUtil.ifFalse
import projekt.cloud.piece.music.player.util.ScreenDensity
import projekt.cloud.piece.music.player.util.ScreenDensity.COMPACT
import projekt.cloud.piece.music.player.util.ScreenDensity.EXPANDED
import projekt.cloud.piece.music.player.util.ScreenDensity.MEDIUM
import projekt.cloud.piece.music.player.util.TimeUtil.durationStr

abstract class ArtistLayoutCompat(
    binding: FragmentArtistBinding
): BaseLayoutCompat<FragmentArtistBinding>(binding) {

    companion object HomeLayoutCompatUtil {

        fun inflate(screenDensity: ScreenDensity, binding: FragmentArtistBinding): ArtistLayoutCompat {
            return when (screenDensity) {
                COMPACT -> CompatImpl(binding)
                MEDIUM -> W600dpImpl(binding)
                EXPANDED -> W1240dpImpl(binding)
            }
        }

    }

    private val recyclerView: RecyclerView
        get() = binding.recyclerView

    @UiThread
    open fun setupCollapsingAppBar(fragment: Fragment) = Unit

    @UiThread
    open fun setupNavigation(fragment: Fragment) = Unit

    @UiThread
    open fun setupArtistMetadata(fragment: Fragment, artistView: ArtistView) = Unit

    @UiThread
    open fun setupMargin(fragment: Fragment) = Unit

    fun setRecyclerViewAdapter(
        fragment: Fragment, audioList: List<AudioMetadataEntity>, onItemClick: (String) -> Unit
    ) {
        recyclerView.adapter = ArtistRecyclerAdapter(fragment, audioList, onItemClick)
    }

    private class CompatImpl(binding: FragmentArtistBinding): ArtistLayoutCompat(binding), BackPressedInterface {

        private val appBarLayout: AppBarLayout
            get() = binding.appBarLayout
        private val toolbar: MaterialToolbar
            get() = binding.materialToolbar

        private val avatar: ArtistAvatarBinding
            get() = binding.artistAvatar
        private val avatarContainer: ConstraintLayout
            get() = avatar.constraintLayoutAvatarContainer
        // TODO: Preserved for future
        // private val avatarArt: ShapeableImageView
        //     get() = avatar.shapeableImageViewArt

        private var _isExpandedLiveData = MutableLiveData<Boolean>()
        private val isExpandedLiveData: LiveData<Boolean>
            get() = _isExpandedLiveData

        override fun setupCollapsingAppBar(fragment: Fragment) {
            appBarLayout.setupAutoExpandableAppBarLayout(
                fragment,
                constraintLayout = avatarContainer,
                isExpandedLiveData = _isExpandedLiveData,
                expandedConstraintSet = createExpandedConstraintSet(avatarContainer),
                collapsedConstraintSet = createCollapsedConstraintSet(fragment, avatarContainer),
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

        private fun createExpandedConstraintSet(container: ConstraintLayout): ConstraintSet {
            return ConstraintSet().apply { clone(container) }
        }

        private fun createCollapsedConstraintSet(fragment: Fragment, container: ConstraintLayout): ConstraintSet {
            return createExpandedConstraintSet(container).apply {
                // Set size to ShapeableImageView
                val imageSize = fragment.resources.getDimensionPixelSize(R.dimen.md_spec_size_image_40)
                constrainHeight(R.id.shapeable_image_view_art, imageSize)
                constrainWidth(R.id.shapeable_image_view_art, imageSize)

                fragment.resources.getDimensionPixelSize(R.dimen.md_spec_spacing_hor_16).let { marginHorizontal ->
                    // Set horizontal margin to ShapeableImageView
                    setMargin(R.id.shapeable_image_view_art, START, marginHorizontal)
                    setMargin(R.id.shapeable_image_view_art, END, marginHorizontal)

                    // Connect ShapeableImageView and MaterialTextView
                    connect(R.id.shapeable_image_view_art, END, R.id.material_text_view_name, START, marginHorizontal)
                }

                // Set horizontal margin to ShapeableImageView
                fragment.resources.getDimensionPixelSize(R.dimen.md_spec_spacing_ver_8).let { marginVertical ->
                    setMargin(R.id.shapeable_image_view_art, TOP, marginVertical)
                    setMargin(R.id.shapeable_image_view_art, BOTTOM, marginVertical)
                }

                // Show texts
                setVisibility(R.id.material_text_view_name, VISIBLE)
                setVisibility(R.id.material_text_view_metadata, VISIBLE)
            }
        }

        override fun setupNavigation(fragment: Fragment) {
            toolbar.setNavigationOnClickListener {
                fragment.requireActivity()
                    .onBackPressedDispatcher
                    .onBackPressed()
            }
        }

        override fun setupArtistMetadata(fragment: Fragment, artistView: ArtistView) {
            val fragmentLabel = fragment.getString(R.string.artist_title)

            val artistName = artistView.name.also {
                avatar.name = it
            }
            val metadataStr = fragment.getString(
                R.string.artist_metadata_str, artistView.songCount, artistView.duration.durationStr
            ).also { avatar.metadataStr = it }

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

        override fun onBackPressed(fragment: Fragment): Boolean {
            fragment.findNavController()
                .navigateUp()
            return false
        }

    }

    private class W600dpImpl(binding: FragmentArtistBinding): ArtistLayoutCompat(binding) {

        private val root: CoordinatorLayout
            get() = binding.coordinatorLayoutRoot
        private val appBarLayout: AppBarLayout
            get() = binding.appBarLayout
        private val toolbar: MaterialToolbar
            get() = binding.materialToolbar

        private val avatar: ArtistAvatarBinding
            get() = binding.artistAvatar
        private val avatarContainer: ConstraintLayout
            get() = avatar.constraintLayoutAvatarContainer

        private val isExpandedMutableLiveData = MutableLiveData<Boolean>()

        override fun setupCollapsingAppBar(fragment: Fragment) {
            val avatarContainer = avatarContainer
            appBarLayout.setupAutoExpandableAppBarLayout(
                fragment,
                constraintLayout = avatarContainer,
                isExpandedMutableLiveData,
                expandedConstraintSet = createExpandedConstraintSet(avatarContainer),
                collapsedConstraintSet = createCollapsedConstraintSet(fragment, avatarContainer),
                transitionLimit = { appBarLayout.totalScrollRange / 2 }
            )
        }

        private fun createExpandedConstraintSet(constraintLayout: ConstraintLayout): ConstraintSet {
            return ConstraintSet().apply { clone(constraintLayout) }
        }

        private fun createCollapsedConstraintSet(fragment: Fragment, constraintLayout: ConstraintLayout): ConstraintSet {
            return createExpandedConstraintSet(constraintLayout).apply {
                val imageSize = fragment.resources.getDimensionPixelSize(R.dimen.md_spec_size_image_40)
                constrainHeight(R.id.shapeable_image_view_art, imageSize)
                constrainWidth(R.id.shapeable_image_view_art, imageSize)
            }
        }

        override fun setupNavigation(fragment: Fragment) {
            toolbar.setNavigationOnClickListener {
                fragment.requireActivity()
                    .onBackPressedDispatcher
                    .onBackPressed()
            }
        }

        override fun setupArtistMetadata(fragment: Fragment, artistView: ArtistView) {
            avatar.name = artistView.name
            avatar.metadataStr = fragment.getString(
                R.string.artist_metadata_str, artistView.songCount, artistView.duration.durationStr
            )
        }

        override fun setupMargin(fragment: Fragment) {
            fragment.lifecycleScope.main {
                findLibraryFragment(fragment)?.canSlide
                    .ifFalse(::removeRootPaddingStart)
            }
        }

        private suspend fun findLibraryFragment(fragment: Fragment): LibraryFragment? {
            return withContext(default) {
                fragment.findParent()
            }
        }

        private fun removeRootPaddingStart() {
            root.updatePadding(left = 0)
        }

    }

    private class W1240dpImpl(binding: FragmentArtistBinding): ArtistLayoutCompat(binding)

}