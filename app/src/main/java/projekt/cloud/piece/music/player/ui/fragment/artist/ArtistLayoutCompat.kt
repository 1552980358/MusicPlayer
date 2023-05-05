package projekt.cloud.piece.music.player.ui.fragment.artist

import androidx.annotation.UiThread
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.Transition.TransitionListener
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import kotlin.math.abs
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseLayoutCompat
import projekt.cloud.piece.music.player.base.BaseMultiDensityFragment
import projekt.cloud.piece.music.player.databinding.ArtistAvatarBinding
import projekt.cloud.piece.music.player.databinding.FragmentArtistBinding
import projekt.cloud.piece.music.player.storage.runtime.databaseView.ArtistView
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity
import projekt.cloud.piece.music.player.util.AutoExpandableAppBarLayoutBehavior
import projekt.cloud.piece.music.player.util.KotlinUtil.ifNotNull
import projekt.cloud.piece.music.player.util.KotlinUtil.tryTo
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

    open fun setupNavigation(fragment: Fragment) = Unit

    @UiThread
    open fun setupArtistMetadata(fragment: Fragment, artistView: ArtistView) = Unit

    fun setRecyclerViewAdapter(
        fragment: Fragment, audioList: List<AudioMetadataEntity>, onItemClick: (String) -> Unit
    ) {
        recyclerView.adapter = ArtistRecyclerAdapter(fragment, audioList, onItemClick)
    }

    private class CompatImpl(binding: FragmentArtistBinding): ArtistLayoutCompat(binding) {

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

        private var _isAppBarContentExpanded = MutableLiveData<Boolean>()
        private val isAppBarContentExpanded: LiveData<Boolean>
            get() = _isAppBarContentExpanded

        override fun setupCollapsingAppBar(fragment: Fragment) {
            val originalSet = ConstraintSet().apply {
                clone(avatarContainer)
            }
            val collapsedSet = ConstraintSet().apply {
                clone(fragment.requireContext(), R.layout.artist_avatar_collapsed)
            }

            var transitionSet: TransitionSet? = null
            var currentSet = originalSet
            _isAppBarContentExpanded.value = currentSet == originalSet

            /**
             * Should not directly set as `appBarLayout.totalScrollRange / 2`
             * because [setupCollapsingAppBar] is called
             * during [BaseMultiDensityFragment.onSetupLayoutCompat], i.e. [Fragment.onViewCreated],
             * which does not having [Fragment.onResume] be called, i.e. no height known.
             * So, appBarLayout.totalScrollRange / 2 will be 0 if not be assigned by `lazy { ... }`
             **/
            val offsetLimit by lazy { appBarLayout.totalScrollRange / 2 }
            appBarLayout.addOnOffsetChangedListener { _, verticalOffset ->
                transitionSet.ifNotNull {
                    return@addOnOffsetChangedListener
                }

                when (currentSet) {
                    collapsedSet -> {
                        originalSet.takeIf {
                            abs(verticalOffset) < offsetLimit
                        }
                    }

                    else -> {
                        collapsedSet.takeIf {
                            abs(verticalOffset) > offsetLimit
                        }
                    }
                }?.let { constraintSet ->
                    currentSet = constraintSet
                    _isAppBarContentExpanded.value = currentSet == originalSet
                    transitionSet = createTransitionSet {
                        transitionSet = null
                    }
                    TransitionManager.beginDelayedTransition(avatarContainer, transitionSet)
                    constraintSet.applyTo(avatarContainer)
                }

            }

            appBarLayout.layoutParams.tryTo<CoordinatorLayout.LayoutParams>()
                ?.behavior
                .tryTo<AutoExpandableAppBarLayoutBehavior>()
                ?.setObserver(fragment, isAppBarContentExpanded)
        }

        private fun createTransitionSet(doOnEnd: () -> Unit): TransitionSet {
            return AutoTransition().addListener(
                object : TransitionListener {
                    override fun onTransitionStart(transition: Transition) = Unit
                    override fun onTransitionEnd(transition: Transition) = doOnEnd.invoke()
                    override fun onTransitionCancel(transition: Transition) = Unit
                    override fun onTransitionPause(transition: Transition) = Unit
                    override fun onTransitionResume(transition: Transition) = Unit
                }
            )
        }

        override fun setupNavigation(fragment: Fragment) {
            toolbar.setNavigationOnClickListener {
                fragment.requireActivity()
                    .onBackPressedDispatcher
                    .onBackPressed()
            }
        }

        override fun setupArtistMetadata(fragment: Fragment, artistView: ArtistView) {
            val artistName = artistView.name
            val metadataStr = fragment.getString(
                R.string.artist_metadata_str, artistView.songCount, artistView.duration.durationStr
            )

            avatar.name = artistName
            avatar.metadataStr = metadataStr

            val fragmentLabel = fragment.getString(R.string.artist_title)
            isAppBarContentExpanded.observe(fragment.viewLifecycleOwner) { isAppBarExpanded ->
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

    }

    private class W600dpImpl(binding: FragmentArtistBinding): ArtistLayoutCompat(binding)

    private class W1240dpImpl(binding: FragmentArtistBinding): ArtistLayoutCompat(binding)

}