package projekt.cloud.piece.music.player.ui.play

import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import kotlinx.coroutines.delay
import lib.github1552980358.ktExtension.android.content.getStatusBarHeight
import lib.github1552980358.ktExtension.android.view.heightF
import lib.github1552980358.ktExtension.android.view.widthF
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BasePlayFragment
import projekt.cloud.piece.music.player.databinding.FragmentPlayBinding

class PlayFragment: BasePlayFragment() {
    
    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!
    
    private val contentPlayFragmentBottomSheet get() =
        binding.contentPlayFragmentBottomSheet
    
    private val contentPlayFragmentButtons get() =
        binding.contentPlayFragmentButtons
    
    private val imageViewCycle get() = contentPlayFragmentButtons.imageViewCycle
    private val imageViewPrev get() = contentPlayFragmentButtons.imageViewPrev
    private val imageViewNext get() = contentPlayFragmentButtons.imageViewNext
    private val imageViewShuffle get() = contentPlayFragmentButtons.imageViewShuffle
    
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CardView>
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_play, container, false)
        
        binding.imageView.apply {
            layoutParams = layoutParams.apply {
                height = resources.displayMetrics.widthPixels
            }
        }
    
        loadMetadata(activityInterface.requestMetadata())
        
        activityInterface.setListener(
            loadMetadata = { audioItem ->
            
            },
            loadBitmap = { bitmap -> binding.coverImage = bitmap }
        )
        
        return binding.root
    }
    
    @Suppress("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        contentPlayFragmentBottomSheet.cardView.apply {
            layoutParams = layoutParams.apply {
                height = pixelHeight - requireContext().getStatusBarHeight()
            }
            setContentPadding(0, 0, 0, navigationBarHeight)
        }
    
        val bottomHeight = pixelHeight - resources.displayMetrics.widthPixels
        
        contentPlayFragmentButtons.linearLayout.apply {
            layoutParams = layoutParams.apply { height = bottomHeight * 2 / 5 }
        }
        
        bottomSheetBehavior = BottomSheetBehavior.from(contentPlayFragmentBottomSheet.cardView)
        with(bottomSheetBehavior) {
            peekHeight = bottomHeight * 3 / 5
            isHideable = true
            state = STATE_HIDDEN
        }
        
        contentPlayFragmentButtons.linearLayout.setOnTouchListener { _, motionEvent ->
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
    
    override fun onStart() {
        super.onStart()
        io {
            delay(ANIMATION_DURATION_HALF_LONG)
            ui {
                bottomSheetBehavior.state = STATE_COLLAPSED
                bottomSheetBehavior.isHideable = false
            }
        }
    }
    
    private fun loadMetadata(audioItem: AudioItem) {
        contentPlayFragmentBottomSheet.title = audioItem.title
        contentPlayFragmentBottomSheet.subtitle = "${audioItem.artistItem.name} - ${audioItem.albumItem.title}"
    }

}