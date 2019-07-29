package app.skynight.musicplayer.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import app.skynight.musicplayer.R

/**
 * @File    : CircleImageView
 * @Author  : 1552980358
 * @Date    : 28 Jul 2019
 * @TIME    : 11:56 AM
 **/

class MusicAlbumRoundedImageView: AppCompatImageView {
    constructor(context: Context): this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?): super(context) {
        //setImageDrawable(ContextCompat.getDrawable(context, R.drawable.unknown))
        background = ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent))
        setImageDrawable(BitmapDrawable(context.resources, BitmapFactory.decodeStream(context.assets.open("unknown.png"))))
    }

    override fun setImageBitmap(bm: Bitmap) {
        setImageDrawable(RoundedBitmapDrawableFactory.create(context.resources, bm).apply {
            isCircular = true
        })
    }
}