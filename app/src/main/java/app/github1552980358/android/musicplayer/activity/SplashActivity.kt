package app.github1552980358.android.musicplayer.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.base.AudioData
import app.github1552980358.android.musicplayer.base.Constant.Companion.AudioDataDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.AudioDataListFile
import app.github1552980358.android.musicplayer.base.Constant.Companion.AudioDataMapFile
import app.github1552980358.android.musicplayer.base.Constant.Companion.BackgroundThread
import app.github1552980358.android.musicplayer.base.Constant.Companion.IgnoredFile
import app.github1552980358.android.musicplayer.service.PlayService
import lib.github1552980358.labourforce.LabourForce
import lib.github1552980358.labourforce.commands.LabourLv
import lib.github1552980358.labourforce.labours.work.LabourWork
import java.io.File
import java.io.ObjectInputStream

/**
 * @file    : SplashActivity
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/9
 * @time    : 12:00
 **/

class SplashActivity : AppCompatActivity() {
    
    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.MODIFY_AUDIO_SETTINGS,
        Manifest.permission.RECORD_AUDIO
    )
    
    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        
        super.onCreate(savedInstanceState)
        Log.e("SplashActivity", "onCreate")
        setContentView(R.layout.activity_splash)
    
        startService(Intent(this, PlayService::class.java))
        
        for (i in permissions) {
            if (ContextCompat.checkSelfPermission(this, i) == PackageManager.PERMISSION_GRANTED)
                continue
            ActivityCompat.requestPermissions(this, permissions, 0)
            return
        }
        
        loadMedia()
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (i in grantResults) {
            if (i != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 0)
                return
            }
        }
        loadMedia()
    }
    
    private fun loadMedia() {
        LabourForce.onDuty
            .employLabour(BackgroundThread, LabourLv.Mid)
            .sendWork2Labour(BackgroundThread, object : LabourWork(Handler()) {
                override fun dutyEnd(workProduct: MutableMap<String, Any?>?, handler: Handler?) {
                    handler!!.post {
                        //startService(Intent(this@SplashActivity, PlayService::class.java))
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    }
                }
                
                override fun workContent(workProduct: MutableMap<String, Any?>?, handler: Handler?) {
                    Log.e("BGT", "WorkContent")
                    File(getExternalFilesDir(AudioDataDir), AudioDataListFile).apply {
                        if (!exists())
                            return
                        
                        // Import to application
                        // 载入App
                        inputStream().use {
                            ObjectInputStream(it).use { ois ->
                                @Suppress("UNCHECKED_CAST")
                                AudioData.audioDataList = ois.readObject() as ArrayList<AudioData>
                            }
                        }
                        
                    }
                    
                    File(getExternalFilesDir(AudioDataDir), AudioDataMapFile).apply {
                        if (!exists())
                            return
    
                        inputStream().use {
                            ObjectInputStream(it).use { ois ->
                                @Suppress("UNCHECKED_CAST")
                                AudioData.audioDataMap = (ois.readObject() as MutableMap<String, AudioData>)
                            }
                        }
                    }
                    
                    File(getExternalFilesDir(AudioDataDir), IgnoredFile).apply {
                        if (!exists())
                            return
                        
                        AudioData.ignoredData = readLines().toMutableList() as ArrayList<String>
                    }
                    
                }
                
                override fun workDone(workProduct: MutableMap<String, Any?>?, handler: Handler?) {
                    Log.e("BGT", "WorkDone")
                    
                    handler!!.post {
                        //startService(Intent(this@SplashActivity, PlayService::class.java))
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    }
                }
                
                override fun workFail(e: Exception, workProduct: MutableMap<String, Any?>?, handler: Handler?) {
                    Log.e("BGT", "WorkDone")
                    
                    handler!!.post {
                        //startService(Intent(this@SplashActivity, PlayService::class.java))
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    }
                }
                
            })
        
    }
    
}