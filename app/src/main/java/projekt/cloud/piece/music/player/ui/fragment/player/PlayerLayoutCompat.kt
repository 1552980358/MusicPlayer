package projekt.cloud.piece.music.player.ui.fragment.player

import android.graphics.Rect
import android.graphics.drawable.AnimatedVectorDrawable
import android.net.Uri
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaControllerCompat.TransportControls
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_ALL
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_NONE
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_ONE
import android.support.v4.media.session.PlaybackStateCompat.RepeatMode
import android.support.v4.media.session.PlaybackStateCompat.ShuffleMode
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_ALL
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_NONE
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import android.view.View.OnClickListener
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.core.view.doOnAttach
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.google.android.material.circularreveal.CircularRevealCompat
import com.google.android.material.circularreveal.CircularRevealWidget.RevealInfo
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.slider.Slider
import com.google.android.material.slider.Slider.OnSliderTouchListener
import kotlin.math.max
import kotlinx.coroutines.withContext
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseLayoutCompat
import projekt.cloud.piece.music.player.base.interfaces.WindowInsetsInterface
import projekt.cloud.piece.music.player.databinding.FragmentPlayerBinding
import projekt.cloud.piece.music.player.util.CoroutineUtil.default
import projekt.cloud.piece.music.player.util.CoroutineUtil.main
import projekt.cloud.piece.music.player.util.PlaybackStateManager
import projekt.cloud.piece.music.player.util.ResourceUtil.getLong
import projekt.cloud.piece.music.player.util.ScreenDensity
import projekt.cloud.piece.music.player.util.ScreenDensity.COMPACT
import projekt.cloud.piece.music.player.util.ScreenDensity.EXPANDED
import projekt.cloud.piece.music.player.util.ScreenDensity.MEDIUM
import projekt.cloud.piece.music.player.util.TimeUtil.durationStr
import projekt.cloud.piece.music.player.util.TimeUtil.timeStr

