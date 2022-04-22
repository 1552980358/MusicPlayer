package projekt.cloud.piece.music.player.ui.play.control

import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
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
    
    private val floatingActionButton get() = binding.buttonsPlayControl.floatingActionButton

    private val transportControls get() = requireActivity().mediaController.transportControls
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlayControlBinding.inflate(layoutInflater, container, false)
        binding.imageBitmap = containerViewModel.bitmapArt
        binding.audioItem = containerViewModel.audioItem
        containerViewModel.colorItem?.let {
            binding.backgroundColor = it.background
            binding.primaryColor = it.primary
            binding.secondaryColor = it.secondary
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