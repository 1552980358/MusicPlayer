package projekt.cloud.piece.music.player.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import projekt.cloud.piece.music.player.R

class TwoStateImageView(context: Context, attributeSet: AttributeSet?): AppCompatImageView(context, attributeSet) {

    companion object {

        @JvmStatic
        @BindingAdapter("state")
        fun TwoStateImageView.updateState(newState: Boolean) {
            if (state != newState) {
                state = newState
            }
        }

    }

    private val positiveImage: Drawable
    private val negativeImage: Drawable

    private var state = false
        set(value) {
            field = value
            setImageDrawable(when {
                value -> positiveImage
                else -> negativeImage
            })
        }

    init {
        context.theme.obtainStyledAttributes(
            attributeSet, R.styleable.TwoStateImageView, 0, 0
        ).apply {
            positiveImage = getDrawable(R.styleable.TwoStateImageView_positiveImage)!!
            negativeImage = getDrawable(R.styleable.TwoStateImageView_negativeImage)!!
        }
    }

}