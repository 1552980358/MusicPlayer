package projekt.cloud.piece.music.player.ui.fragment.mainHost

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.BitmapDrawable
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ART
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ARTIST
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ART_URI
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaControllerCompat.TransportControls
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.GONE
import androidx.constraintlayout.widget.ConstraintSet.VISIBLE
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.ui.setupWithNavController
import androidx.transition.TransitionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.google.android.material.elevation.SurfaceColors
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigationrail.NavigationRailView
import kotlin.reflect.KClass
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseLayoutCompat
import projekt.cloud.piece.music.player.databinding.FragmentMainHostBinding
import projekt.cloud.piece.music.player.databinding.MainHostPlaybackBarBinding
import projekt.cloud.piece.music.player.ui.fragment.home.HomeViewModel

private interface MainHostInterface {

    fun setupColor(context: Context) = Unit

    fun setupNavigation(navController: NavController) = Unit

    fun setupNavigationItems(fragment: Fragment, navController: NavController) = Unit

    fun setupPlaybackBar(fragment: Fragment, navController: NavController) = Unit

    fun setupPlaybackControl(transportControls: TransportControls)

}

open class MainHostLayoutCompat: BaseLayoutCompat<FragmentMainHostBinding>, MainHostInterface {

    private companion object {
        const val STATE_UNKNOWN = -1
    }

    constructor(): super(null)
    constructor(binding: FragmentMainHostBinding): super(binding)

    override val compatImpl: KClass<*>
        get() = CompatImpl::class
    override val w600dpImpl: KClass<*>
        get() = W600dpImpl::class
    override val w1240dpImpl: KClass<*>
        get() = W1240dpImpl::class

    /**
     * Shared views
     **/
    protected val constraintLayout: ConstraintLayout
        get() = binding.constraintLayout
    protected val playbackBar: MainHostPlaybackBarBinding
        get() = binding.mainHostPlaybackBar
    private val playbackControl: AppCompatImageButton
        get() = playbackBar.appCompatImageButtonPlayControl
    private val cover: AppCompatImageView
        get() = playbackBar.shapeableImageViewImage

    override fun setupPlaybackControl(transportControls: TransportControls) {
        playbackControl.setOnClickListener {
            when (lastPlaybackState) {
                STATE_PLAYING -> { transportControls.pause() }
                STATE_PAUSED -> { transportControls.play() }
            }
        }
    }

    fun setupSwitchingToPlayer(mediaControllerCompat: MediaControllerCompat, navController: NavController) {
        cover.setOnClickListener {
            mediaControllerCompat.metadata?.let { mediaMetadataCompat ->
                navController.navigate(
                    MainHostFragmentDirections.toPlayer(
                        mediaMetadataCompat.getString(METADATA_KEY_TITLE),
                        mediaMetadataCompat.getString(METADATA_KEY_ARTIST),
                        mediaMetadataCompat.getString(METADATA_KEY_ALBUM),
                        mediaMetadataCompat.getLong(METADATA_KEY_DURATION),
                        mediaMetadataCompat.getString(METADATA_KEY_ART_URI)
                    ),
                    FragmentNavigatorExtras(cover to cover.transitionName)
                )
            }
        }
    }

    fun notifyMetadataChanged(context: Context, metadata: MediaMetadataCompat) {
        playbackBar.title = metadata.getString(METADATA_KEY_TITLE)
        playbackBar.artist = metadata.getString(METADATA_KEY_ARTIST)
        playbackBar.cover = BitmapDrawable(
            context.resources, metadata.getBitmap(METADATA_KEY_ART)
        )
    }

    fun notifyPlaybackStateChanged(context: Context, @PlaybackStateCompat.State state: Int) {
        onPlaybackStateChanged(context, state, true)
    }

    private var lastPlaybackState = STATE_UNKNOWN
        @Synchronized
        private set

    @Synchronized
    protected open fun onPlaybackStateChanged(
        context: Context, @PlaybackStateCompat.State state: Int, requireAnimation: Boolean
    ) {
        ContextCompat.getDrawable(
            context, getPlaybackDrawableId(state, lastPlaybackState)
        )?.let { drawable ->
            playbackControl.setImageDrawable(drawable)
            if (drawable is AnimatedVectorDrawable) {
                drawable.start()
            }
        }
    }

