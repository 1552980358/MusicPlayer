package projekt.cloud.piece.music.player.ui.fragment.player

import android.graphics.Rect
import android.graphics.drawable.AnimatedVectorDrawable
import android.net.Uri
import android.support.v4.media.session.MediaControllerCompat.TransportControls
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import android.view.View.OnClickListener
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.slider.Slider
import com.google.android.material.slider.Slider.OnSliderTouchListener
import kotlin.reflect.KClass
import kotlinx.coroutines.withContext
import projekt.cloud.piece.music.player.base.BaseLayoutCompat
import projekt.cloud.piece.music.player.databinding.FragmentPlayerBinding
import projekt.cloud.piece.music.player.util.CoroutineUtil.default
import projekt.cloud.piece.music.player.util.CoroutineUtil.main
import projekt.cloud.piece.music.player.util.PlaybackStateManager
import projekt.cloud.piece.music.player.util.ScreenDensity
import projekt.cloud.piece.music.player.util.TimeUtil.durationStr
import projekt.cloud.piece.music.player.util.TimeUtil.timeStr

abstract class PlayerLayoutCompat(binding: FragmentPlayerBinding): BaseLayoutCompat<FragmentPlayerBinding>(binding) {

    private companion object {

        @Suppress("unused")
        @Keep
        @JvmStatic
        @JvmName(METHOD_GET_IMPL)
        fun getImpl(screenDensity: ScreenDensity): KClass<*> {
            return determineLayoutCompat(
                screenDensity,
                arrayOf(CompatImpl::class, W600dpImpl::class, W1240dpImpl::class)
            )
        }

    }

    private val constantRoot: ConstraintLayout
        get() = binding.constraintLayoutRoot
    private val position: Slider
        get() = binding.sliderPosition
    private val cover: ShapeableImageView
        get() = binding.shapeableImageViewImageCover
    private val playbackControl: FloatingActionButton
        get() = binding.floatingActionButtonPlaybackControl
    private val prev: AppCompatImageButton
        get() = binding.appCompatImageButtonPrev
    private val next: AppCompatImageButton
        get() = binding.appCompatImageButtonNext

    override val requireWindowInsets: Boolean
        get() = true

    override fun onSetupRequireWindowInsets() = { insets: Rect ->
        constantRoot.updatePadding(top = insets.top, bottom = insets.bottom)
    }

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
        playbackStateManager: PlaybackStateManager, transportControls: TransportControls
    ) {
        val onClickListener = OnClickListener {
            when (it) {
                playbackControl -> {
                    when (playbackStateManager.playbackState) {
                        STATE_PLAYING -> { transportControls.pause() }
                        STATE_PAUSED -> { transportControls.play() }
                    }
                }
                prev -> { transportControls.skipToPrevious() }
                next -> { transportControls.skipToNext() }
            }
        }
        playbackControl.setOnClickListener(onClickListener)
        prev.setOnClickListener(onClickListener)
        next.setOnClickListener(onClickListener)
    }

    @Keep
    private class CompatImpl(binding: FragmentPlayerBinding): PlayerLayoutCompat(binding)

    @Keep
    private class W600dpImpl(binding: FragmentPlayerBinding): PlayerLayoutCompat(binding)

    @Keep
    private class W1240dpImpl(binding: FragmentPlayerBinding): PlayerLayoutCompat(binding)

}