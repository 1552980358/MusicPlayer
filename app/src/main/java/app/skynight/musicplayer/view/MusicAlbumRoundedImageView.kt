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

/**
 * @File    : CircleImageView
 * @Author  : 1552980358
 * @Date    : 28 Jul 2019
 * @TIME    : 11:56 AM
 **/

class MusicAlbumRoundedImageView : AppCompatImageView {
    var size = 0

    constructor(context: Context) : this(context, null)
    @Suppress("UNUSED_PARAMETER")
    constructor(context: Context, attributeSet: AttributeSet?) : super(context) {
        //setImageDrawable(ContextCompat.getDrawable(context, R.drawable.unknown))
        background = ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent))
        setImageDrawable(
            BitmapDrawable(
                context.resources, BitmapFactory.decodeStream(context.assets.open("unknown.png"))
            )
        )
    }

    override fun setImageBitmap(bm: Bitmap) {
        if (bm.width == bm.height) {
            super.setImageDrawable(RoundedBitmapDrawableFactory.create(
                context.resources, Bitmap.createScaledBitmap(bm, size, size, true)
            ).apply {
                isCircular = true
            })
            return
        }

        if (bm.width < bm.height) {
            super.setImageDrawable(RoundedBitmapDrawableFactory.create(
                context.resources,
                Bitmap.createScaledBitmap(
                    Bitmap.createBitmap(
                        bm,
                        0,
                        (bm.height - bm.width) / 2,
                        bm.width,
                        bm.width
                    ), size, size, true
                )
            ).apply {
                isCircular = true
            })
            return
        }

        super.setImageDrawable(RoundedBitmapDrawableFactory.create(
            context.resources,
            Bitmap.createScaledBitmap(
                Bitmap.createBitmap(
                    bm,
                    (bm.width - bm.height) / 2,
                    0,
                    bm.width,
                    bm.width
                ), size, size, true
            )
        ).apply {
            isCircular = true
        })


        /*
        log("setImageBitmap", "${bm.width} ${bm.height}")
        /* 尺寸刚刚好, 只需要圆形处理 */
        if (bm.width == bm.height) {
            if (bm.height == size) {
                log("setImageBitmap", "bm.height == size")
                super.setImageDrawable(RoundedBitmapDrawableFactory.create(
                    context.resources, bm
                ).apply {
                    isCircular = true
                })
                return
            }

            if (bm.width > size) {
                log("setImageBitmap", "bm.width == bm.height: bm.width > size")
                super.setImageDrawable(RoundedBitmapDrawableFactory.create(
                    context.resources, Bitmap.createBitmap(bm, (bm.width - size) / 2, ( bm.width - size) / 2, size, size, null, true)
                ).apply {
                    isCircular = true
                })
                return
            }

            log("setImageBitmap", "bm.width == bm.height: bm.width < size")
            val tmp = Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, Matrix().apply {
                val scale = size / bm.height.toFloat()
                postScale(scale, scale)
            }, true)
            super.setImageDrawable(RoundedBitmapDrawableFactory.create(
                content.resources,
                Bitmap.createBitmap(tmp, 0, 0, size, size, null, true)
            ).apply { isCircular = true })
            return
        }

        if (bm.width < bm.height) {
            /* 剪裁高度 */
            if (bm.width == size) {
                log("setImageBitmap", "bm.width < bm.height: bm.width == size")
                super.setImageDrawable(RoundedBitmapDrawableFactory.create(
                    content.resources,
                    Bitmap.createBitmap(bm, 0, (bm.height - size) / 2, size, size, null, true)
                ).apply { isCircular = true })
                return
            }

            /* 宽高都剪裁 */
            if (bm.width > size) {
                log("setImageBitmap", "bm.width < bm.height: bm.width > size")
                super.setImageDrawable(RoundedBitmapDrawableFactory.create(
                    content.resources, Bitmap.createBitmap(
                        bm,
                        (bm.width - size) / 2,
                        (bm.height - size) / 2,
                        size,
                        size,
                        null,
                        true
                    )
                ).apply { isCircular = true })
                return
            }

            log("setImageBitmap", "bm.width < bm.height: bm.width < size")
            /* 放大后剪裁 */
            val tmp = Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, Matrix().apply {
                val scale = size / bm.width.toFloat()
                postScale(scale, scale)
            }, true)
            super.setImageDrawable(RoundedBitmapDrawableFactory.create(
                content.resources,
                Bitmap.createBitmap(tmp, 0, (bm.height - size) / 2, size, size, null, true)
            ).apply { isCircular = true })
            return
        }

        /* bm.height < bm.width */
        /* 剪裁宽度 */
        if (bm.height == size) {
            log("setImageBitmap", "bm.height == size")
            super.setImageDrawable(RoundedBitmapDrawableFactory.create(
                content.resources,
                Bitmap.createBitmap(bm, (bm.width - size) / 2, 0, size, size, null, true)
            ).apply { isCircular = true })
            return
        }

        /* 宽高都剪裁 */
        if (bm.height > size) {
            log("setImageBitmap", "bm.height > size")
            super.setImageDrawable(RoundedBitmapDrawableFactory.create(
                content.resources, Bitmap.createBitmap(
                    bm, (size - bm.width) / 2, (size - bm.height) / 2, size, size, null, true
                )
            ).apply { isCircular = true })
            return
        }

        log("setImageBitmap", "bm.height < size")
        /* 放大后剪裁 */
        val tmp = Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, Matrix().apply {
            val scale = size / bm.height.toFloat()
            postScale(scale, scale)
        }, true)
        super.setImageDrawable(RoundedBitmapDrawableFactory.create(
            content.resources,
            Bitmap.createBitmap(tmp, (size - bm.height) / 2, 0, size, size, null, true)
        ).apply { isCircular = true })
        return
        */
    }
}