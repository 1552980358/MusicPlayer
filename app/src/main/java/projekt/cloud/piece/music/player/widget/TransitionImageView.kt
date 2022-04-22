package projekt.cloud.piece.music.player.widget

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View.MeasureSpec.EXACTLY
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView.ScaleType.FIT_XY
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.animation.doOnEnd
import androidx.databinding.BindingAdapter
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui
import kotlin.math.min

class TransitionImageView(context: Context, attributeSet: AttributeSet?): ViewGroup(context, attributeSet) {

    companion object {

        @JvmStatic
        @BindingAdapter("default_image")
        fun TransitionImageView.setDefaultImageDrawable(drawable: Drawable?) {
            defaultImage = drawable
        }

    }

    private val appCompatImageView = AppCompatImageView(context)

    private var defaultImage: Drawable? = null

    private var animator: Animator? = null

    init {
        appCompatImageView.scaleType = FIT_XY
        addView(appCompatImageView, LayoutParams(MATCH_PARENT, MATCH_PARENT))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measureSpec = MeasureSpec.makeMeasureSpec(
            min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec)),
            EXACTLY
        )

        setMeasuredDimension(measureSpec, measureSpec)
        measureChildren(measureSpec, measureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        appCompatImageView.layout(0, 0, measuredWidth, measuredHeight)
    }

    fun setBitmap(bitmap: Bitmap?) {
        background = bitmap?.run { BitmapDrawable(resources, this) } ?: defaultImage
        if (appCompatImageView.drawable == null) {
            return appCompatImageView.setImageDrawable(background)
        }

        ui { animator?.cancel() }
        animator = ValueAnimator.ofFloat(1F, 0F).apply {
            duration = ANIMATION_DURATION
            addUpdateListener { appCompatImageView.alpha = animatedValue as Float }
            doOnEnd {
                appCompatImageView.setImageDrawable(background)
                appCompatImageView.alpha = 1F
                animator = null
            }
            ui { start() }
        }
    }

}