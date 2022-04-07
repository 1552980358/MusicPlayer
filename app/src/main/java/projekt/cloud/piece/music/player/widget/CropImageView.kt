package projekt.cloud.piece.music.player.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Bitmap.createScaledBitmap
import android.graphics.Canvas
import android.graphics.Color.WHITE
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.graphics.Paint.Style.STROKE
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.View
import lib.github1552980358.ktExtension.android.graphics.heightF
import lib.github1552980358.ktExtension.android.graphics.widthF
import projekt.cloud.piece.music.player.R
import kotlin.math.min

class CropImageView(context: Context, attributeSet: AttributeSet?): View(context, attributeSet) {

    companion object {
        private const val MOVE_STATE_NONE = -1
        private const val MOVE_STATE_TOP_LEFT = 0
        private const val MOVE_STATE_TOP_RIGHT = 1
        private const val MOVE_STATE_BOTTOM_LEFT = 2
        private const val MOVE_STATE_BOTTOM_RIGHT = 3
        private const val MOVE_STATE_FREE = 4
    }

    private var originBitmap: Bitmap? = null
    private var bitmapPaint = Paint().apply {
        isAntiAlias = true
    }

    private val bitmapRect = RectF()

    private val cropPaint = Paint().apply {
        isAntiAlias = true
        strokeWidth = resources.getDimension(R.dimen.crop_image_view_crop_line_width)
        style = STROKE
        color = WHITE
    }

    private val circleRadius = resources.getDimension(R.dimen.crop_image_view_circle_radius)
    private val circlePaint = Paint().apply {
        isAntiAlias = true
        style = FILL
        color = WHITE
    }

    private val cropRect = RectF()

    private val minSize = resources.getDimension(R.dimen.md_spec_list_image_size)

