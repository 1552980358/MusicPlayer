package projekt.cloud.piece.music.player

import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewAnimationUtils.createCircularReveal
import android.widget.ImageView.ScaleType.MATRIX
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.core.animation.doOnEnd
import androidx.core.view.WindowCompat.setDecorFitsSystemWindows
import androidx.core.view.doOnAttach
import lib.github1552980358.ktExtension.android.graphics.heightF
import lib.github1552980358.ktExtension.android.graphics.widthF
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.base.BaseThemeActivity
import projekt.cloud.piece.music.player.databinding.ActivityThemeTransitionBinding
import kotlin.math.hypot

class ThemeTransitionActivity: BaseThemeActivity() {
    
    companion object {
        private var _screenshot: Bitmap? = null
        fun setScreenshot(screenshot: Bitmap?) {
            _screenshot = screenshot
        }
        private val screenshot get() = _screenshot!!
        
        const val EXTRA_IS_NIGHT = "is-night"
        
        private const val ANIMATION_DURATION = 1000L
    }
    
    private lateinit var binding: ActivityThemeTransitionBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
    
        setDecorFitsSystemWindows(window, false)
        
        super.onCreate(savedInstanceState)
        
        binding = ActivityThemeTransitionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.imageView.scaleType = MATRIX
        binding.imageView.setImageBitmap(screenshot)
        
        ui {
            setDefaultNightMode(when {
                    intent.getBooleanExtra(EXTRA_IS_NIGHT, true) -> MODE_NIGHT_YES
                    else -> MODE_NIGHT_NO
                }
            )
            
            binding.imageView.doOnAttach {
                createCircularReveal(binding.imageView, 0, 0, hypot(screenshot.widthF, screenshot.heightF), 0F).apply {
                    duration = ANIMATION_DURATION
                    doOnEnd {
                        setScreenshot(null)
                        finish()
                        overridePendingTransition(0, 0)
                    }
                }.start()
            }
            
        }
        
    }

}