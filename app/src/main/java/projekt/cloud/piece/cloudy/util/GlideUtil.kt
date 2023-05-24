package projekt.cloud.piece.cloudy.util

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DimenRes
import androidx.annotation.IntegerRes
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory

object GlideUtil {

    /**
     * [GlideUtil.roundCorners]
     * @extends [com.bumptech.glide.RequestBuilder]
     * @param context [android.content.Context]
     * @param resId [Int]
     * @return [com.bumptech.glide.RequestBuilder]
     *
     * Set round corners to image
     **/
    fun <T> RequestBuilder<T>.roundCorners(
        context: Context, @DimenRes resId: Int
    ): RequestBuilder<T> {
        return transform(getRoundCorners(context, resId))
    }

    /**
     * [GlideUtil.getRoundCorners]
     * @param context [android.content.Context]
     * @param resId [Int]
     * @return [com.bumptech.glide.load.resource.bitmap.RoundedCorners]
     **/
    private fun getRoundCorners(context: Context, resId: Int): RoundedCorners {
        return RoundedCorners(
            context.resources.getDimensionPixelSize(resId)
        )
    }

    /**
     * [GlideUtil.crossFade]
     * @extends [com.bumptech.glide.RequestBuilder]<[android.graphics.drawable.Drawable]>
     * @param context [android.content.Context]
     * @param resId [Int]
     * @return [com.bumptech.glide.RequestBuilder]<[android.graphics.drawable.Drawable]>
     *
     * Set cross fade transition
     **/
    fun RequestBuilder<Drawable>.crossFade(
        context: Context, @IntegerRes resId: Int
    ): RequestBuilder<Drawable> {
        return transition(
            getDrawableTransitionOptions(context, resId)
        )
    }

    /**
     * [GlideUtil.getDrawableTransitionOptions]
     * @param context [android.content.Context]
     * @param resId [Int]
     * @return [com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions]
     *
     * Create and return [com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions]
     **/
    private fun getDrawableTransitionOptions(
        context: Context, @IntegerRes resId: Int
    ): DrawableTransitionOptions {
        return DrawableTransitionOptions.withCrossFade(
            buildDrawableCrossFadeFactory(context, resId)
        )
    }

    /**
     * [GlideUtil.buildDrawableCrossFadeFactory]
     * @param context [android.content.Context]
     * @param resId [Int]
     * @return [com.bumptech.glide.request.transition.DrawableCrossFadeFactory]
     *
     * Build [com.bumptech.glide.request.transition.DrawableCrossFadeFactory]
     **/
    private fun buildDrawableCrossFadeFactory(
        context: Context, @IntegerRes resId: Int
    ): DrawableCrossFadeFactory {
        return DrawableCrossFadeFactory.Builder(context.resources.getInteger(resId))
            .setCrossFadeEnabled(true)
            .build()
    }

}