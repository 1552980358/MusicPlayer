package app.skynight.musicplayer.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import app.skynight.musicplayer.R
import app.skynight.musicplayer.util.Player

/**
 * @File    : PlayListView
 * @Author  : 1552980358
 * @Date    : 31 Jul 2019
 * @TIME    : 2:06 PM
 **/
class PlayListView: LinearLayout {
    private lateinit var imageView: AppCompatImageView
    private lateinit var textView: AppCompatTextView
    private fun createView() {
        addView(AppCompatImageView(context).apply {
            imageView = this
        }, LayoutParams(resources.getDimensionPixelSize(R.dimen.playListView_widget_height), resources.getDimensionPixelSize(R.dimen.playListView_widget_height)).apply {
            setMargins(resources.getDimensionPixelSize(R.dimen.playListView_widget_margin), 0, 0, 0)
            gravity = Gravity.CENTER
        })
        addView(AppCompatTextView(context).apply {
            textView = this
            setTextColor(Color.BLACK)
        }, LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            leftMargin = resources.getDimensionPixelSize(R.dimen.playListView_widget_margin)
            gravity = Gravity.CENTER
        })
    }

    fun setUp(icon: Int, text: String) {
        imageView.setImageDrawable(ContextCompat.getDrawable(context, icon).apply {
            this!!.setTint(Color.BLACK)
        })
        textView.text = text
    }
    fun setUp(icon: Int, text: Int) {
        imageView.setImageDrawable(ContextCompat.getDrawable(context, icon).apply {
            this!!.setTint(Color.BLACK)
        })
        textView.setText(text)
    }

    fun setUp(icon: Bitmap, text: Int) {
        imageView.setImageBitmap(icon)
        textView.setText(text)
    }
    @Suppress("unused")
    fun setUp(icon: Drawable?, text: Int) {
        imageView.setImageDrawable(icon)
        textView.setText(text)
    }

    fun setUp(icon: Bitmap, text: String) {
        imageView.setImageBitmap(icon)
        textView.text = text
    }
    @Suppress("unused")
    fun setUp(icon: Drawable?, text: String) {
        imageView.setImageDrawable(icon)
        textView.text = text
    }

    @Suppress("unused")
    fun setIcon(icon: Bitmap) {
        imageView.setImageBitmap(icon)
    }
    @Suppress("unused")
    fun setIcon(icon: Drawable) {
        imageView.setImageDrawable(icon)
    }

    @Suppress("unused")
    fun setText(text: String) {
        textView.text = text
    }
    @Suppress("unused")
    fun setText(text: Int) {
        textView.setText(text)
    }

    constructor(context: Context): this(context, null)
    @Suppress("UNUSED_PARAMETER")
    constructor(context: Context, attributeSet: AttributeSet?): super(context) {
        orientation = HORIZONTAL
        createView()
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, resources.getDimensionPixelSize(R.dimen.playListView_height))
        isClickable = true
        isFocusable = true
        background = ContextCompat.getDrawable(context, R.drawable.ripple_effect)
    }
}