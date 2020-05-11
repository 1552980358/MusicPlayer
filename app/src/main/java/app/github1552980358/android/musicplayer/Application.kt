package app.github1552980358.android.musicplayer

import android.content.Intent
import android.util.Log
import app.github1552980358.android.musicplayer.base.Constant
import app.github1552980358.android.musicplayer.service.PlayService
import lib.github1552980358.labourforce.LabourForce
import lib.github1552980358.labourforce.android.Application
import lib.github1552980358.labourforce.commands.LabourLv

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
    
        LabourForce.onDuty.employLabour(Constant.BackgroundThread, LabourLv.Mid)
    
        startService(Intent(this, PlayService::class.java))
        
    }
    
}