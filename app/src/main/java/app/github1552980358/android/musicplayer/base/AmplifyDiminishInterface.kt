package app.github1552980358.android.musicplayer.base

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.widget.ImageView
import app.github1552980358.android.musicplayer.R

/**
 * @file    : [AmplifyDiminishInterface]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/11
 * @time    : 16:39
 **/

@Deprecated("Replaced with Share Element provided by Android Framework", ReplaceWith("Share Element"), DeprecationLevel.HIDDEN)
interface AmplifyDiminishInterface {
    
    fun zoom(imageView: ImageView) {
        val margin = imageView.context.resources.getDimension(R.dimen.mainActivity_bottom_sheet_icon_margin)
        
        // x start
        val a = ObjectAnimator.ofFloat(
            imageView,
            "TranslationX",
            0F,
            // Calculate distance of x-axis from origin location, with canceling of magnified margin
            // 计算从原本位置到目标x轴距离, 消除放大后的margin
            imageView.context.resources.displayMetrics.widthPixels.toFloat() / 2F -
                ((imageView.context.resources.displayMetrics.widthPixels.toFloat() / imageView.layoutParams.width.toFloat() + 0.5F) * margin)
        )
        // y start
        val b = ObjectAnimator.ofFloat(
            imageView,
            "TranslationY",
            0F,
            // Calculate distance of y-axis from origin location, with canceling of magnified margin
            // 计算从原本位置到目标y轴距离, 消除放大后的margin
            (margin - imageView.context.resources.displayMetrics.heightPixels) + imageView.context.resources.displayMetrics.widthPixels / 2
        )
        val c = ObjectAnimator.ofFloat(
            imageView,
            "scaleX",
            1F,
            // x-axis magnifying ratio
            // x轴放大比例
            imageView.context.resources.displayMetrics.widthPixels / imageView.layoutParams.width.toFloat()
        )
        val d = ObjectAnimator.ofFloat(
            imageView,
            "scaleY",
            1F,
            // y-axis magnifying ratio
            // y轴放大比例
            imageView.context.resources.displayMetrics.widthPixels / imageView.layoutParams.width.toFloat()
        )
        AnimatorSet().apply {
            playTogether(a, b, c, d)
            
            duration = 3000
            start()
        }
    }
    
}