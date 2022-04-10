package projekt.cloud.piece.music.player.widget

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View.MeasureSpec.EXACTLY
import android.view.View.MeasureSpec.UNSPECIFIED
import android.view.View.MeasureSpec.makeMeasureSpec
import androidx.core.animation.doOnEnd
import androidx.databinding.BindingAdapter
import com.google.android.material.card.MaterialCardView
import lib.github1552980358.ktExtension.android.view.getDimensionPixelSize
import lib.github1552980358.ktExtension.android.view.getString
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.databinding.CardViewCollapsingBinding
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION

class CollapsingCardView(context: Context, attributeSet: AttributeSet?): MaterialCardView(context, attributeSet) {

    companion object {

        @JvmStatic
        @BindingAdapter("title")
        fun CollapsingCardView.setTitle(resId: Int) {
            setTitleText(getString(resId))
        }

        @JvmStatic
        @BindingAdapter("content")
        fun CollapsingCardView.setContent(title: String) {
            setContentText(title)
        }

    }

    private val binding = CardViewCollapsingBinding.inflate(LayoutInflater.from(context))
    private val textViewContent get() = binding.textViewContent
    private val imageViewIndicator get() = binding.imageViewIndicator

    private val textViewContentHeight = makeMeasureSpec(0, UNSPECIFIED)
    private val textViewContentWidth by lazy {
        makeMeasureSpec(width - 2 * getDimensionPixelSize(R.dimen.md_spec_card_button_margin_horizontal), EXACTLY)
    }

    private var isExpanded = false

    private var animator: Animator? = null

    init {
        addView(binding.root)
        setOnClickListener {
            isExpanded = !isExpanded
            when {
                isExpanded -> {
                    expand()
                    imageViewIndicator.setImageResource(R.drawable.ani_arrow_expand)
                    (imageViewIndicator.drawable as AnimatedVectorDrawable).start()
                }
                else -> {
                    collapse()
                    imageViewIndicator.setImageResource(R.drawable.ani_arrow_collapse)
                    (imageViewIndicator.drawable as AnimatedVectorDrawable).start()
                }
            }
        }
    }

    fun setIsExpand(isExpand: Boolean) {
        binding.textViewContent.visibility = when {
            isExpand -> VISIBLE
            else -> GONE
        }
        isExpanded = isExpand
    }

    private fun expand() {
        textViewContent.measure(textViewContentWidth, textViewContentHeight)
        animator = ValueAnimator.ofInt(0, textViewContent.measuredHeight).apply {
            duration = ANIMATION_DURATION
            addUpdateListener {
                with(textViewContent) {
                    layoutParams = layoutParams.apply { height = it.animatedValue as Int }
                }
            }
            doOnEnd { animator = null }
            textViewContent.visibility = VISIBLE
            start()
        }
    }

    private fun collapse() {
        animator = ValueAnimator.ofInt(textViewContent.measuredHeight, 0).apply {
            duration = ANIMATION_DURATION
            addUpdateListener {
                with(textViewContent) {
                    layoutParams = layoutParams.apply { height = it.animatedValue as Int }
                }
            }
            doOnEnd {
                animator = null
                textViewContent.visibility = GONE
            }
            start()
        }
    }

    fun setTitleText(title: String) {
        binding.title = title
    }

    fun setContentText(content: String) {
        binding.content = content
    }

}