abstract class PlayerLayoutCompat(
    binding: FragmentPlayerBinding
): BaseLayoutCompat<FragmentPlayerBinding>(binding), WindowInsetsInterface {

    companion object LibraryLayoutCompatUtil {

        fun inflate(screenDensity: ScreenDensity, binding: FragmentPlayerBinding): PlayerLayoutCompat {
            return when (screenDensity) {
                COMPACT -> CompatImpl(binding)
                MEDIUM -> W600dpImpl(binding)
                EXPANDED -> W1240dpImpl(binding)
            }
        }

        private const val REPEAT_DISABLED_ALPHA = 0.5F
        private const val SHUFFLE_DISABLED_ALPHA = 0.5F

    }

    private val constantRoot: ConstraintLayout
        get() = binding.constraintLayoutRoot
    private val position: Slider
        get() = binding.sliderPosition
    private val cover: ShapeableImageView
        get() = binding.shapeableImageViewImageCover
    private val playbackControl: FloatingActionButton
        get() = binding.floatingActionButtonPlaybackControl
    private val repeat: AppCompatImageButton
        get() = binding.appCompatImageButtonRepeat
    private val prev: AppCompatImageButton
        get() = binding.appCompatImageButtonPrev
    private val next: AppCompatImageButton
        get() = binding.appCompatImageButtonNext
    private val playbackControlContainer: ConstraintLayout
        get() = binding.constraintLayoutPlaybackControlContainer
    private val shuffle: AppCompatImageButton
        get() = binding.appCompatImageButtonShuffle

    private val playbackContainerSet = ConstraintSet()

    override fun onSetupRequireWindowInsets() = { insets: Rect ->
        constantRoot.updatePadding(top = insets.top, bottom = insets.bottom)
    }

    abstract fun setupExit(fragment: Fragment)

    fun setupSlider(
        transportControls: TransportControls, slidingListener: (Boolean) -> Unit
    ) {
        position.setLabelFormatter { value ->
            value.toLong().durationStr
        }
        position.addOnSliderTouchListener(
            object: OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) =
                    slidingListener.invoke(true)

                override fun onStopTrackingTouch(slider: Slider) {
                    // Update playback to position first
                    transportControls.seekTo(slider.value.toLong())
                    slidingListener.invoke(false)
                }
            }
        )
    }

    fun updateMetadata(title: String, artist: String, album: String, duration: Long) {
        binding.title = title
        binding.artist = artist
        binding.album = album
        binding.duration = duration.timeStr
        position.valueTo = duration.toFloat()
    }

    fun updateCoverImage(fragment: Fragment, uri: Uri) {
        Glide.with(fragment)
            .load(uri)
            .placeholder(cover.drawable)
            // .transition(DrawableTransitionOptions.withCrossFade())
            .into(cover)
    }

    fun updatePlaybackPosition(positionLong: Long, isSliding: Boolean) {
        binding.position = positionLong.timeStr
        if (!isSliding) {
            position.value = positionLong.toFloat()
        }
    }

    fun notifyUpdatePlaybackController(fragment: Fragment, @DrawableRes resId: Int) {
        fragment.lifecycleScope.main {
            val drawable = withContext(default) {
                ContextCompat.getDrawable(fragment.requireContext(), resId)
            }
            if (drawable != null) {
                playbackControl.setImageDrawable(drawable)
                if (drawable is AnimatedVectorDrawable) {
                    drawable.start()
                }
            }
        }
    }

    fun setupPlaybackControls(
        playbackStateManager: PlaybackStateManager,
        mediaControllerCompat: MediaControllerCompat,
        transportControls: TransportControls
    ) {
        val onClickListener = OnClickListener {
            when (it) {
                repeat -> {
                    transportControls.setRepeatMode(
                        when (mediaControllerCompat.repeatMode) {
                            REPEAT_MODE_NONE -> REPEAT_MODE_ALL
                            REPEAT_MODE_ALL -> REPEAT_MODE_ONE
                            else -> REPEAT_MODE_NONE
                        }
                    )
                }
                playbackControl -> {
                    when (playbackStateManager.playbackState) {
                        STATE_PLAYING -> { transportControls.pause() }
                        STATE_PAUSED -> { transportControls.play() }
                    }
                }
                prev -> { transportControls.skipToPrevious() }
                next -> { transportControls.skipToNext() }
                shuffle -> {
                    transportControls.setShuffleMode(
                        when (mediaControllerCompat.shuffleMode) {
                            SHUFFLE_MODE_ALL -> SHUFFLE_MODE_NONE
                            else -> SHUFFLE_MODE_ALL
                        }
                    )
                }
            }
        }
        repeat.setOnClickListener(onClickListener)
        playbackControl.setOnClickListener(onClickListener)
        prev.setOnClickListener(onClickListener)
        next.setOnClickListener(onClickListener)
        shuffle.setOnClickListener(onClickListener)
    }

    fun setupShuffleMode() {
        playbackContainerSet.clone(playbackControlContainer)
    }

    fun notifyPlaybackModesChanged(@RepeatMode repeatMode: Int, @ShuffleMode shuffleMode: Int) {
        setPlaybackModes(repeatMode, shuffleMode, true)
    }

    fun setPlaybackModes(@RepeatMode repeatMode: Int, @ShuffleMode shuffleMode: Int) {
        setPlaybackModes(repeatMode, shuffleMode, false)
    }

    private fun setPlaybackModes(
        @RepeatMode repeatMode: Int, @ShuffleMode shuffleMode: Int, requireTransition: Boolean
    ) {
        repeat.setImageResource(
            when (repeatMode) {
                REPEAT_MODE_ONE -> R.drawable.ic_round_repeat_one_24
                else -> R.drawable.ic_round_repeat_24
            }
        )
        when {
            repeatMode != REPEAT_MODE_NONE && shuffleMode != SHUFFLE_MODE_NONE -> {
                playbackContainerSet
            }
            else -> ConstraintSet().apply {
                clone(playbackContainerSet)
                if (repeatMode == REPEAT_MODE_NONE) {
                    setAlpha(repeat.id, REPEAT_DISABLED_ALPHA)
                }
                if (shuffleMode == SHUFFLE_MODE_NONE) {
                    setAlpha(shuffle.id, SHUFFLE_DISABLED_ALPHA)
                }
            }
        }.let { constraintSet ->
            if (requireTransition) {
                TransitionManager.beginDelayedTransition(playbackControlContainer)
            }
            constraintSet.applyTo(playbackControlContainer)
        }
    }

    private class CompatImpl(binding: FragmentPlayerBinding): PlayerLayoutCompat(binding) {

        private companion object {
            const val CIRCULAR_REVEAL_START_RADIUS = 0F
        }

        private val exit: CircularRevealCardView
            get() = binding.circularRevealCardViewExit!!

        override fun setupExit(fragment: Fragment) {
            exit.setOnClickListener {
                fragment.requireActivity()
                    .onBackPressedDispatcher
                    .onBackPressed()
            }
            fragment.lifecycleScope.launchWhenResumed {
                when {
                    exit.isAttachedToWindow -> startExitButtonAnimation(fragment)
                    else -> exit.doOnAttach { startExitButtonAnimation(fragment) }
                }
            }
        }

        private fun startExitButtonAnimation(fragment: Fragment) {
            val maxRadius = max(exit.width / 2F, exit.height / 2F)
            exit.revealInfo = RevealInfo(maxRadius, maxRadius, CIRCULAR_REVEAL_START_RADIUS)
            CircularRevealCompat.createCircularReveal(exit, maxRadius, maxRadius, maxRadius)
                .setDuration(fragment.resources.getLong(R.integer.anim_duration_400) / 2)
                .apply { doOnStart { exit.isVisible = true } }
                .start()
        }

    }

    private class W600dpImpl(binding: FragmentPlayerBinding): PlayerLayoutCompat(binding) {

        private val exit: AppCompatImageButton
            get() = binding.appCompatImageButtonExit!!

        override fun setupExit(fragment: Fragment) {
            exit.setOnClickListener {
                fragment.requireActivity()
                    .onBackPressedDispatcher
                    .onBackPressed()
            }
        }

    }

    private class W1240dpImpl(binding: FragmentPlayerBinding): PlayerLayoutCompat(binding) {

        private val exit: AppCompatImageButton
            get() = binding.appCompatImageButtonExit!!

        override fun setupExit(fragment: Fragment) {
            exit.setOnClickListener {
                fragment.requireActivity()
                    .onBackPressedDispatcher
                    .onBackPressed()
            }
        }

    }

}