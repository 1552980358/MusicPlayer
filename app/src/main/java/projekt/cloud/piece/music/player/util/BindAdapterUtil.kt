package projekt.cloud.piece.music.player.util

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.view.View
import androidx.databinding.BindingAdapter
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION

object BindAdapterUtil {
    
    @JvmStatic
    @BindingAdapter("animatedAlpha")
    fun View.setAnimatedAlpha(newAlpha: Float) {
        if (alpha != newAlpha) {
            ValueAnimator.ofFloat(alpha, newAlpha).apply {
                duration = ANIMATION_DURATION
                addUpdateListener { alpha = it.animatedValue as Float }
            }.start()
        }
    }
    
    @JvmStatic
    @BindingAdapter("rippleColorStateList")
    fun View.setRippleColorStateList(colorStateList: ColorStateList) {
        (background as? RippleDrawable ?: return setBackground(RippleDrawable(colorStateList, null, null)))
            .setColor(colorStateList)
    }

}