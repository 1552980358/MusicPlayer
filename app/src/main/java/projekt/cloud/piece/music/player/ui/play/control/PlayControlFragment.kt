package projekt.cloud.piece.music.player.ui.play.control

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import projekt.cloud.piece.music.player.MainActivityViewModel.Companion.LABEL_BITMAP_ART
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.databinding.FragmentPlayControlBinding

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
        containerViewModel.register<Bitmap>(TAG, LABEL_BITMAP_ART) {
            binding.imageBitmap = it
        }
    }

}