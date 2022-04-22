package projekt.cloud.piece.music.player.ui.play.control

import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_AUDIO_ITEM
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_BITMAP_ART
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_COLOR_ITEM
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_POSITION
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlayControlBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.imageBitmap = containerViewModel.bitmapArt
        containerViewModel.colorItem?.let {
            updateColors(it.background, it.primary, it.secondary)
        }
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
    }
    
    private fun updateColors(background: Int, primary: Int, secondary: Int) {
        binding.backgroundColor = background
        binding.primaryColor = primary
        binding.secondaryColor = secondary
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