    init {
        var isTouched = false
        val downRect = RectF()
        val downPoint = PointF()
        var moveState = 0
        @Suppress("ClickableViewAccessibility")
        setOnTouchListener { _, event ->
            when (event.action) {

                ACTION_DOWN -> {
                    isTouched = true
                    val cropTopLeftX = cropRect.left
                    val cropTopLeftY = cropRect.top
                    val cropBottomRightX = cropRect.right
                    val cropBottomRightY = cropRect.bottom
                    when {
                        // Top left
                        event.x in cropTopLeftX - circleRadius .. cropTopLeftX + circleRadius &&
                            event.y in cropTopLeftY - circleRadius .. cropTopLeftY + circleRadius -> {
                                moveState = MOVE_STATE_TOP_LEFT
                            }

                        // Top right
                        event.x in cropBottomRightX - circleRadius .. cropBottomRightX + circleRadius
                            && event.y in cropTopLeftY - circleRadius .. cropTopLeftY + circleRadius -> {
                                moveState = MOVE_STATE_TOP_RIGHT
                            }

                        // Bottom left
                        event.x in cropTopLeftX - circleRadius .. cropTopLeftX + circleRadius
                            && event.y in cropBottomRightY - circleRadius  .. cropBottomRightY + circleRadius -> {
                                moveState = MOVE_STATE_BOTTOM_LEFT
                            }

                        // Bottom right
                        event.x in cropBottomRightX - circleRadius .. cropBottomRightX + circleRadius
                            && event.y in cropBottomRightY - circleRadius .. cropBottomRightY + circleRadius -> {
                                moveState = MOVE_STATE_BOTTOM_RIGHT
                            }

                        event.x in cropTopLeftX .. cropBottomRightX && event.y in cropTopLeftY .. cropBottomRightY -> {
                            with(downRect) {
                                left = cropRect.left
                                top = cropRect.top
                                right = cropRect.right
                                bottom = cropRect.bottom
                            }
                            with(downPoint) {
                                x = event.x
                                y = event.y
                            }
                            moveState = MOVE_STATE_FREE
                        }

                        else -> moveState = MOVE_STATE_NONE
                    }
                }

                ACTION_MOVE -> {
                    if (isTouched) {

                        when (moveState) {

                            MOVE_STATE_TOP_LEFT -> {
                                when {
                                    event.x < bitmapRect.left || event.y < bitmapRect.top -> {
                                        val min = min(
                                            cropRect.right - bitmapRect.left,
                                            cropRect.bottom - bitmapRect.top
                                        )
                                        with(cropRect) {
                                            left = right - min
                                            top = bottom - min
                                        }
                                    }

                                    (event.x + minSize > cropRect.right) || event.y + minSize > cropRect.bottom -> Unit

                                    else -> {
                                        val min = min(
                                            cropRect.right - event.x,
                                            cropRect.bottom - event.y
                                        )
                                        with(cropRect) {
                                            left = right - min
                                            top = bottom - min
                                        }
                                    }
                                }
                            }

                            MOVE_STATE_TOP_RIGHT -> {
                                // Only determine x-axis only
                                var dist = event.x - cropRect.left
                                when {
                                    dist < minSize -> {
                                        dist = cropRect.run { right - left }
                                    }

                                    event.x > bitmapRect.right -> {
                                        dist = bitmapRect.right - cropRect.left
                                        if (cropRect.top + dist > bitmapRect.bottom) {
                                            dist = bitmapRect.bottom - cropRect.top
                                        }
                                    }

                                    else -> if (cropRect.top + dist > bitmapRect.bottom) {
                                        dist = bitmapRect.bottom - cropRect.top
                                    }
                                }
                                with(cropRect) {
                                    right = left + dist
                                    bottom = top + dist
                                }
                            }

                            MOVE_STATE_BOTTOM_LEFT -> {
                                var dist = event.y - cropRect.top
                                when {
                                    dist < minSize -> {
                                        dist = cropRect.run { bottom - top }
                                    }

                                    event.y > bitmapRect.bottom -> {
                                        dist = bitmapRect.bottom - cropRect.top
                                        if (cropRect.left + dist > bitmapRect.right) {
                                            dist = bitmapRect.right - cropRect.left
                                        }
                                    }

                                    else -> if (cropRect.left + dist > bitmapRect.right) {
                                        dist = bitmapRect.right - cropRect.left
                                    }
                                }
                                with(cropRect) {
                                    right = left + dist
                                    bottom = top + dist
                                }
                            }

                            MOVE_STATE_BOTTOM_RIGHT -> {
                                when {
                                    event.x > bitmapRect.right || event.y > bitmapRect.bottom -> {
                                        val min = min(
                                            bitmapRect.right - cropRect.left,
                                            bitmapRect.bottom - cropRect.top
                                        )
                                        with(cropRect) {
                                            right = left + min
                                            bottom = top + min
                                        }
                                    }

                                    event.x - minSize < cropRect.left || event.y - minSize < cropRect.top -> Unit

                                    else -> {
                                        val min = min(
                                            event.x - cropRect.left,
                                            event.y - cropRect.top
                                        )
                                        with(cropRect) {
                                            right = left + min
                                            bottom = top + min
                                        }
                                    }
                                }
                            }

                            MOVE_STATE_FREE -> with(cropRect) {
                                var diff = event.x - downPoint.x
                                val cropWidth = downRect.width()
                                when {
                                    // Move left & not exceed left bound
                                    event.x <= bitmapRect.left || (diff < 0 && downRect.left + diff < bitmapRect.left) -> {
                                        left = bitmapRect.left
                                        right = left + cropWidth
                                    }

                                    // Move right & not exceed right bound
                                    event.x >= bitmapRect.right || (diff > 0 && downRect.right + diff > bitmapRect.right) -> {
                                        right = bitmapRect.right
                                        left = right - cropWidth
                                    }

                                    // Move freely
                                    else -> {
                                        left = downRect.left + diff
                                        right = left + cropWidth
                                    }
                                }

                                diff = event.y - downPoint.y
                                when {
                                    // Move up & not exceed upper bound
                                    event.y <= bitmapRect.top || (diff < 0 && downRect.top + diff < bitmapRect.top) -> {
                                        top = bitmapRect.top
                                        bottom = top + cropWidth
                                    }

                                    // Move down & not exceed bottom bound
                                    event.y >= bitmapRect.bottom || (diff > 0 && downRect.bottom + diff > bitmapRect.bottom) -> {
                                        bottom = bitmapRect.bottom
                                        top = bottom - cropWidth
                                    }

                                    // Move freely
                                    else -> {
                                        top = downRect.top + diff
                                        bottom = top + cropWidth
                                    }
                                }

                            }
                        }
                    }
                }
            }
            invalidate()
            true
        }
    }