    private fun getPlaybackDrawableId(state: Int, lastState: Int): Int {
        lastPlaybackState = state
        return when (lastState) {
            STATE_UNKNOWN -> when (state) {
                STATE_PLAYING -> { R.drawable.ic_round_pause_24 }
                STATE_BUFFERING -> { R.drawable.av_round_play_to_pause_24 }
                // STATE_PAUSED -> { R.drawable.ic_round_play_24 }
                else -> { R.drawable.ic_round_play_24 }
            }
            STATE_NONE -> when (state) {
                STATE_BUFFERING, STATE_PLAYING -> { R.drawable.ic_round_pause_24 }
                else -> { R.drawable.ic_round_play_24 }
            }
            STATE_PLAYING -> when (state) {
                // STATE_BUFFERING -> { R.drawable.ic_round_pause_24 }
                STATE_PAUSED -> { R.drawable.av_round_pause_to_play_24 }
                // Keep unchanged
                else -> { R.drawable.ic_round_pause_24 }
            }
            STATE_PAUSED -> when (state) {
                STATE_BUFFERING, STATE_PLAYING -> { R.drawable.av_round_play_to_pause_24 }
                else -> { R.drawable.ic_round_play_24 }
            }
            STATE_BUFFERING -> when (state) {
                // STATE_PLAYING -> { R.drawable.ic_round_pause_24 }
                STATE_PAUSED -> { R.drawable.av_round_pause_to_play_24 }
                // Keep unchanged
                else -> { R.drawable.ic_round_pause_24 }
            }
            else -> R.drawable.ic_round_pause_24
        }
    }

    fun recoverPlaybackBar(context: Context, mediaControllerCompat: MediaControllerCompat) {
        onPlaybackStateChanged(
            context, mediaControllerCompat.playbackState.state, false
        )
        notifyMetadataChanged(context, mediaControllerCompat.metadata)
    }

    private class CompatImpl(binding: FragmentMainHostBinding): MainHostLayoutCompat(binding) {

        private val bottomNavigationView: BottomNavigationView
            get() = binding.bottomNavigationView!!
        private val bottomSheet: ConstraintLayout
            get() = playbackBar.constraintLayoutPlaybackBar

        private var _bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>? = null
        private val bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
            get() = _bottomSheetBehavior!!

        override fun setupNavigation(navController: NavController) {
            bottomNavigationView.setupWithNavController(navController)
        }

        override fun setupNavigationItems(fragment: Fragment, navController: NavController) {
            val homeViewModel: HomeViewModel by fragment.viewModels(
                { navController.getViewModelStoreOwner(R.id.nav_graph_main_host) }
            )

            bottomNavigationView.setOnItemReselectedListener {
                if (navController.currentDestination?.id == R.id.home) {
                    if (!homeViewModel.isOnTop) {
                        homeViewModel.scrollToTop()
                    }
                }
            }
        }

        override fun setupPlaybackBar(fragment: Fragment, navController: NavController) {
            val mainHostViewModel: MainHostViewModel by fragment.viewModels(
                { navController.getViewModelStoreOwner(R.id.nav_graph_main_host) }
            )

            bottomSheet.setBackgroundColor(
                SurfaceColors.SURFACE_2.getColor(fragment.requireContext())
            )
            @Suppress("UNCHECKED_CAST")
            _bottomSheetBehavior = (bottomSheet.layoutParams as CoordinatorLayout.LayoutParams)
                .behavior as BottomSheetBehavior<ConstraintLayout>

            with(bottomSheetBehavior) {
                maxHeight = fragment.resources
                    .getDimensionPixelSize(R.dimen.main_host_playback_bar_height)

                isHideable = true

                isDraggable = true

                state = STATE_HIDDEN

                addBottomSheetCallback(
                    object: BottomSheetCallback() {
                        override fun onStateChanged(bottomSheet: View, newState: Int) {
                            when (newState) {
                                STATE_HIDDEN -> {
                                    val mediaControllerCompat =
                                        MediaControllerCompat.getMediaController(fragment.requireActivity())
                                    mediaControllerCompat.transportControls
                                        .stop()
                                    mainHostViewModel.setBottomMargin(0)
                                }
                                STATE_COLLAPSED -> {
                                    mainHostViewModel.setBottomMargin(bottomSheet.height)
                                }
                                else -> {
                                    // Not Implemented
                                }
                            }
                        }
                        override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
                    }
                )
            }
        }

        @Synchronized
        override fun onPlaybackStateChanged(
            context: Context, @PlaybackStateCompat.State state: Int, requireAnimation: Boolean
        ) {
            when (state) {
                STATE_PLAYING -> {
                    if (bottomSheetBehavior.state != STATE_COLLAPSED) {
                        bottomSheetBehavior.state = STATE_COLLAPSED
                    }
                }
                STATE_PAUSED -> {
                    if (bottomSheetBehavior.state != STATE_COLLAPSED) {
                        bottomSheetBehavior.state = STATE_COLLAPSED
                    }
                }
                STATE_NONE -> {
                    if (bottomSheetBehavior.state != STATE_HIDDEN) {
                        bottomSheetBehavior.state = STATE_HIDDEN
                    }
                }
                else -> {
                    // Not Implemented
                }
            }
            super.onPlaybackStateChanged(context, state, requireAnimation)
        }

