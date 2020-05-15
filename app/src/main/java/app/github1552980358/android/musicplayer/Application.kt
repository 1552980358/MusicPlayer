package app.github1552980358.android.musicplayer

import android.util.Log
import lib.github1552980358.labourforce.android.Application

/**
 * @file    : [Application]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/8
 * @time    : 17:07
 **/

class Application : Application() {
    
    override fun onCreate() {
        super.onCreate()
        Log.e("Application", "onCreate")
    }
    
}