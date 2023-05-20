package projekt.cloud.piece.cloudy.base

import android.content.Intent
import android.os.IBinder
import android.support.v4.media.MediaBrowserCompat.MediaItem
import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.media.MediaBrowserServiceCompat

abstract class BaseLifecycleMediaBrowserService: MediaBrowserServiceCompat(), LifecycleOwner {

    /**
     * [BaseLifecycleMediaBrowserService.lifecycleDispatcher]
     * @type [androidx.lifecycle.ServiceLifecycleDispatcher]
     **/
    @Suppress("LeakingThis")
    private val lifecycleDispatcher = ServiceLifecycleDispatcher(this)

    /**
     * [android.app.Service.onCreate]
     **/
    @CallSuper
    override fun onCreate() {
        lifecycleDispatcher.onServicePreSuperOnCreate()
        super.onCreate()
    }

    /**
     * [android.app.Service.onCreate]
     * @param intent [android.content.Intent]
     * @return [android.os.IBinder]
     **/
    @CallSuper
    override fun onBind(intent: Intent?): IBinder? {
        lifecycleDispatcher.onServicePreSuperOnBind()
        return super.onBind(intent)
    }

    /**
     * [android.app.Service.onStart]
     * @param intent [android.content.Intent]
     * @param startId [Int]
     **/
    @Deprecated("Implement onStartCommand(Intent, int, int) instead.")
    @CallSuper
    override fun onStart(intent: Intent?, startId: Int) {
        lifecycleDispatcher.onServicePreSuperOnStart()
        @Suppress("Deprecation")
        super.onStart(intent, startId)
    }

    /**
     * [androidx.media.MediaBrowserServiceCompat.onLoadChildren]
     * @param parentId [String]
     * @param result [androidx.media.MediaBrowserServiceCompat.Result]
     **/
    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaItem>>) {
        result.sendResult(null)
    }

    /**
     * [android.app.Service.onDestroy]
     **/
    @CallSuper
    override fun onDestroy() {
        lifecycleDispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()
    }

    /**
     * [androidx.lifecycle.LifecycleOwner.getLifecycle]
     * @return [androidx.lifecycle.Lifecycle]
     **/
    override fun getLifecycle(): Lifecycle {
        return lifecycleDispatcher.lifecycle
    }

}