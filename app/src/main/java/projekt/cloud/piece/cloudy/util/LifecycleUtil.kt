package projekt.cloud.piece.cloudy.util

import android.app.Activity
import android.app.Service
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner

object LifecycleUtil {

    /**
     * [androidx.lifecycle.LifecycleOwner.requireContext]
     * @return [android.content.Context]
     *
     * Require a non-null [android.content.Context] instance
     **/
    fun LifecycleOwner.requireContext(): Context {
        return context ?: throw IllegalAccessException("LifecycleOwner should be extended to Fragment, Activity or Service.")
    }

    /**
     * [androidx.lifecycle.LifecycleOwner.context]
     * @return [android.content.Context]
     *
     * Require a nullable [android.content.Context] instance
     * if [androidx.lifecycle.LifecycleOwner] is not extended
     * to a [android.content.Context] obtainable class
     **/
    val LifecycleOwner.context: Context?
        get() = when (this) {
            is Fragment -> requireContext()
            is Activity -> this
            is Service -> this
            else -> null
        }

}