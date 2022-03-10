package projekt.cloud.piece.music.player.ui.play

import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BasePlayFragment
import projekt.cloud.piece.music.player.databinding.FragmentPlayBinding

class PlayFragment: BasePlayFragment() {
    
    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_play, container, false)
        
        binding.imageView.apply {
            layoutParams = layoutParams.apply {
                height = resources.displayMetrics.widthPixels
            }
        }
        
        activityInterface.setListener(
            loadMetadata = { audioItem ->
            
            },
            loadBitmap = { bitmap -> binding.coverImage = bitmap }
        )
        
        return binding.root
    }
    
    @Suppress("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.contentPlayFragmentButtons.linearLayout.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                ACTION_DOWN -> {
                    binding.relativeLayout.apply {
                        (background as RippleDrawable).setHotspot(motionEvent.x, motionEvent.y)
                        isPressed = true
                    }
                }
                ACTION_UP -> {
                    binding.relativeLayout.isPressed = false
                }
            }
            return@setOnTouchListener true
        }
    }

}