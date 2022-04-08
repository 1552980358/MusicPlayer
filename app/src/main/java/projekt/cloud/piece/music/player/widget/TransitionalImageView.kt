package projekt.cloud.piece.music.player.widget

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.animation.doOnEnd
import androidx.databinding.BindingAdapter
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION

class TransitionalImageView(context: Context, attributeSet: AttributeSet): LinearLayout(context, attributeSet) {

    companion object {

        @JvmStatic
        @BindingAdapter("bitmap")
        fun TransitionalImageView.setBitmap(bitmap: Bitmap?) {
            bitmap?.let { setNextBitmap(it) }
        }

    }

    private val size = resources.displayMetrics.widthPixels

    private var animator: Animator? = null

    private var linearLayout: LinearLayout

    init {
        orientation = VERTICAL
        linearLayout = LinearLayout(context)
        addView(linearLayout, LayoutParams(size, size))
        with(linearLayout) {
            alpha = 1F
            visibility = VISIBLE
        }
    }

    fun setNextBitmap(bitmap: Bitmap) {
        background = BitmapDrawable(resources, bitmap)
        if (linearLayout.background == null) {
            linearLayout.background = background
            return
        }
        animator = ValueAnimator.ofFloat(1F, 0F).apply {
            duration = ANIMATION_DURATION
            addUpdateListener { linearLayout.alpha = animatedValue as Float }
            doOnEnd {
                linearLayout.background = background
                linearLayout.alpha = 1F
            }
            ui { start() }
        }
    }

}