    fun setBitmap(bitmap: Bitmap) {
        var bitmapWidth = bitmap.widthF
        var bitmapHeight = bitmap.heightF

        val imageWidth = width - circleRadius * 2
        val imageHeight = height - circleRadius * 2

        when {
            bitmapWidth < bitmapHeight -> {
                bitmapHeight *= imageWidth / bitmapWidth
                bitmapWidth = imageWidth
            }
            else -> {
                bitmapWidth *= imageHeight / bitmapHeight
                bitmapHeight = imageHeight
            }
        }

        // Treat image
        if (bitmapHeight > imageHeight) {
            bitmapWidth *= imageHeight / bitmapHeight
            bitmapHeight = imageHeight
        }
        // Treat image again fitting screen
        if (bitmapWidth > imageWidth) {
            bitmapHeight *= imageWidth / bitmapWidth
            bitmapWidth = imageWidth
        }

        originBitmap = createScaledBitmap(bitmap, bitmapWidth.toInt(), bitmapHeight.toInt(), false)

        with(bitmapRect) {
            when {
                bitmapWidth >= bitmapHeight -> {
                    left = circleRadius
                    top = (height - bitmapHeight) / 2
                    right = circleRadius + bitmapWidth
                    bottom = (height + bitmapHeight) / 2F
                }
                bitmapWidth < bitmapHeight -> {
                    left = (width - bitmapWidth) / 2
                    top = circleRadius
                    right = (width + bitmapWidth) / 2
                    bottom = circleRadius + bitmapHeight
                }
            }
        }

        with(cropRect) {
            when {
                bitmapWidth >= bitmapHeight -> {
                    left = (width - bitmapHeight) / 2
                    top = bitmapRect.top
                    right = (width + bitmapHeight) / 2
                    bottom = bitmapRect.bottom
                }
                bitmapWidth < bitmapHeight -> {
                    left = bitmapRect.left
                    top = (height - bitmapWidth) / 2
                    right = bitmapRect.right
                    bottom = (height + bitmapWidth) / 2
                }
            }
        }
        invalidate()
    }

    val crop get() = originBitmap?.run {
        val bitmapWidth = bitmapRect.width()
        val bitmapHeight = bitmapRect.height()
        createBitmap(
            this,
            (width * (cropRect.left - bitmapRect.left) / bitmapWidth).toInt(),
            (height * (cropRect.top - bitmapRect.top) / bitmapHeight).toInt(),
            (width * cropRect.width() / bitmapWidth).toInt(),
            (height * cropRect.height() / bitmapHeight).toInt()
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return

        val bitmap = originBitmap
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, bitmapRect.left, bitmapRect.top, bitmapPaint)

            val cropTopLeftX = cropRect.left
            val cropTopLeftY = cropRect.top
            val cropBottomRightX = cropRect.right
            val cropBottomRightY = cropRect.bottom
            canvas.drawRect(cropTopLeftX, cropTopLeftY, cropBottomRightX, cropBottomRightY, cropPaint)

            // Top left
            canvas.drawCircle(cropTopLeftX, cropTopLeftY, circleRadius, circlePaint)
            // Top right
            canvas.drawCircle(cropBottomRightX, cropTopLeftY, circleRadius, circlePaint)
            // Bottom left
            canvas.drawCircle(cropTopLeftX, cropBottomRightY, circleRadius, circlePaint)
            // Bottom right
            canvas.drawCircle(cropBottomRightX, cropBottomRightY, circleRadius, circlePaint)
        }
    }

}