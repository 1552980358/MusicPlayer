package app.skynight.musicplayer.util

import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs


/**
 * @FILE:   PageTransformer
 * @AUTHOR: 1552980358
 * @DATE:   22 Jul 2019
 * @TIME:   11:54 AM
 **/

class PageTransformer : ViewPager.PageTransformer {
    companion object {
        const val MIN_SCALE = 0.75f
    }

    override fun transformPage(view: View, position: Float) {

        val pageWidth = view.width
        when {
            position < -1 -> { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.alpha = 0f
            }
            position <= 0 -> { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.apply {
                    alpha = 1f
                    translationX = 0f
                    scaleX = 1f
                    scaleY = 1f
                }
            }
            position <= 1 -> { // (0,1]

                view.apply {
                    // Fade the page out.
                    alpha = 1 - position

                    // Counteract the default slide transition
                    translationX = pageWidth * -position

                    // Scale the page down (between MIN_SCALE and 1)
                    val scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - abs(position))
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                }
            }
            else -> { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.alpha = 0f
            }
        }
    }

}