        override fun onRecycleInstance() {
            _bottomSheetBehavior = null
        }

    }

    private class W600dpImpl(binding: FragmentMainHostBinding): MainHostLayoutCompat(binding) {

        private val navigationRailView: NavigationRailView
            get() = binding.navigationRailView!!
        private val next: AppCompatImageButton
            get() = playbackBar.appCompatImageButtonNext!!

        val originSet = ConstraintSet().apply {
            clone(constraintLayout)
            setVisibility(R.id.main_host_playback_bar, GONE)
        }
        val playingSet = ConstraintSet().apply {
            clone(constraintLayout)
            setVisibility(R.id.main_host_playback_bar, VISIBLE)
        }

        override val requireWindowInsets: Boolean
            get() = true

        override fun onSetupRequireWindowInsets() = { insets: Rect ->
            constraintLayout.updatePadding(bottom = insets.bottom)
        }

        override fun setupColor(context: Context) {
            val backgroundColor = SurfaceColors.SURFACE_2.getColor(context)
            navigationRailView.setBackgroundColor(backgroundColor)
            constraintLayout.setBackgroundColor(backgroundColor)
        }

        override fun setupNavigation(navController: NavController) {
            navigationRailView.setupWithNavController(navController)
        }

        override fun setupPlaybackControl(transportControls: TransportControls) {
            super.setupPlaybackControl(transportControls)
            next.setOnClickListener { transportControls.skipToNext() }
        }

        private var isPlaybackBarShown = false
            @Synchronized set

        @Synchronized
        override fun onPlaybackStateChanged(
            context: Context, @PlaybackStateCompat.State state: Int, requireAnimation: Boolean
        ) {
            val constraintSet = when (state) {
                STATE_BUFFERING, STATE_PLAYING, STATE_PAUSED -> when {
                    isPlaybackBarShown -> { null }
                    else -> {
                        isPlaybackBarShown = true
                        playingSet
                    }
                }
                else -> when {
                    !isPlaybackBarShown -> { null }
                    else -> {
                        isPlaybackBarShown = false
                        originSet
                    }
                }
            }
            constraintSet?.let {
                if (requireAnimation) {
                    TransitionManager.beginDelayedTransition(constraintLayout)
                }
                constraintSet.applyTo(constraintLayout)
            }
            super.onPlaybackStateChanged(context, state, requireAnimation)
        }

    }

    private class W1240dpImpl(binding: FragmentMainHostBinding): MainHostLayoutCompat(binding) {

        private val navigationView: NavigationView
            get() = binding.navigationView!!
        private val prev: AppCompatImageButton
            get() = playbackBar.appCompatImageButtonPrev!!
        private val next: AppCompatImageButton
            get() = playbackBar.appCompatImageButtonNext!!

        val originSet = ConstraintSet().apply {
            clone(constraintLayout)
            setVisibility(R.id.main_host_playback_bar, GONE)
        }
        val playingSet = ConstraintSet().apply {
            clone(constraintLayout)
            setVisibility(R.id.main_host_playback_bar, VISIBLE)
        }

        override val requireWindowInsets: Boolean
            get() = true

        override fun onSetupRequireWindowInsets() = { insets: Rect ->
            constraintLayout.updatePadding(bottom = insets.bottom)
        }

        override fun setupColor(context: Context) {
            val backgroundColor = SurfaceColors.SURFACE_2.getColor(context)
            constraintLayout.setBackgroundColor(backgroundColor)
            navigationView.setBackgroundColor(backgroundColor)
        }

        override fun setupNavigation(navController: NavController) {
            navigationView.setupWithNavController(navController)
        }

        override fun setupPlaybackControl(transportControls: TransportControls) {
            super.setupPlaybackControl(transportControls)
            prev.setOnClickListener { transportControls.skipToPrevious() }
            next.setOnClickListener { transportControls.skipToNext() }
        }

        private var isPlaybackBarShown = false
            @Synchronized set

        @Synchronized
        override fun onPlaybackStateChanged(
            context: Context, @PlaybackStateCompat.State state: Int, requireAnimation: Boolean
        ) {
            val constraintSet = when (state) {
                STATE_BUFFERING, STATE_PLAYING, STATE_PAUSED -> when {
                    isPlaybackBarShown -> { null }
                    else -> {
                        isPlaybackBarShown = true
                        playingSet
                    }
                }
                else -> when {
                    !isPlaybackBarShown -> { null }
                    else -> {
                        isPlaybackBarShown = false
                        originSet
                    }
                }
            }
            constraintSet?.let {
                if (requireAnimation) {
                    TransitionManager.beginDelayedTransition(constraintLayout)
                }
                constraintSet.applyTo(constraintLayout)
            }
            super.onPlaybackStateChanged(context, state, requireAnimation)
        }

    }

}