package projekt.cloud.piece.music.player.ui.play.control

import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_CANCEL
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewGroup
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_AUDIO_ITEM
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_BITMAP_ART
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_COLOR_ITEM
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_IS_PLAYING
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_POSITION
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.database.audio.item.AudioItem
import projekt.cloud.piece.music.player.database.audio.item.ColorItem
import projekt.cloud.piece.music.player.databinding.FragmentPlayControlBinding
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION

class PlayControlFragment: BaseFragment() {

    companion object {
        private const val TAG = "PlayControlFragment"
    }

    private var _binding: FragmentPlayControlBinding? = null
    private val binding get() = _binding!!
    private val root get() = binding.root
    private val buttonPlayControl get() = binding.buttonsPlayControl
    private val appCompatImageViewRepeat get() = buttonPlayControl.appCompatImageViewRepeat
    private val appCompatImageViewPrev get() = buttonPlayControl.appCompatImageViewPrev
    private val appCompatImageViewNext get() = buttonPlayControl.appCompatImageViewNext
    private val appCompatImageViewShuffle get() = buttonPlayControl.appCompatImageViewShuffle
    
    private val floatingActionButton get() = binding.buttonsPlayControl.floatingActionButton

    private val transportControls get() = requireActivity().mediaController.transportControls
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlayControlBinding.inflate(layoutInflater, container, false)
        with(binding) {
            imageBitmap = containerViewModel.bitmapArt
            audioItem = containerViewModel.audioItem
            containerViewModel.colorItem?.let {
                backgroundColor = it.background
                primaryColor = it.primary
                secondaryColor = it.secondary
            }
            position = containerViewModel.position
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        containerViewModel.register<AudioItem>(TAG, LABEL_AUDIO_ITEM) {
            binding.audioItem = it
        }
        containerViewModel.register<Bitmap>(TAG, LABEL_BITMAP_ART) {
            binding.imageBitmap = it
        }
        containerViewModel.register<ColorItem>(TAG, LABEL_COLOR_ITEM) { colorItem ->
            colorItem?.let {
                updateColorsAnimated(it.background, it.primary, it.secondary)
            }
        }
        containerViewModel.register<Long>(TAG, LABEL_POSITION) {
            binding.position = it
        }

        @Suppress("ClickableViewAccessibility")
        with(root) {
            setOnTouchListener { _, event ->
                when (event.action) {
                    ACTION_DOWN -> {
                        (background as RippleDrawable).setHotspot(event.x, event.y)
                        isPressed = true
                    }
                    ACTION_CANCEL -> isPressed = false
                    ACTION_UP -> {
                        isPressed = false
                        processTouchEvent(event.rawX, event.rawY)
                    }
                }
                true
            }
        }
        
        with(floatingActionButton) {
            setImageResource(
                when {
                    containerViewModel.isPlaying -> R.drawable.ic_baseline_pause_24
                    else -> R.drawable.ic_baseline_play_arrow_24
                }
            )
            containerViewModel.register<Boolean>(TAG, LABEL_IS_PLAYING) { isPlaying ->
                isPlaying?.let {
                    if (drawable == null) {
                        return@register setImageResource(
                            when {
                                it -> R.drawable.ic_baseline_pause_24
                                else -> R.drawable.ic_baseline_play_arrow_24
                            }
                        )
                    }
                    setImageResource(
                        when {
                            it -> R.drawable.anim_baseline_play_24
                            else -> R.drawable.anim_baseline_pause_24
                        }
                    )
                    (drawable as? AnimatedVectorDrawable)?.start()
                }
            }
            setOnClickListener {
                when (containerViewModel.isPlaying) {
                    true -> transportControls.pause()
                    false -> transportControls.play()
                }
            }
        }
    }

    private fun processTouchEvent(rawX: Float, rawY: Float) {
        val rawPosition = IntArray(2)
        when {
            compareViewLocation(rawX, rawY, appCompatImageViewRepeat, rawPosition) -> {
            }

            compareViewLocation(rawX, rawY, appCompatImageViewPrev, rawPosition) ->
                transportControls.skipToPrevious()

            compareViewLocation(rawX, rawY, appCompatImageViewNext, rawPosition) ->
                transportControls.skipToNext()

            compareViewLocation(rawX, rawY, appCompatImageViewShuffle, rawPosition) -> {
            }
        }
    }

    private fun compareViewLocation(touchX: Float, touchY: Float, view: View, position: IntArray): Boolean {
        view.getLocationOnScreen(position)
        return compareViewLocation(
            touchX,
            touchY,
            position[0],
            position[1],
            view.width,
            view.height
        )
    }

    private fun compareViewLocation(touchX: Float,
                                    touchY: Float,
                                    viewX: Int,
                                    viewY: Int,
                                    viewWidth: Int,
                                    viewHeight: Int) =
        compareViewAxis(touchX, viewX.toFloat(), viewWidth)
            && compareViewAxis(touchY, viewY.toFloat(), viewHeight)

    private fun compareViewAxis(touchAxis: Float, viewAxis: Float, viewSize: Int) =
        touchAxis in viewAxis .. viewAxis + viewSize


    private fun updateColorsAnimated(background: Int, primary: Int, secondary: Int) {
        ValueAnimator.ofArgb(binding.backgroundColor!!, background).apply {
            duration = ANIMATION_DURATION
            addUpdateListener { binding.backgroundColor = animatedValue as Int }
            start()
        }
        ValueAnimator.ofArgb(binding.primaryColor!!, primary).apply {
            duration = ANIMATION_DURATION
            addUpdateListener { binding.primaryColor = animatedValue as Int }
            start()
        }
        ValueAnimator.ofArgb(binding.secondaryColor!!, secondary).apply {
            duration = ANIMATION_DURATION
            addUpdateListener { binding.secondaryColor = animatedValue as Int }
            start()
        }
    }

}