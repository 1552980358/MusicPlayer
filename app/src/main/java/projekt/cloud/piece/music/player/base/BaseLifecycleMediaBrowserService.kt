package projekt.cloud.piece.music.player.base

import android.content.Intent
import android.os.IBinder
import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.media.MediaBrowserServiceCompat

abstract class BaseLifecycleMediaBrowserService: MediaBrowserServiceCompat(), LifecycleOwner {

    @Suppress("LeakingThis")
    private val serviceLifecycleDispatcher = ServiceLifecycleDispatcher(this)

    @CallSuper
    override fun onCreate() {
        serviceLifecycleDispatcher.onServicePreSuperOnCreate()
        super.onCreate()
    }

    @Suppress("DEPRECATION")
    @Deprecated("Implement onStartCommand(Intent, int, int) instead.")
    @CallSuper
    override fun onStart(intent: Intent?, startId: Int) {
        serviceLifecycleDispatcher.onServicePreSuperOnStart()
        super.onStart(intent, startId)
    }

    // this method is added only to annotate it with @CallSuper.
    // In usual service super.onStartCommand is no-op, but in LifecycleService
    // it results in mDispatcher.onServicePreSuperOnStart() call, because
    // super.onStartCommand calls onStart().
    @CallSuper
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    @CallSuper
    override fun onBind(intent: Intent?): IBinder? {
        serviceLifecycleDispatcher.onServicePreSuperOnBind()
        return super.onBind(intent)
    }

    @CallSuper
    override fun onDestroy() {
        serviceLifecycleDispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()
    }

    override val lifecycle: Lifecycle
        get() = serviceLifecycleDispatcher.lifecycle

}