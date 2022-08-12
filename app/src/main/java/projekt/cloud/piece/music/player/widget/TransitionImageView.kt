package projekt.cloud.piece.music.player.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View.MeasureSpec.EXACTLY
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewPropertyAnimator
import android.widget.ImageView.ScaleType.FIT_XY
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import kotlin.math.min

class TransitionImageView(context: Context, attributeSet: AttributeSet? = null): ViewGroup(context, attributeSet) {
    
    companion object {
        
        private const val DEFAULT_DURATION = 400L
        
        private const val ALPHA_COVERED = 1F
        private const val ALPHA_NOT_COVERED = 0F
        
        @JvmStatic
        @BindingAdapter("bitmap")
        fun TransitionImageView.updateBitmap(bitmap: Bitmap?) {
            setBitmap(bitmap)
        }
        
    }
    
    private val appCompatImageView = AppCompatImageView(context)
    
    private var animator: ViewPropertyAnimator? = null
    
    init {
        appCompatImageView.scaleType = FIT_XY
        addView(appCompatImageView, LayoutParams(MATCH_PARENT, MATCH_PARENT))
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.colorBackground, typedValue, true)
        if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            appCompatImageView.setBackgroundColor(typedValue.data)
        }
    }
    
    private val isCovered: Boolean
        get() = appCompatImageView.alpha == ALPHA_COVERED
    
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
        if (appCompatImageView.drawable == null) {
            return appCompatImageView.setImageBitmap(bitmap)
        }
        animator?.cancel()
        when {
            isCovered -> animateImageViewCovered(bitmap)
            else -> animateImageViewNotCovered(bitmap)
        }
    }
    
    private fun animateImageViewCovered(bitmap: Bitmap?) {
        background = BitmapDrawable(resources, bitmap)
        setUpAnimator(ALPHA_NOT_COVERED)
    }
    
    private fun animateImageViewNotCovered(bitmap: Bitmap?) {
        appCompatImageView.setImageBitmap(bitmap)
        setUpAnimator(ALPHA_COVERED)
    }
    
    private fun setUpAnimator(alpha: Float) {
        animator = appCompatImageView.animate()
            .setDuration(DEFAULT_DURATION)
            .alpha(alpha)
            .withEndAction { animator = null }
        animator?.start()
    }
    
}