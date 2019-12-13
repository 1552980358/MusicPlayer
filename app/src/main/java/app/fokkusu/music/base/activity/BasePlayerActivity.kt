package app.fokkusu.music.base.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import app.fokkusu.music.base.Constants.Companion.SERVICE_BROADCAST_BITMAP_CONTENT
import app.fokkusu.music.base.Constants.Companion.SERVICE_BROADCAST_BITMAP_RESULT
import app.fokkusu.music.base.Constants.Companion.SERVICE_BROADCAST_CHANGED
import app.fokkusu.music.base.Constants.Companion.SERVICE_BROADCAST_PAUSE
import app.fokkusu.music.base.Constants.Companion.SERVICE_BROADCAST_PLAY
import app.fokkusu.music.base.getStack
import app.fokkusu.music.service.PlayService
import java.lang.Exception
import app.fokkusu.music.R

/**
 * @File    : BasePlayerActivity
 * @Author  : 1552980358
 * @Date    : 2019/11/29
 * @TIME    : 18:07
 **/

@SuppressLint("Registered")
open class BasePlayerActivity : AppCompatActivity() {
    
    /* Thread and flag */
    protected var timeCount: Thread? = null
    protected var threadStop = false
    
    private val broadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent ?: return
                when (intent.action) {
                    SERVICE_BROADCAST_BITMAP_RESULT -> {
                        intent.getBooleanExtra(SERVICE_BROADCAST_BITMAP_CONTENT, false).apply {
                            if (!this) {
                                onNullResult()
                                return
                            }
    
                            PlayService.getCurrentBitmap().apply {
                                if (this == null) {
                                    onNullResult()
                                    return
                                }
                                onResult(this)
                            }
                        }
                    }
    
                    SERVICE_BROADCAST_PLAY -> {
                        onReceivePlay()
                    }
                    
                    SERVICE_BROADCAST_PAUSE -> {
                        onReceivePause()
                    }
    
                    SERVICE_BROADCAST_CHANGED -> {
                        onReceiveChange()
                    }
                }
            }
        }
    }
    
    private val intentFilter by lazy {
        IntentFilter().apply {
            addAction(SERVICE_BROADCAST_BITMAP_RESULT)
            addAction(SERVICE_BROADCAST_PLAY)
            addAction(SERVICE_BROADCAST_PAUSE)
            addAction(SERVICE_BROADCAST_CHANGED)
        }
    }
    
    /* onResult */
    /* Need to be override when inheriting PlayerActivity */
    open fun onResult(bitmap: Bitmap) {
    }
    
    /* onNullResult */
    /* Need to be override when inheriting PlayerActivity */
    open fun onNullResult() {
    }
    
    /* onReceivePlay */
    /* Need to be override when inheriting PlayerActivity */
    open fun onReceivePlay() {
    }
    
    /* onReceivePause */
    /* Need to be override when inheriting PlayerActivity */
    open fun onReceivePause() {
    }
    
    /* onReceiveChange */
    /* Need to be override when inheriting PlayerActivity */
    open fun onReceiveChange() {
        changeMusic()
    }
    
    @Synchronized
    open fun changeMusic(onResume: Boolean = false) {
    }
    
    @Synchronized
    open fun getThreadStart() {
    }
    
    /* onResume */
    override fun onResume() {
        super.onResume()
        registerReceiver(broadcastReceiver, intentFilter)
        changeMusic(true)
        getThreadStart()
    }
    
    /* onPause */
    override fun onPause() {
        super.onPause()
        
        // Stop Thread
        threadStop = true
        timeCount = null
        
        unregisterReceiver(broadcastReceiver)
        
        System.gc()
    }
    
    /* onBackPressed */
    override fun onBackPressed() {
        finish()
    }
    
    /* finish */
    override fun finish() {
        // Stop Thread
        threadStop = true
        timeCount = null
    
        super.finish()
        overridePendingTransition(R.anim.anim_stay, R.anim.anim_top2bottom)
    }
    
    /* onDestroy */
    override fun onDestroy() {
        // make sure that sub-thread is removed
        timeCount = null
        
        try {
            unregisterReceiver(broadcastReceiver)
        } catch (e: Exception) {
            e.getStack(showLog = false, showToast = false)
        }
        super.onDestroy()
    }
    
    
}