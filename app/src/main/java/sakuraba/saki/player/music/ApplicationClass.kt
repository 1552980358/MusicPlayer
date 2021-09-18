package sakuraba.saki.player.music

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log

@Suppress("unused")
class ApplicationClass: Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, extras: Bundle?) {
                Log.e(activity::class.java.simpleName, "onActivityCreated")
            }
    
            override fun onActivityStarted(activity: Activity) {
                Log.e(activity::class.java.simpleName, "onActivityStarted")
            }
    
            override fun onActivityResumed(activity: Activity) {
                Log.e(activity::class.java.simpleName, "onActivityResumed")
            }
    
            override fun onActivityPaused(activity: Activity) {
                Log.e(activity::class.java.simpleName, "onActivityPaused")
            }
    
            override fun onActivityStopped(activity: Activity) {
                Log.e(activity::class.java.simpleName, "onActivityStopped")
            }
    
            override fun onActivitySaveInstanceState(activity: Activity, extras: Bundle) {
                Log.e(activity::class.java.simpleName, "onActivitySaveInstanceState")
            }
    
            override fun onActivityDestroyed(activity: Activity) {
                Log.e(activity::class.java.simpleName, "onActivityDestroyed")
            }
        })
    }
    
}