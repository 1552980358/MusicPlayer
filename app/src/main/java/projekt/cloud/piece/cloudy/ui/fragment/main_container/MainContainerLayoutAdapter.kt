package projekt.cloud.piece.cloudy.ui.fragment.main_container

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.CallSuper
import androidx.annotation.ColorInt
import androidx.annotation.MainThread
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.doOnLayout
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.google.android.material.card.MaterialCardView
import com.google.android.material.color.MaterialColors
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigationrail.NavigationRailView
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import kotlin.math.max
import projekt.cloud.piece.cloudy.base.BaseLayoutAdapter
import projekt.cloud.piece.cloudy.base.LayoutAdapterBuilder
import projekt.cloud.piece.cloudy.base.LayoutAdapterConstructor
import projekt.cloud.piece.cloudy.databinding.FragmentMainContainerBinding
import projekt.cloud.piece.cloudy.R
import projekt.cloud.piece.cloudy.R.dimen
import projekt.cloud.piece.cloudy.databinding.MainContainerMiniPlayerBinding
import projekt.cloud.piece.cloudy.util.PixelDensity
import projekt.cloud.piece.cloudy.util.PixelDensity.COMPAT
import projekt.cloud.piece.cloudy.util.PixelDensity.EXPANDED
import projekt.cloud.piece.cloudy.util.PixelDensity.MEDIUM
import projekt.cloud.piece.cloudy.util.CastUtil.cast
import projekt.cloud.piece.cloudy.util.GlideUtil.crossFade
import projekt.cloud.piece.cloudy.util.GlideUtil.roundCorners
import projekt.cloud.piece.cloudy.util.helper.MediaControllerHelper
import projekt.cloud.piece.cloudy.util.helper.NullableHelper.NullableHelperUtil.nullable
import projekt.cloud.piece.cloudy.util.helper.PlaybackControllerHelper

private typealias MainContainerLayoutAdapterBuilder =
    LayoutAdapterBuilder<FragmentMainContainerBinding, MainContainerLayoutAdapter>
private typealias MainContainerLayoutAdapterConstructor =
    LayoutAdapterConstructor<FragmentMainContainerBinding, MainContainerLayoutAdapter>

/**
 * [MainContainerLayoutAdapter]
 * @abstractExtends [BaseLayoutAdapter]
 *   @typeParam [FragmentMainContainerBinding]
 * @param binding [FragmentMainContainerBinding]
 *
 * @abstractImpl [MainContainerLayoutAdapter.LargeScreenCommonImpl]
 * @impl [MainContainerLayoutAdapter.CompatImpl], [MainContainerLayoutAdapter.W600dpImpl], [MainContainerLayoutAdapter.W600dpImpl]
 **/
