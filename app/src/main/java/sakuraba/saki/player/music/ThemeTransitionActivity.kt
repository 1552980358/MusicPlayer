package sakuraba.saki.player.music

import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewAnimationUtils.createCircularReveal
import android.widget.ImageView.ScaleType.MATRIX
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.core.animation.doOnEnd
import androidx.core.view.WindowCompat
import androidx.core.view.doOnAttach
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import sakuraba.saki.player.music.base.BaseThemeAppCompatActivity
import sakuraba.saki.player.music.databinding.ActivityThemeTransitionBinding
import sakuraba.saki.player.music.util.ActivityUtil.setLightNavigationBar
import sakuraba.saki.player.music.util.Constants.ANIMATION_DURATION_LONG
import kotlin.math.hypot

class ThemeTransitionActivity: BaseThemeAppCompatActivity() {

    companion object {
        private var _screenshot: Bitmap? = null
        fun setScreenshot(screenshot: Bitmap?) {
            _screenshot = screenshot
        }
        private val screenshot get() = _screenshot!!

        const val EXTRA_IS_NIGHT = "is-night"
    }

    private lateinit var layout: ActivityThemeTransitionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setLightNavigationBar()
        super.onCreate(savedInstanceState)

        layout = ActivityThemeTransitionBinding.inflate(layoutInflater)
        setContentView(layout.root)

        layout.imageView.scaleType = MATRIX
        layout.imageView.setImageBitmap(screenshot)

        ui {
            setDefaultNightMode(
                when {
                    intent.getBooleanExtra(EXTRA_IS_NIGHT, true) -> MODE_NIGHT_YES
                    else -> MODE_NIGHT_NO
                }
            )

            layout.imageView.doOnAttach {
                createCircularReveal(layout.imageView, 0, 0, hypot(screenshot.width.toFloat(), screenshot.height.toFloat()), 0F).apply {
                    duration = ANIMATION_DURATION_LONG * 2
                    doOnEnd {
                        finish()
                        overridePendingTransition(0, 0)
                    }
                }.start()
            }

        }

    }

}