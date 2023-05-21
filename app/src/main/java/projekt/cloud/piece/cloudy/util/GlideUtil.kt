package projekt.cloud.piece.cloudy.util

import android.content.Context
import androidx.annotation.DimenRes
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

object GlideUtil {

    fun <T> RequestBuilder<T>.roundCorners(
        context: Context, @DimenRes resId: Int
    ): RequestBuilder<T> {
        return transform(getRoundCorners(context, resId))
    }

    private fun getRoundCorners(context: Context, resId: Int): RoundedCorners {
        return RoundedCorners(
            context.resources.getDimensionPixelSize(resId)
        )
    }

}