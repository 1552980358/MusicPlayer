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

}