abstract class MainContainerLayoutAdapter(
    binding: FragmentMainContainerBinding
): BaseLayoutAdapter<FragmentMainContainerBinding>(binding) {

    companion object {

        /**
         * [MainContainerLayoutAdapter.builder]
         * @type [LayoutAdapterBuilder]<[FragmentMainContainerBinding], [MainContainerLayoutAdapter]>
         **/
        val builder: MainContainerLayoutAdapterBuilder
            get() = ::builder

        /**
         * [MainContainerLayoutAdapter.builder]
         * @param pixelDensity [PixelDensity]
         * @return [LayoutAdapterConstructor]<[FragmentMainContainerBinding], [MainContainerLayoutAdapter]>
         **/
        private fun builder(pixelDensity: PixelDensity): MainContainerLayoutAdapterConstructor {
            return when (pixelDensity) {
                COMPAT -> ::CompatImpl
                MEDIUM -> ::W600dpImpl
                EXPANDED -> ::W1240dpImpl
            }
        }

    }

    protected val fragmentContainerView: FragmentContainerView
        get() = binding.fragmentContainerView

    protected val miniPlayer: MainContainerMiniPlayerBinding
        get() = binding.miniPlayer
    private val miniPlayerLeading: AppCompatImageView
        get() = miniPlayer.appCompatImageViewLeading
    private val miniPlayerRoot: ConstraintLayout
        get() = miniPlayer.constraintLayoutRoot
    private val playbackControl: AppCompatImageButton
        get() = miniPlayer.appCompatImageButtonPlayback

    protected val childNavController: NavController
        get() = fragmentContainerView.getFragment<NavHostFragment>()
            .navController

    protected val miniPlayerBehavior = nullable<BottomSheetBehavior<*>>()

    /**
     * [MainContainerLayoutAdapter.setupMiniPlayer]
     * @param viewModel [MainContainerViewModel]
     **/
    fun setupMiniPlayer(viewModel: MainContainerViewModel) {
        // Obtain and store instance
        val miniPlayerBehavior = miniPlayerBehavior valued getMiniPlayerBehavior()

        // Hide it when setup
        miniPlayerBehavior.state = STATE_HIDDEN

        // Update maxHeight of behavior
        miniPlayerRoot.doOnLayout {
            miniPlayerBehavior.maxHeight = miniPlayerRoot.height
        }

        onSetupMiniPlayer(viewModel, miniPlayerBehavior)
    }

    /**
     * [MainContainerLayoutAdapter.onSetupMiniPlayer]
     * @param viewModel [MainContainerViewModel]
     * @param miniPlayerBehavior [com.google.android.material.bottomsheet.BottomSheetBehavior]
     *
     * @impl [CompatImpl.onSetupMiniPlayer]
     **/
    protected open fun onSetupMiniPlayer(
        viewModel: MainContainerViewModel, miniPlayerBehavior: BottomSheetBehavior<*>
    ) = Unit

    /**
     * [MainContainerLayoutAdapter.getMiniPlayerBehavior]
     * @return [com.google.android.material.bottomsheet.BottomSheetBehavior]
     *
     * Get [com.google.android.material.bottomsheet.BottomSheetBehavior] instance
     * from [R.id.constraint_layout_root] of [R.id.mini_player]
     **/
    private fun getMiniPlayerBehavior(): BottomSheetBehavior<*> {
        return miniPlayerRoot.layoutParams.let { layoutParams ->
            layoutParams.cast<CoordinatorLayout.LayoutParams>()
                .behavior
                .cast()
        }
    }

    /**
     * [MainContainerLayoutAdapter.setupMiniPlayerCoverObserving]
     * @param fragment [androidx.fragment.app.Fragment]
     * @param viewModel [MainContainerViewModel]
     **/
    fun setupMiniPlayerCoverObserving(
        fragment: Fragment, viewModel: MainContainerViewModel
    ) {
        val context = fragment.requireContext()
        viewModel.metadata.observe(fragment.viewLifecycleOwner) { metadata ->
            Glide.with(fragment)
                .load(metadata.albumUri)
                .roundCorners(context, dimen.md_spec_round_radius)
                .apply(::setCrossFade)
                .into(miniPlayerLeading)
        }
    }

    @MainThread
    @CallSuper
    open fun setupMiniPlayerControl(
        mainControllerHelper: MediaControllerHelper
    ) {
        playbackControl.setOnClickListener {
            mainControllerHelper.requireMediaController { mediaController ->
                when {
                    mediaController.isPlaying -> { mediaController.pause() }
                    else -> { mediaController.play() }
                }
            }
        }
    }

    /**
     * [MainContainerLayoutAdapter.setCrossFade]
     * @param requestBuilder [com.bumptech.glide.RequestBuilder]<[android.graphics.drawable.Drawable]>
     *
     * Setting cross fade if required
     */
    private fun setCrossFade(requestBuilder: RequestBuilder<Drawable>) {
        miniPlayerLeading.let { miniPlayerLeading ->
            setCrossFade(
                requestBuilder,
                miniPlayerLeading.context,
                miniPlayerLeading.drawable
            )
        }
    }

    /**
     * [MainContainerLayoutAdapter.setCrossFade]
     * @param requestBuilder [com.bumptech.glide.RequestBuilder]<[android.graphics.drawable.Drawable]>
     * @param context [android.content.Context]
     * @param drawable [android.graphics.drawable.Drawable]
     *
     * Actual implement of [MainContainerLayoutAdapter.setCrossFade]
     */
    private fun setCrossFade(
        requestBuilder: RequestBuilder<Drawable>,
        context: Context,
        drawable: Drawable?
    ) {
        if (drawable != null) {
            requestBuilder.placeholder(drawable)
                .crossFade(context, R.integer.md_spec_transition_duration_400)
        }
    }

    /**
     * [MainContainerLayoutAdapter.setupDynamicLayout]
     * @param resources [android.content.res.Resources]
     *
     * Setup layout properties due to [R.id.bottom_navigation_view]'s height
     *
     * @impl [CompatImpl.setupDynamicLayout]
     **/
    open fun setupDynamicLayout(resources: Resources) = Unit

    /**
     * [MainContainerLayoutAdapter.setupNavigation]
     *
     * @impl [CompatImpl.setupNavigation], [W600dpImpl.setupNavigation], [W1240dpImpl.setupNavigation]
     **/
    open fun setupNavigation() = Unit

    /** Non-setup functions **/

    /**
     * [MainContainerLayoutAdapter.ensureMiniPlayVisible]
     *
     * Confirm that behavior state of [R.id.mini_player]
     * into [com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED]
     **/
    fun ensureMiniPlayVisible() {
        miniPlayerBehavior safely { miniPlayerBehavior ->
            if (miniPlayerBehavior.state == STATE_HIDDEN) {
                miniPlayerBehavior.state = STATE_COLLAPSED
            }
        }
    }

    /**
     * [MainContainerLayoutAdapter.playbackControllerHelper]
     * @type [PlaybackControllerHelper]
     **/
    private val playbackControllerHelper = PlaybackControllerHelper(
        ::getColorOnSurface,
        ::onBufferingState,
        ::onPlayingState, ::onPausedState,
        ::onPlayToPauseState, ::onPauseToPlayState
    )

    /**
     * [MainContainerLayoutAdapter.getColorOnSurface]
     * @param view [android.view.View]
     * @return [Int]
     *
     * Get color on surface attribute color int value
     **/
    @ColorInt
    private fun getColorOnSurface(view: View): Int {
        return MaterialColors.getColor(
            view, com.google.android.material.R.attr.colorOnSurface
        )
    }

    /**
     * [MainContainerLayoutAdapter.onBufferingState]
     * @param appCompatImageButton [androidx.appcompat.widget.AppCompatImageButton]
     * @param color [Int]
     *
     * Triggered when buffering state changed
     **/
    private fun onBufferingState(appCompatImageButton: AppCompatImageButton, @ColorInt color: Int) {
        appCompatImageButton.setImageDrawable(
            getBufferingAnimatedDrawable(
                appCompatImageButton.context, color
            )
        )
    }

    /**
     * [MainContainerLayoutAdapter.getBufferingAnimatedDrawable]
     * @param context [android.content.Context]
     * @param color [Int]
     * @return [android.graphics.drawable.Drawable]
     *
     * Get [com.google.android.material.progressindicator.IndeterminateDrawable] instance
     **/
    private fun getBufferingAnimatedDrawable(context: Context, color: Int): Drawable {
        return CircularProgressIndicatorSpec(context, null).let { spec ->
            // Set Color
            spec.indicatorColors = intArrayOf(color)

            // Create drawable
            return@let IndeterminateDrawable.createCircularDrawable(context, spec)
        }
    }

    /**
     * [MainContainerLayoutAdapter.onPlayingState]
     * @param appCompatImageButton [androidx.appcompat.widget.AppCompatImageButton]
     * @param color [Int]
     *
     * On playing
     **/
    private fun onPlayingState(appCompatImageButton: AppCompatImageButton, color: Int) {
        appCompatImageButton.setImageResource(R.drawable.ic_round_pause_24)
        appCompatImageButton.setColorFilter(color)
    }

    /**
     * [MainContainerLayoutAdapter.onPausedState]
     * @param appCompatImageButton [androidx.appcompat.widget.AppCompatImageButton]
     * @param color [Int]
     *
     * On paused
     **/
    private fun onPausedState(appCompatImageButton: AppCompatImageButton, color: Int) {
        appCompatImageButton.setImageResource(R.drawable.ic_round_play_24)
        appCompatImageButton.setColorFilter(color)
    }

    /**
     * [MainContainerLayoutAdapter.onPlayToPauseState]
     * @param appCompatImageButton [androidx.appcompat.widget.AppCompatImageButton]
     * @param color [Int]
     *
     * Switch from playing to paused
     **/
    private fun onPlayToPauseState(appCompatImageButton: AppCompatImageButton, color: Int) {
        appCompatImageButton.setImageResource(R.drawable.av_round_pause_to_play_24)
        appCompatImageButton.setColorFilter(color)
    }

    /**
     * [MainContainerLayoutAdapter.onPauseToPlayState]
     * @param appCompatImageButton [androidx.appcompat.widget.AppCompatImageButton]
     * @param color [Int]
     *
     * Switch from paused to playing
     **/
    private fun onPauseToPlayState(appCompatImageButton: AppCompatImageButton, color: Int) {
        appCompatImageButton.setImageResource(R.drawable.av_round_play_to_pause_24)
        appCompatImageButton.setColorFilter(color)
    }

    /**
     * [MainContainerLayoutAdapter.notifyBufferingStateChanged]
     * @param isBuffering [Boolean]
     **/
    fun notifyBufferingStateChanged(isBuffering: Boolean) {
        playbackControllerHelper.updateBufferingState(playbackControl, isBuffering)
    }

    /**
     * [MainContainerLayoutAdapter.notifyPlayingStateChanged]
     * @param isPlaying [Boolean]
     **/
    fun notifyPlayingStateChanged(isPlaying: Boolean) {
        playbackControllerHelper.updatePlayingState(playbackControl, isPlaying)
    }

    /**
     * [MainContainerLayoutAdapter.CompatImpl]
     * @extends [MainContainerLayoutAdapter]
     **/
    private class CompatImpl(binding: FragmentMainContainerBinding): MainContainerLayoutAdapter(binding) {

        /**
         * [MainContainerLayoutAdapter.CompatImpl.CompatImplHidingCallback]
         * @abstractExtends [com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback]
         *
         * This is implement for handling with hiding of [R.id.mini_player]
         **/
        private abstract class CompatImplHidingCallback private constructor(): BottomSheetCallback() {

            companion object CompatImplHidingCallbackUtil {

                /**
                 * [CompatImplHidingCallback.getImpl]
                 * @param viewModel [MainContainerViewModel]
                 * @param bottomNavigationView [com.google.android.material.bottomnavigation.BottomNavigationView]
                 * @param miniPlayerBehavior [com.google.android.material.bottomsheet.BottomSheetBehavior]
                 * @return [com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback]
                 *
                 * Return a implemented [CompatImplHidingCallback] instance
                 **/
                fun getImpl(
                    viewModel: MainContainerViewModel,
                    bottomNavigationView: BottomNavigationView,
                    miniPlayerBehavior: BottomSheetBehavior<*>
                ): BottomSheetCallback {
                    val bottomNavigationViewHeight by lazy { bottomNavigationView.height }
                    return object: CompatImplHidingCallback() {

                        /**
                         * [CompatImplHidingCallback.viewModel]
                         **/
                        override val viewModel: MainContainerViewModel
                            get() = viewModel

                        /**
                         * [CompatImplHidingCallback.bottomNavigationViewHeight]
                         **/
                        override val bottomNavigationViewHeight: Int
                            get() = bottomNavigationViewHeight

                        /**
                         * [CompatImplHidingCallback.miniPlayerBehavior]
                         **/
                        override val miniPlayerBehavior: BottomSheetBehavior<*>
                            get() = miniPlayerBehavior

                    }
                }

                /**
                 * [CompatImplHidingCallback.MINIMUM_MINI_PLAYER_HEIGHT]
                 * Limit the height calculated, prevent drawing below bottom margin,
                 * causing bottom item below [R.id.bottom_navigation_view]
                 **/
                private const val MINIMUM_MINI_PLAYER_HEIGHT = 0F


                /**
                 * [CompatImplHidingCallback.BOTTOM_SHEET_OFFSET_COLLAPSED]
                 * @type [Int]
                 **/
                private const val BOTTOM_SHEET_OFFSET_COLLAPSED = 0

            }

            /**
             * [CompatImplHidingCallback.viewModel]
             * @type [MainContainerViewModel]
             **/
            protected abstract val viewModel: MainContainerViewModel

            /**
             * [CompatImplHidingCallback.bottomNavigationViewHeight]
             * @type [Int]
             **/
            protected abstract val bottomNavigationViewHeight: Int

            /**
             * [CompatImplHidingCallback.miniPlayerBehavior]
             * @type [com.google.android.material.bottomsheet.BottomSheetBehavior]
             **/
            protected abstract val miniPlayerBehavior: BottomSheetBehavior<*>

            /**
             * [com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback.onStateChanged]
             * @param bottomSheet [android.view.View]
             * @param newState [Int]
             **/
            override fun onStateChanged(bottomSheet: View, newState: Int) = Unit

            /**
             * [com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback.onSlide]
             * @param bottomSheet [android.view.View]
             * @param slideOffset [Float]
             **/
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset <= BOTTOM_SHEET_OFFSET_COLLAPSED) {
                    viewModel.updateMiniPlayerHidingHeight(
                        getMiniPlayerHeight(
                            miniPlayerBehavior.peekHeight, slideOffset, bottomNavigationViewHeight
                        )
                    )
                }
            }

            /**
             * [CompatImplHidingCallback.getMiniPlayerHeight]
             * @param peekHeight [Int]
             * @param slideOffset [Float]
             * @param bottomNavigationViewHeight [Int]
             * @return [Int]
             *
             * Calculate mini player's visible height, and check if result available
             **/
            private fun getMiniPlayerHeight(
                peekHeight: Int, slideOffset: Float, bottomNavigationViewHeight: Int
            ): Int {
                return max(
                    MINIMUM_MINI_PLAYER_HEIGHT,
                    calculateMiniPlayerHeight(peekHeight, slideOffset, bottomNavigationViewHeight)
                ).toInt()
            }

            /**
             * [MainContainerLayoutAdapter.CompatImpl.CompatImplHidingCallback.calculateMiniPlayerHeight]
             * @param peekHeight [Int]
             * @param slideOffset [Float]
             * @param bottomNavigationViewHeight [Int]
             * @return [Float]
             *
             * Calculate mini player's downward distance
             **/
            private fun calculateMiniPlayerHeight(
                peekHeight: Int, slideOffset: Float, bottomNavigationViewHeight: Int
            ): Float {
                /**
                 * Collapsed => Hidden: [slideOffset] ranged from 0 to -1, with reference to [peekHeight]
                 * where
                 *   1 + (-[slideOffset]) = offset of peek height above bottom of [androidx.coordinatorlayout.widget.CoordinatorLayout];
                 * where
                 *   1 - [slideOffset] * [peekHeight] =
                 *   height above bottom of [androidx.coordinatorlayout.widget.CoordinatorLayout];
                 * where
                 *   height above bottom of [androidx.coordinatorlayout.widget.CoordinatorLayout] - [bottomNavigationViewHeight] =
                 *   visible height of [miniPlayer].
                 * Thus,
                 *   Visible height of [miniPlayer] =
                 *   [peekHeight] * (1 - [slideOffset]) - [bottomNavigationViewHeight]
                 **/
                return (
                    peekHeight * slideOffset.inc()  // slideOffset + 1 => 1 - abs(slideOffset), where slideOffset <= 0
                        - bottomNavigationViewHeight
                )
            }

        }

        /**
         * [MainContainerLayoutAdapter.CompatImpl.CompatImplExpandingCallback]
         * @abstractExtends [com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback]
         *
         * This is implement for handling with expanding of [R.id.mini_player]
         **/
        private abstract class CompatImplExpandingCallback private constructor(): BottomSheetCallback() {

            companion object CompatImplExpandingCallbackUtil {

                fun getImpl(
                    viewModel: MainContainerViewModel,
                    miniPlayerBehavior: BottomSheetBehavior<*>
                ): BottomSheetCallback {
                    return object: CompatImplExpandingCallback() {

                        /**
                         * [CompatImplExpandingCallback.viewModel]
                         **/
                        override val viewModel: MainContainerViewModel
                            get() = viewModel

                        /**
                         * [CompatImplExpandingCallback.miniPlayerBehavior]
                         **/
                        override val miniPlayerBehavior: BottomSheetBehavior<*>
                            get() = miniPlayerBehavior

                    }
                }

                /**
                 * [CompatImplExpandingCallback.BOTTOM_SHEET_OFFSET_COLLAPSED]
                 * @type [Int]
                 **/
                private const val BOTTOM_SHEET_OFFSET_COLLAPSED = 0

            }

            /**
             * [CompatImplExpandingCallback.viewModel]
             * @type [MainContainerViewModel]
             **/
            protected abstract val viewModel: MainContainerViewModel

            /**
             * [CompatImplExpandingCallback.miniPlayerBehavior]
             * @type [com.google.android.material.bottomsheet.BottomSheetBehavior]
             **/
            protected abstract val miniPlayerBehavior: BottomSheetBehavior<*>

            /**
             * [com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback.onStateChanged]
             * @param bottomSheet [android.view.View]
             * @param newState [Int]
             **/
            override fun onStateChanged(bottomSheet: View, newState: Int) = Unit

            /**
             * [com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback.onSlide]
             * @param bottomSheet [android.view.View]
             * @param slideOffset [Float]
             **/
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset >= BOTTOM_SHEET_OFFSET_COLLAPSED) {
                    viewModel.updateMiniPlayerExpandingHeight(
                        calculateExpandedDistance(
                            miniPlayerBehavior.maxHeight,
                            miniPlayerBehavior.peekHeight,
                            slideOffset
                        )
                    )
                }
            }

            /**
             * [MainContainerLayoutAdapter.CompatImpl.CompatImplExpandingCallback.calculateExpandedDistance]
             * @param maxHeight [Int]
             * @param peekHeight [Int]
             * @param slideOffset [Float]
             * @return [Float]
             *
             * Calculate mini player's upward distance
             **/
            private fun calculateExpandedDistance(
                maxHeight: Int, peekHeight: Int, slideOffset: Float
            ): Int {
                /**
                 * Collapsed => Expanded: [slideOffset] ranged from 0 to 1, with reference [maxHeight] - [peekHeight]
                 * where
                 *   [slideOffset] = offset slided up content originally below bottom of [androidx.coordinatorlayout.widget.CoordinatorLayout];
                 * where
                 *   [maxHeight] - [peekHeight] = height of content below bottom of [androidx.coordinatorlayout.widget.CoordinatorLayout].
                 * Thus,
                 *   Distance = slided up height of content originally below bottom of [androidx.coordinatorlayout.widget.CoordinatorLayout] =
                 *   ([maxHeight] - [peekHeight]) * [slideOffset]
                 **/
                return ((maxHeight - peekHeight) * slideOffset).toInt()
            }

        }

        private val bottomNavigationView: BottomNavigationView
            get() = binding.bottomNavigationView!!

        private val miniPlayerContainer: MaterialCardView
            get() = miniPlayer.materialCardViewMiniPlayer

        /**
         * [MainContainerLayoutAdapter.setupDynamicLayout]
         **/
        override fun setupDynamicLayout(resources: Resources) {
            // In-function local instance, will be called future
            val fragmentContainerView = fragmentContainerView

            val miniPlayerHeight by lazy { miniPlayerContainer.height }

            bottomNavigationView.addOnLayoutChangeListener { _, _, top, _, bottom, _, _, _, _ ->
                (bottom - top).let { bottomNavigationViewHeight ->
                    if (fragmentContainerView.marginBottom < bottomNavigationViewHeight) {
                        fragmentContainerView.updateLayoutParams<MarginLayoutParams> {
                            setMargins(marginStart, topMargin, marginEnd, bottomNavigationViewHeight)
                        }
                    }

                    miniPlayerBehavior safely { bottomSheetBehavior ->
                        (miniPlayerHeight + bottomNavigationViewHeight).let { peekHeight ->
                            if (bottomSheetBehavior.peekHeight < peekHeight) {
                                bottomSheetBehavior.peekHeight = peekHeight
                            }
                        }
                    }
                }
            }
        }

        /**
         * [MainContainerLayoutAdapter.onSetupMiniPlayer]
         * @param viewModel [MainContainerViewModel]
         * @param miniPlayerBehavior [com.google.android.material.bottomsheet.BottomSheetBehavior]
         **/
        override fun onSetupMiniPlayer(
            viewModel: MainContainerViewModel,
            miniPlayerBehavior: BottomSheetBehavior<*>
        ) {
            miniPlayerBehavior.addBottomSheetCallback(
                // Sliding down
                CompatImplHidingCallback.getImpl(
                    viewModel, bottomNavigationView, miniPlayerBehavior
                )
            )

            miniPlayerBehavior.addBottomSheetCallback(
                // Sliding up
                CompatImplExpandingCallback.getImpl(viewModel, miniPlayerBehavior)
            )
        }

        /**
         * [MainContainerLayoutAdapter.setupNavigation]
         **/
        override fun setupNavigation() {
            bottomNavigationView.setupWithNavController(childNavController)
        }

    }

    /**
     * [MainContainerLayoutAdapter.LargeScreenCommonImpl]
     * @abstractExtends [MainContainerLayoutAdapter]
     **/
    private abstract class LargeScreenCommonImpl(
        binding: FragmentMainContainerBinding
    ): MainContainerLayoutAdapter(binding) {

        /**
         * [MainContainerLayoutAdapter.LargeScreenCommonImpl.LargeScreenCommonHidingCallback]
         * @abstractExtends [com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback]
         *
         * This is implement for handling with hiding of [R.id.mini_player]
         **/
        private abstract class LargeScreenCommonHidingCallback private constructor(): BottomSheetCallback() {

            companion object LargeScreenCommonHidingCallbackUtil {

                /**
                 * [LargeScreenCommonHidingCallback.getImpl]
                 * @param viewModel [MainContainerViewModel]
                 * @param miniPlayerBehavior [com.google.android.material.bottomsheet.BottomSheetBehavior]
                 * @return [LargeScreenCommonHidingCallback]
                 *
                 * Return an implemented [LargeScreenCommonHidingCallback] instance
                 **/
                fun getImpl(
                    viewModel: MainContainerViewModel,
                    miniPlayerBehavior: BottomSheetBehavior<*>
                ): LargeScreenCommonHidingCallback {
                    return object: LargeScreenCommonHidingCallback() {

                        /**
                         * [LargeScreenCommonHidingCallback.viewModel]
                         **/
                        override val viewModel: MainContainerViewModel
                            get() = viewModel

                        /**
                         * [LargeScreenCommonHidingCallback.miniPlayerBehavior]
                         **/
                        override val miniPlayerBehavior: BottomSheetBehavior<*>
                            get() = miniPlayerBehavior

                    }
                }

                /**
                 * [LargeScreenCommonHidingCallback.BOTTOM_SHEET_OFFSET_COLLAPSED]
                 * @type [Int]
                 **/
                private const val BOTTOM_SHEET_OFFSET_COLLAPSED = 0

            }

            /**
             * [LargeScreenCommonHidingCallback.miniPlayerBehavior]
             * @type [MainContainerViewModel]
             **/
            protected abstract val viewModel: MainContainerViewModel

            /**
             * [LargeScreenCommonHidingCallback.miniPlayerBehavior]
             * @type [com.google.android.material.bottomsheet.BottomSheetBehavior]
             **/
            protected abstract val miniPlayerBehavior: BottomSheetBehavior<*>

            /**
             * [com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback.onStateChanged]
             * @param bottomSheet [android.view.View]
             * @param newState [Int]
             **/
            override fun onStateChanged(bottomSheet: View, newState: Int) = Unit

            /**
             * [com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback.onSlide]
             * @param bottomSheet [android.view.View]
             * @param slideOffset [Float]
             **/
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset <= BOTTOM_SHEET_OFFSET_COLLAPSED) {
                    viewModel.updateMiniPlayerHidingHeight(
                        getSlidedHeight(miniPlayerBehavior.peekHeight, slideOffset)
                    )
                }
            }

            /**
             * [LargeScreenCommonHidingCallback.getSlidedHeight]
             * @param peekHeight [Int]
             * @param slideOffset [Float]
             * @return [Int]
             **/
            private fun getSlidedHeight(peekHeight: Int, slideOffset: Float): Int {
                return (peekHeight * slideOffset.inc()).toInt()
            }

        }

        /**
         * [MainContainerLayoutAdapter.onSetupMiniPlayer]
         * @param viewModel [MainContainerViewModel]
         * @param miniPlayerBehavior [com.google.android.material.bottomsheet.BottomSheetBehavior]
         **/
        override fun onSetupMiniPlayer(viewModel: MainContainerViewModel, miniPlayerBehavior: BottomSheetBehavior<*>) {
            miniPlayerBehavior.addBottomSheetCallback(
                LargeScreenCommonHidingCallback.getImpl(viewModel, miniPlayerBehavior)
            )
        }

    }

    /**
     * [MainContainerLayoutAdapter.W600dpImpl]
     * @extends [MainContainerLayoutAdapter.LargeScreenCommonImpl]
     **/
    private class W600dpImpl(binding: FragmentMainContainerBinding): LargeScreenCommonImpl(binding) {

        /**
         * [W600dpImpl.navigationRailView]
         * @type [com.google.android.material.navigationrail.NavigationRailView]
         **/
        private val navigationRailView: NavigationRailView
            get() = binding.navigationRailView!!

        /**
         * [MainContainerLayoutAdapter.setupNavigation]
         **/
        override fun setupNavigation() {
            navigationRailView.setupWithNavController(childNavController)
        }

    }

    /**
     * [MainContainerLayoutAdapter.W1240dpImpl]
     * @extends [MainContainerLayoutAdapter.LargeScreenCommonImpl]
     **/
    private class W1240dpImpl(binding: FragmentMainContainerBinding): LargeScreenCommonImpl(binding) {

        /**
         * [W1240dpImpl.navigationView]
         * @type [com.google.android.material.navigation.NavigationView]
         **/
        private val navigationView: NavigationView
            get() = binding.navigationView!!

        /**
         * [MainContainerLayoutAdapter.setupNavigation]
         **/
        override fun setupNavigation() {
            navigationView.setupWithNavController(childNavController)
        }

    }

}