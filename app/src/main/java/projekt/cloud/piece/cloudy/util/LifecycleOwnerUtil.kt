package projekt.cloud.piece.cloudy.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import projekt.cloud.piece.cloudy.util.CoroutineUtil.default
import projekt.cloud.piece.cloudy.util.CoroutineUtil.io
import projekt.cloud.piece.cloudy.util.CoroutineUtil.main

object LifecycleOwnerUtil {

    fun LifecycleOwner.main(block: SuspendScopedBlock): Job {
        return lifecycleScope.main(block)
    }
    fun LifecycleOwner.default(block: SuspendScopedBlock): Job {
        return lifecycleScope.default(block)
    }
    fun LifecycleOwner.io(block: SuspendScopedBlock): Job {
        return lifecycleScope.io(block)
    }

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