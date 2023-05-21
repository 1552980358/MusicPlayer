package projekt.cloud.piece.cloudy.base

import android.content.Intent
import android.os.IBinder
import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.media3.session.MediaLibraryService

abstract class BaseLifecycleMediaLibraryService: MediaLibraryService(), LifecycleOwner {

    @Suppress("LeakingThis")
    private val serviceLifecycleDispatcher = ServiceLifecycleDispatcher(this)

    /**
     * [android.app.Service.onCreate]
     **/
    @CallSuper
    override fun onCreate() {
        serviceLifecycleDispatcher.onServicePreSuperOnCreate()
        super.onCreate()
    }

    /**
     * [android.app.Service.onCreate]
     * @param intent [android.content.Intent]
     * @return [android.os.IBinder]
     **/
    @CallSuper
    override fun onBind(intent: Intent?): IBinder? {
        serviceLifecycleDispatcher.onServicePreSuperOnBind()
        return super.onBind(intent)
    }

    /**
     * [android.app.Service.onStart]
     * @param intent [android.content.Intent]
     * @param startId [Int]
     **/
    @Deprecated("Deprecated: Implement onStartCommand(Intent, int, int) instead.")
    @CallSuper
    override fun onStart(intent: Intent?, startId: Int) {
        serviceLifecycleDispatcher.onServicePreSuperOnStart()
        @Suppress("DEPRECATION")
        super.onStart(intent, startId)
    }

    /**
     * [android.app.Service.onDestroy]
     **/
    @CallSuper
    override fun onDestroy() {
        serviceLifecycleDispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()
    }

    /**
     * [androidx.lifecycle.LifecycleOwner.getLifecycle]
     * @return [androidx.lifecycle.Lifecycle]
     **/
    override fun getLifecycle(): Lifecycle {
        return serviceLifecycleDispatcher.lifecycle
    }

}